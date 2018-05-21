package com.springrain.erp.common.email;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Quota;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FromStringTerm;
import javax.mail.search.SearchTerm;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.custom.entity.CustomEmail;
import com.springrain.erp.modules.custom.scheduler.CustomEmailManager;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

/**
 * 邮件管理器，支持pop3/imap协议。
 * 能够接收文本、HTML和带有附件的邮件
 */
@Component
public class MailManager {
    // 收邮件的参数配置
    private MailManagerInfo managerInfo;
    // 与邮件服务器连接后得到的邮箱
    private Store store;
    
    private Store imapStore;
    
    // 收件箱
    private Folder folder;
    
    // 收件箱 Imap
    private IMAPFolder folderByImap;
    
    // 当前正在处理的邮件消息
    private Message currentMessage;

    private Session session;
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    public MailManager(MailManagerInfo receiverInfo) {
        this.managerInfo = receiverInfo;
    }
    
    public MailManager() {
    	managerInfo = new MailManagerInfo();
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        managerInfo.getProperties().setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        managerInfo.getProperties().setProperty("mail.smtp.socketFactory.fallback", "false");
        
        managerInfo.getProperties().setProperty("mail.smtp.port", "465");
        managerInfo.getProperties().setProperty("mail.smtp.socketFactory.port", "465");
        
        managerInfo.getProperties().setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
        managerInfo.getProperties().setProperty("mail.pop3.port", "995");
        managerInfo.getProperties().setProperty("mail.pop3.socketFactory.port", "995");
       
        managerInfo.getProperties().setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
        managerInfo.getProperties().setProperty("mail.imap.port", "993");
        managerInfo.getProperties().setProperty("mail.imap.socketFactory.port", "993");
    }
    
    public void setManagerInfo(MailManagerInfo managerInfo) {
		this.managerInfo = managerInfo;
	}

	public MailManagerInfo getManagerInfo() {
		return managerInfo;
	}

	/**
     * 收邮件
     */
    public List<Message> receiveMail(int expired) throws Exception{
        if (this.managerInfo == null){
            throw new Exception("必须提供接收邮件的参数！");
        }
        // 连接到服务器
        if (this.connectToServer()) {
            // 打开收件箱
            if (this.openFirstInBoxFolder()) {
                // 获取所有邮件
                return this.getMail(expired);
            } else {
                throw new Exception("打开收件箱失败！");
            }
        } else {
            throw new Exception("连接邮件服务器失败！");
        }
    }
    
    private static class MyAuthenticator extends Authenticator {//认证

		private String m_username = null;

		private String m_userpass = null;

		public MyAuthenticator(String username, String userpass) {
			super();
			setUsername(username);
			setUserpass(userpass);
		}

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(m_username, m_userpass);
		}

		public void setUsername(String username) {
			m_username = username;
		}

		public void setUserpass(String userpass) {
			m_userpass = userpass;
		}
	}
    
    
    /**
     * 登陆邮件服务器
     */
    private boolean connectToServer() {
    	if(session==null||store==null){
	        // 判断是否需要身份认证
    		System.setProperty("mail.mime.multipart.ignoreexistingboundaryparameter", "true");
	        MyAuthenticator authenticator = null;
	        if (this.managerInfo.isValidate()) {
	            // 如果需要身份认证，则创建一个密码验证器
	            authenticator = new MyAuthenticator(this.managerInfo.getUserName(),
	                    this.managerInfo.getPassword());
	        }
	        //创建session
	        session = Session.getInstance(this.managerInfo
	                .getProperties(), authenticator);
	
	        //创建store,建立连接
	        try {
	            this.store = session.getStore(this.managerInfo.getProtocal());
	        } catch (NoSuchProviderException e) {
	            System.out.println("连接服务器失败！");
	            logger.warn("连接服务器失败！",e);
	            session = null;
	            return false;
	        }
	        System.out.println("connecting");
	        if(!"mail1.inateck.com".equals(this.managerInfo.getMailSmtpHost())){
		        if(!this.store.isConnected()){
			        try {
			            this.store.connect();
			        } catch (MessagingException e) {
			        	logger.warn("连接服务器失败！",e);
			            session = null;
			            return false;
			        }
		        }
	        }
    	}
        return true;
    }
    
    /**
     * 登陆邮件服务器
     */
    private boolean connectToServerByImap() {
    	if(session==null){
	        // 判断是否需要身份认证
    		System.setProperty("mail.mime.multipart.ignoreexistingboundaryparameter", "true");
	        MyAuthenticator authenticator = null;
	        if (this.managerInfo.isValidate()) {
	            // 如果需要身份认证，则创建一个密码验证器
	            authenticator = new MyAuthenticator(this.managerInfo.getUserName(),
	                    this.managerInfo.getPassword());
	        }
	        //创建session
	        session = Session.getInstance(this.managerInfo
	                .getProperties(), authenticator);
    	}
        return true;
    }
    
    private boolean openFirstInBoxFolder() {
    	try{
	    	if (connectToServerByImap()) {
					this.imapStore = (IMAPStore) session.getStore("imap"); // 使用imap会话机制，连接服务器
					if(!imapStore.isConnected()){
						try{
							imapStore.connect();
						}catch(Exception e){
							logger.warn("监控垃圾箱连接服务器失败!!",e);
							return false;
						}
					}
					folderByImap = (IMAPFolder) imapStore.getFolder("INBOX"); // 收件箱
					folderByImap.open(Folder.READ_WRITE);
		    	return true;
		    }
    	}catch(Exception e){
    		logger.error("登陆失败",e);
    	}
    	return false;
    }
    
    
    /**
     * 打开收件箱
     */
    private boolean openInBoxFolder() {
    	try{
	    	if(folderByImap == null){
	    		if(session==null){
	    			connectToServerByImap();
	    		}
	    		this.imapStore = (IMAPStore) session.getStore("imap"); // 使用imap会话机制，连接服务器
				if(!imapStore.isConnected()){
					try{
						imapStore.connect();
					}catch(Exception e){
						logger.warn("监控垃圾箱连接服务器失败!!");
						return false;
					}
				}
				folderByImap = (IMAPFolder) imapStore.getFolder("INBOX"); // 收件箱
				folderByImap.open(Folder.READ_WRITE);
	    	} 
	    	if(!folderByImap.isOpen()){
	    		folderByImap.open(Folder.READ_ONLY);
	    	}
    	} catch (MessagingException e) {
    		logger.error("打开邮箱失败:"+e.getMessage());
            return false;
	    }
    	return true;
    }
    /**
     * 断开与邮件服务器的连接
     */
    public boolean closeConnection() {
        try {
            if (this.folder.isOpen()) {
                this.folder.close(true);
            }
            this.store.close();
            this.session = null;
            System.out.println("成功关闭与邮件服务器的连接！");
            return true;
        } catch (Exception e) {
            System.out.println("关闭和邮件服务器之间连接时出错！");
        }
        return false;
    }
    
    public void clearConnection() {
    	this.folder = null;
    	this.session = null;
    	this.store = null;
    }
    
    /**
     * 获取messages中的所有邮件
     * @throws MessagingException 
     */
    private List<Message> getMail(int expired) throws MessagingException{
    	//先移动垃圾箱
    	try{
    		IMAPFolder lajifolder = (IMAPFolder) imapStore.getFolder("Junk");
	    	
			if(lajifolder.isOpen()){
				Message[] msgs = lajifolder.getMessages();
				// 获取总邮件数
				if(msgs.length>0){
					lajifolder.copyMessages(msgs, folderByImap);
				}
			}
    	}catch(Exception e){
    		logger.warn("移动垃圾邮箱失败！！！",e);
    	}
        //从邮件文件夹获取邮件信息
    	int flag = 0 ;
    	int mailCount = this.folderByImap.getMessageCount();
    	int num = mailCount/100;
    	if(num<8){
    		num = 8;
    	}else if(num>20){
    		num = 20;
    	}
    	List<Message> rs = Lists.newArrayList();
    	int j = 0;
    	Date today = new Date();
    	while(flag == 0){
    		int start = mailCount-num*(j+1);
    		int end = mailCount-(num*j);
    		if(start<=0){
    			start = 1;
    		}
    		if(end<=1){
    			break;
    		}
    		Message[] msgs = this.folderByImap.getMessages(start,end);
    		for (int i = msgs.length-1; i >=0; i--) {
        		Message message = msgs[i];
        		try {
    				Date date = message.getSentDate();
    				if (date == null) {
    					date = new Date();
    					date = DateUtils.addHours(date, -50);
    					logger.warn(getSubject(message)+"该邮件发送时间为空，手动设置时间");
					}
					boolean isNotExpired = DateUtils.addMinutes(date,expired).after(today);
					if(isNotExpired){
						rs.add(message);
					}else{
						if(j>=1){
							flag = 1;
							break;
						}else{
							rs.add(message);
						}
					}
    			} catch (Exception e) {
    				logger.error(message.getSubject()+"::出错了",e);
    				rs.add(message);
    			} 
    		}
    		j++;
    	}
        return rs;
    }

    /**
     * 显示邮件的基本信息
     */
    private void showMailBasicInfo() throws Exception{
        showMailBasicInfo(this.currentMessage);
    }
    private void showMailBasicInfo(Message message) throws Exception {
        System.out.println("-------- 邮件ID：" + this.getMessageId()
                + " ---------");
        System.out.println("From：" + this.getFrom());
        System.out.println("To：" + this.getTOAddress());
        System.out.println("CC：" + this.getCCAddress());
        System.out.println("BCC：" + this.getBCCAddress());
        System.out.println("Subject：" + this.getSubject());
        System.out.println("发送时间：：" + this.getSentDate());
        System.out.println("是新邮件？" + this.isNew());
        System.out.println("要求回执？" + this.getReplySign());
        System.out.println("包含附件？" + this.isContainAttach());
        System.out.println("------------------------------");
    }

    /**
     * 获得邮件的收件人，抄送，和密送的地址和姓名，根据所传递的参数的不同 
     * "to"----收件人 "cc"---抄送人地址 "bcc"---密送人地址
     */
    private String getTOAddress() throws Exception {
        return getMailAddress("TO", this.currentMessage);
    }

    private String getCCAddress() throws Exception {
        return getMailAddress("CC", this.currentMessage);
    }

    private String getBCCAddress() throws Exception {
        return getMailAddress("BCC", this.currentMessage);
    }
    
    /**
     * 获得邮件的优先级
     * @param msg 邮件内容
     * @return 1(High):紧急  3:普通(Normal)  5:低(Low)
     * @throws MessagingException 
     */ 
    public String getPriority(Message msg) throws MessagingException { 
    	String priority = "0"; 
    	if (this.openInBoxFolder()) {
	        String[] headers = msg.getHeader("X-Priority"); 
	        if (headers != null) { 
	            String headerPriority = headers[0]; 
	            if (headerPriority.indexOf("1") != -1 || headerPriority.indexOf("High") != -1){
	                priority = "1"; 
	            }else if (headerPriority.indexOf("5") != -1 || headerPriority.indexOf("Low") != -1){ 
	                priority = "0"; 
	            }else{ 
	                priority = "0";
	            }    
	        } 
    	}
        return priority; 
    }  
    
    /**
     * 获得邮件地址
     * @param type        类型，如收件人、抄送人、密送人
     * @param mimeMessage    邮件消息
     * @return
     * @throws Exception
     */
    public String getMailAddress(String type, Message mimeMessage)
            throws Exception {
        String mailaddr = "";
        String addtype = type.toUpperCase();
        InternetAddress[] address = null;
        if (addtype.equals("TO") || addtype.equals("CC")
                || addtype.equals("BCC")) {
            if (addtype.equals("TO")) {
                address = (InternetAddress[]) mimeMessage
                        .getRecipients(Message.RecipientType.TO);
            } else if (addtype.equals("CC")) {
                address = (InternetAddress[]) mimeMessage
                        .getRecipients(Message.RecipientType.CC);
            } else {
                address = (InternetAddress[]) mimeMessage
                        .getRecipients(Message.RecipientType.BCC);
            }
            if (address != null) {
                for (int i = 0; i < address.length; i++) {
                    // 先获取邮件地址
                    String email = address[i].getAddress();
                    if (email == null){
                        email = "";
                    }else {
                        email = MimeUtility.decodeText(email);
                    }
                    // 再取得个人描述信息
                    String personal = address[i].getPersonal();
                    if (personal == null){
                        personal = "";
                    } else {
                        personal = MimeUtility.decodeText(personal);
                    }
                    // 将个人描述信息与邮件地址连起来
                    String compositeto = personal + "<" + email + ">";
                    // 多个地址时，用逗号分开
                    mailaddr += "," + compositeto;
                }
                mailaddr = mailaddr.substring(1);
            }
        } else {
            throw new Exception("错误的地址类型！!");
        }
        return mailaddr;
    }

    /**
     * 获得发件人的地址和姓名
     * @throws Exception
     */
    private String getFrom() throws Exception {
    	if (this.openInBoxFolder()) {
    		return getFrom(this.currentMessage);
    	}
    	return null;
    }

    public static String getFrom(Message mimeMessage) throws Exception {
        InternetAddress[] address = (InternetAddress[]) mimeMessage.getFrom();
        // 获得发件人的邮箱
        String from = address[0].getAddress();
        if (from == null){
            from = "";
        }
        return from;
    }
    
    /**
     * 获得邮件主题
     */
    private String getSubject() throws MessagingException {
        return getSubject(this.currentMessage);
    }

    public String getSubject(Message mimeMessage) throws MessagingException {
    	if (this.openInBoxFolder()) {
	        String subject = "";
	        try {
	            // 将邮件主题解码
	            subject = MimeUtility.decodeText(mimeMessage.getSubject());
	            if (subject == null||subject.length()==0){
	                subject = "无主题";
	            }
	        } catch (Exception exce) {
	        }
	        return subject;
    	}
    	return null;
    }

    /**
     * 获得邮件发送日期
     */
    private Date getSentDate() throws Exception {
    	if (this.openInBoxFolder()) {
    		return getSentDate(this.currentMessage);
    	}	
    	return null;
    }

    public Date getSentDate(Message mimeMessage) throws Exception {
    	if (this.openInBoxFolder()) {
    		return mimeMessage.getSentDate();
    	}
    	return null;
    }

    /**
     * 判断此邮件是否需要回执，如果需要回执返回"true",否则返回"false"
     */
    private boolean getReplySign() throws MessagingException {
        return getReplySign(this.currentMessage);
    }

    private boolean getReplySign(Message mimeMessage) throws MessagingException {
        boolean replysign = false;
        String needreply[] = mimeMessage
                .getHeader("Disposition-Notification-To");
        if (needreply != null) {
            replysign = true;
        }
        return replysign;
    }

    /**
     * 获得此邮件的Message-ID
     */
    private String getMessageId() throws MessagingException {
        return getMessageId(this.currentMessage);
    }

    public String getMessageId(Message mimeMessage) throws MessagingException {
    	if (this.openInBoxFolder()) {
    		return ((MimeMessage) mimeMessage).getMessageID();
    	}
    	return null;
    }

    /**
     * 判断此邮件是否已读，如果未读返回返回false,反之返回true
     */
    private boolean isNew() throws MessagingException {
        return isNew(this.currentMessage);
    }
    public boolean isNew(Message mimeMessage) throws MessagingException {
        boolean isnew = false;
        Flags flags = mimeMessage.getFlags();
        Flags.Flag[] flag = flags.getSystemFlags();
        for (int i = 0; i < flag.length; i++) {
            if (flag[i] == Flags.Flag.SEEN) {
                isnew = true;
                break;
            }
        }
        return isnew;
    }

    /**
     * 判断此邮件是否包含附件
     */
    private boolean isContainAttach() throws Exception {
        return isContainAttach(this.currentMessage);
    }
    private boolean isContainAttach(Part part) throws Exception {
        boolean attachflag = false;
        if (part.isMimeType("multipart/*")) {
            // 如果邮件体包含多部分
            Multipart mp = (Multipart) part.getContent();
            // 遍历每部分
            for (int i = 0; i < mp.getCount(); i++) {
                // 获得每部分的主体
                BodyPart bodyPart = mp.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                if ((disposition != null)
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition
                                .equals(Part.INLINE)))){
                    attachflag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    attachflag = isContainAttach((Part) bodyPart);
                } else {
                    String contype = bodyPart.getContentType();
                    if (contype.toLowerCase().indexOf("application") != -1){
                        attachflag = true;
                    }
                    if (contype.toLowerCase().indexOf("name") != -1){
                        attachflag = true;
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            attachflag = isContainAttach((Part) part.getContent());
        }
        return attachflag;
    }

    
    /**
     * 获得当前邮件
     */
    public void getMail(Message message,CustomEmail customEmail) throws Exception {
        try {
            //this.saveMessageAsFile();
            this.parseMessage(message,customEmail);
        } catch (IOException e) {
            throw new IOException("保存邮件出错，检查保存路径"+e.getMessage(),e);
        } catch (MessagingException e) {
            throw new MessagingException("邮件转换出错"+e.getMessage(),e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("未知错误"+e.getMessage(),e);
        }
    }
    
    /**
     * 保存邮件源文件
     */
    private void saveMessageAsFile(Message message) {
        try {
            // 将邮件的ID中尖括号中的部分做为邮件的文件名
            String oriFileName = getInfoBetweenBrackets(this.getMessageId(message)
                    .toString());
            //设置文件后缀名。若是附件则设法取得其文件后缀名作为将要保存文件的后缀名，
            //若是正文部分则用.htm做后缀名
            String emlName = oriFileName;
            String fileNameWidthExtension = this.managerInfo.getEmailDir()
                    + oriFileName + this.managerInfo.getEmailFileSuffix();
            File storeFile = new File(fileNameWidthExtension);
            for (int i = 0; storeFile.exists(); i++) {
                emlName = oriFileName + i;
                fileNameWidthExtension = this.managerInfo.getEmailDir()
                        + emlName + this.managerInfo.getEmailFileSuffix();
                storeFile = new File(fileNameWidthExtension);
            }
            System.out.println("邮件消息的存储路径: " + fileNameWidthExtension);
            // 将邮件消息的内容写入ByteArrayOutputStream流中
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            message.writeTo(baos);
            // 读取邮件消息流中的数据
            /*StringReader in = new StringReader(baos.toString());
            // 存储到文件
            saveFile(fileNameWidthExtension, in);*/
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 解析邮件
     */
    private void parseMessage(Message message,CustomEmail customEmail) throws IOException,
            MessagingException {
        Object content = message.getContent();
        // 处理多部分邮件
        if (this.openInBoxFolder()) {
	        if (content instanceof Multipart) {
	            handleMultipart((Multipart) content,customEmail);
	        } else {
	            handlePart(message,customEmail);
	        }
        }
    }

    /*
     * 解析Multipart
     */
    private void handleMultipart(Multipart multipart,CustomEmail customEmail) throws MessagingException,
            IOException {
        for (int i = 0, n = multipart.getCount(); i < n; i++) {
            handlePart(multipart.getBodyPart(i),customEmail);
        }
    }
    
    public void handlePart(Part part ,CustomEmail customEmail) throws MessagingException, IOException{  
    	String disposition = part.getDisposition();
        String contenttype = part.getContentType();   
        int nameindex = contenttype.toLowerCase().indexOf("name");
        boolean hasAtt = false;
        if (contenttype.toLowerCase().indexOf("application") != -1){
        	hasAtt = true;
        }
        boolean conname = false;   
        if (nameindex != -1)   
            conname = true;   
        System.out.println("CONTENTTYPE: " + contenttype);
        if(disposition == null && !hasAtt){
	        if (part.isMimeType("text/plain") && !conname) {   
	        	 customEmail.setReceiveContent("<pre>"+(String) part.getContent()+"</pre>");  
	        } else if (part.isMimeType("text/html") && !conname) {   
	        	 customEmail.setReceiveContent((String) part.getContent());   
	        } else if (part.isMimeType("multipart/*")) {   
	            Multipart multipart = (Multipart) part.getContent();   
	            int counts = multipart.getCount();   
	            for (int i = 0; i < counts; i++) {   
	            	handlePart(multipart.getBodyPart(i),customEmail);   
	            }   
	        } else if (part.isMimeType("message/rfc822")) {   
	        	handlePart((Part) part.getContent(),customEmail);   
	        }else{}
	        return;
        }
        String fileNameWidthExtension = "";
        // 获得邮件的内容输入流
        InputStream sbis = part.getInputStream();
        // 各种有附件的情况
        String name = "";
        String uuid = UUID.randomUUID().toString();
        if (Part.ATTACHMENT.equalsIgnoreCase(disposition) || disposition ==null) {
            name = getFileName(part);
//            System.out.println("Attachment: " + name + " : "
//                    + contentType);
            if(name.lastIndexOf(".")!=-1){
            	name = "attachment"+name.substring(name.lastIndexOf("."));
            }else{
            	name = "attachment";
            }
            File dir = new File(this.managerInfo.getAttachmentDir()+"/"+uuid);
            dir.mkdirs();
            fileNameWidthExtension = dir.getAbsolutePath()+"/"+ name;
            customEmail.setAttchmentPath(uuid+"/"+name);
        } else if (Part.INLINE.equalsIgnoreCase(disposition)) {
            name = getFileName(part);
            if(name.equals("unknown")){
            	if(contenttype.indexOf("text/plain")>=0&&StringUtils.isBlank(customEmail.getReceiveContent())){
            		customEmail.setReceiveContent("<pre>"+(String) part.getContent()+"</pre>");
            	}else if(contenttype.indexOf("text/html")>=0&&StringUtils.isBlank(customEmail.getReceiveContent())){
            		customEmail.setReceiveContent((String) part.getContent());
            	}
            }
            if(name.lastIndexOf(".")>0){
            	name = "attachment"+name.substring(name.lastIndexOf("."));
            }else{
            	name = "attachment"; 
            }
//            System.out.println("Inline: " + name + " : "
//                    + contentType);
            File dir = new File(this.managerInfo.getAttachmentDir()+"/"+uuid);
            dir.mkdirs();
            fileNameWidthExtension = dir.getAbsolutePath()+"/"+ name;
            customEmail.setInlineAttchmentPath(uuid+"/"+name);
        } else {}
        // 存储各类附件
        if (!fileNameWidthExtension.equals("")) {
            System.out.println("保存邮件附件到：" + fileNameWidthExtension);
            saveFile(fileNameWidthExtension, sbis);
            if(name.equals("attachment")){
            	try {
					customEmail.setReceiveContent("<pre>"+FileUtils.readFileToString(new File(fileNameWidthExtension),"utf-8")+"</pre>");
				} catch (Exception e) {}
            }
        }
    }          
    
    private String getFileName(Part part) throws MessagingException,
            UnsupportedEncodingException {
        String fileName = part.getFileName();
        String name = "unknown";
        if (fileName != null) {
	        fileName = MimeUtility.decodeText(fileName);
	        name = fileName;
            int index = fileName.lastIndexOf("/");
            if (index != -1) {
                name = fileName.substring(index + 1);
            }
        }
        return name;
    }
    /**
     * 保存文件内容
     * @param fileName    文件名
     * @param input        输入流
     * @throws IOException
     */
    private void saveFile(String fileName, InputStream input) throws IOException {
        // 为了放置文件名重名，在重名的文件名后面天上数字
        File file = new File(fileName);
        // 先取得文件名的后缀
        /* int lastDot = fileName.lastIndexOf(".");
        String extension = fileName.substring(lastDot);
        fileName = fileName.substring(0, lastDot);
        for (int i = 0; file.exists(); i++) {
            //　如果文件重名，则添加i
            file = new File(fileName + i + extension);
        }*/
        // 从输入流中读取数据，写入文件输出流
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        BufferedInputStream bis = new BufferedInputStream(input);
        int aByte;
        while ((aByte = bis.read()) != -1) {
            bos.write(aByte);
        }
        // 关闭流
        bos.flush();
        bos.close();
        bis.close();
    }

    /**
     * 获得尖括号之间的字符
     * @param str
     * @return
     * @throws Exception
     */
    private String getInfoBetweenBrackets(String str) throws Exception {
        int i, j; //用于标识字符串中的"<"和">"的位置
        if (str == null) {
            str = "error";
            return str;
        }
        i = str.lastIndexOf("<");
        j = str.lastIndexOf(">");
        if (i != -1 && j != -1){
            str = str.substring(i + 1, j);
        }
        return str;
    }
    
    public boolean transmitEmail(Message message,String toAddress) throws UnsupportedEncodingException {
		try {
			if (this.connectToServer()) {
				// 创建转发邮件信息
				Message forward = new MimeMessage(session);
				/** 设置邮件对象 */
				// 设置主题
				forward.setSubject("Please log in erp to process mail:"+Arrays.asList(message.getFrom())+",Subject:"+message.getSubject());
				forward.setFrom(new InternetAddress(managerInfo.getUserName()));
				 for(String to : toAddress.split(",")) {
					 forward.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                 }
			    forward.setContent(message.getContent(),"text/html;charset=utf-8");      
				Transport.send(forward);
				this.closeConnection();
				return true ;
			}
		} catch (MessagingException e) {
			System.out.println("转发失败！");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
    
    public boolean send(MailInfo mailInfo){
    	return send(mailInfo,0);
    }
    
    private boolean send(MailInfo mailInfo,int num){
    	if(num==3){
    		return false;
    	}
    	if (this.connectToServer()) {
        	 MimeMessage message=new MimeMessage(session);
             try{
            	 if(StringUtils.isEmpty(mailInfo.getFromServer())){
            		 message.setFrom(new InternetAddress(this.getManagerInfo().getUserName()));
            	 }else{
            		 message.setFrom(new InternetAddress(mailInfo.getFromServer()));
            	 }
                 String toAddress = mailInfo.getToAddress();
                 if(StringUtils.isBlank(toAddress)){
                	 logger.warn("发信地址为空！"+mailInfo.getSubject());
                	 return false;
                 }
                 for(String to : toAddress.split(",")) {
                	 message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                 }
                 String ccAddress = mailInfo.getCcToAddress();
                 if(!StringUtils.isBlank(ccAddress)){
                     for(String to : ccAddress.split(",")) {
                    	 message.addRecipient(Message.RecipientType.CC, new InternetAddress(to));
                     }
                 }
                 String bccAddress = mailInfo.getBccToAddress();
                 if(!StringUtils.isBlank(bccAddress)){
                     for(String to : bccAddress.split(",")) {
                    	 message.addRecipient(Message.RecipientType.BCC, new InternetAddress(to));
                     }
                 }
                 message.setSubject(mailInfo.getSubject(),"utf-8");
                 message.setSentDate(mailInfo.getSentdate());
                 //向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
                 Multipart multipart=new MimeMultipart();
                 BodyPart contentPart=new MimeBodyPart();
                 contentPart.setContent(mailInfo.getContent(), "text/html;charset=utf-8"); 
                 multipart.addBodyPart(contentPart);
                 
                /* contentPart = new MimeBodyPart();
                 DataSource fds = new FileDataSource
                   ("inlineatt.jpg");
                 contentPart.setDataHandler(new DataHandler(fds));
                 contentPart.setHeader("Content-ID","<image>");

                 // add it
                 multipart.addBodyPart(contentPart);*/
                 
                 
                 //添加附件
                 List<String> filesPath = mailInfo.getFilePath();
                 if(filesPath.size()>0){
                	 int i = 0;
                	 for (String filePath : filesPath) {
                		 BodyPart messageBodyPart=new MimeBodyPart();
                         DataSource source= new FileDataSource(filePath);
                         messageBodyPart.setDataHandler(new DataHandler(source));
                         sun.misc.BASE64Encoder enc=new sun.misc.BASE64Encoder();
                         messageBodyPart.setFileName("=?GBK?B?"+enc.encode(mailInfo.getFileName().get(i).getBytes())+"?=");
                         messageBodyPart.setFileName(MimeUtility.encodeText(mailInfo.getFileName().get(i)));
                         multipart.addBodyPart(messageBodyPart);
                         i++;
					}
                 }
                 message.setContent(multipart);
              //   message.setHeader("List-Unsubscribe","<http://domain.com/uns.html?test=wdqwqw>");
                 Transport.send(message);   
                // this.closeConnection();
                 return true;
             }catch(Exception e){
            	 e.printStackTrace();
            	 this.session = null;
            	 if(num==2){
            		 logger.error(this.managerInfo.getUserName()+":"+mailInfo.getSubject()+":"+mailInfo.getToAddress()+":第3次发送失败-->"+e.getMessage(), e);
            	 }
            	 if(!this.managerInfo.getUserName().contains("support")){
            		 this.closeConnection();
            	 }
                 num++;
                try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {}
             	send(mailInfo,num);
             }
        } else {
        	logger.warn("连接邮件服务器失败！");
        }
    	return false;
    }
    
	public void moveEmailToInbox() throws Exception {
		if (connectToServerByImap()) {
			this.imapStore = (IMAPStore) session.getStore("imap"); // 使用imap会话机制，连接服务器
			if(!imapStore.isConnected()){
				try{
					imapStore.connect();
				}catch(Exception e){
					logger.warn("监控垃圾箱连接服务器失败!!");
					return ;
				}
			}
		//	IMAPFolder drafts = (IMAPFolder)imapStore.getFolder("[Gmail]/草稿");//草稿箱
			IMAPFolder folder = (IMAPFolder) imapStore.getFolder("[Gmail]/Spam"); // 垃圾箱
			IMAPFolder folder1 = (IMAPFolder) imapStore.getFolder("INBOX"); // 收件箱
			folder.open(Folder.READ_WRITE);
		//	drafts.open(Folder.READ_WRITE);
			if(folder.isOpen()){
				Message[] msgs = folder.getMessages();
				// 获取总邮件数
				if(msgs.length>0){
					folder1.open(Folder.READ_WRITE);
					folder.copyMessages(msgs, folder1);
				}
			}
			/*if(drafts.isOpen()){
			Message[] msgs1 = drafts.getMessages();
				for (Message message : msgs1) {
					if(message.getRecipients(Message.RecipientType.TO).length==0){
						continue;
					}
					MimeMessage message1=new MimeMessage(session);
					message1.setFrom(new InternetAddress(this.getManagerInfo().getUserName()));
	                message1.addRecipients(Message.RecipientType.TO, message.getRecipients(Message.RecipientType.TO));
	                message1.addRecipients(Message.RecipientType.CC, message.getRecipients(Message.RecipientType.CC));
	                message1.addRecipients(Message.RecipientType.BCC,message.getRecipients(Message.RecipientType.BCC));
	                message1.setSubject(message.getSubject());
	                message1.setSentDate(message.getSentDate());
	                message1.setContent(message.getContent(),message.getContentType());
	                try{
	                	Transport.send(message1);
	                }catch(Exception e){
	                	logger.warn("草稿箱邮件发送失败！！");
	                }
					message.setFlag(Flags.Flag.DELETED, true);
				}
			}*/
		} else {
			logger.warn("imap连接邮件服务器失败！");
		}
	}
	
	public void deleteEmailToInbox() throws Exception {
		if (connectToServerByImap()) {
			this.imapStore = (IMAPStore) session.getStore("imap"); // 使用imap会话机制，连接服务器
			if(!imapStore.isConnected()){
				try{
					imapStore.connect();
				}catch(Exception e){
					logger.warn("监控垃圾箱连接服务器失败!!");
					return ;
				}
			}
			/*INBOX
			[Gmail]
			[Gmail]/All Mail
			[Gmail]/Drafts
			[Gmail]/Important
			[Gmail]/Sent Mail
			[Gmail]/Spam
			[Gmail]/Starred
			[Gmail]/Trash*/
			/*Folder folderr =imapStore.getDefaultFolder();
			for (Folder f : folderr.list()) {
				System.out.println(f.getFullName());
				for (Folder f1 : f.list()) {
					System.out.println(f1.getFullName());
				}
			}*/
			boolean flag=true;
			List<String> typeList=Lists.newArrayList("INBOX","[Gmail]/Sent Mail");
			for (String type: typeList) {
				if(!flag){
					break;
				}
				IMAPFolder folder = null;
				int total = 0;
				try{
					folder = (IMAPFolder) imapStore.getFolder(type); // 收件箱[Gmail]/Sent Mail
					total = folder.getMessageCount();
                }catch(Exception e){break;}
				
			    folder.open(Folder.READ_WRITE);
			    Long limitSize=0L;
				Long useSize=0L;
				Quota[] quotas = ((IMAPStore) imapStore).getQuota("INBOX");
				for (Quota quota : quotas) {
		            for (Quota.Resource resource : quota.resources) {
		            	limitSize=resource.limit;
		            	useSize=resource.usage;//KB
		            }
		         } 
				Double percent=useSize*100d/limitSize;
				logger.info(type+"邮箱共有邮件：" + total+" 封,limit:"+limitSize+",usage:"+useSize+",percent:"+percent);//KB
				if(percent>78){
					String toEmail="eileen|tim";
					StringBuilder cnt=new StringBuilder("邮件容量已占存储空间的"+percent);
					WeixinSendMsgUtil.sendTextMsgToUser(toEmail,cnt.toString());
					
					logger.info("邮箱共有邮件：" + total+" 封,limit:"+limitSize+",usage:"+useSize+",percent:"+percent);//KB
					long suitSize=18*1024*1024*1024;//B
					Message[] msgs=folder.getMessages();
					for(int i=0;i<msgs.length;i++){
						Date date = null;
						try {
							date =getSentDate(msgs[i]);
						} catch (Exception e) {
							continue;
						}
						if (!msgs[i].isSet(Flags.Flag.DELETED)&&date.before(DateUtils.addMonths(new Date(), -6))){  
							 msgs[i].setFlag(Flags.Flag.DELETED, true);
						}
						long temp=useSize*1024-msgs[i].getSize();//B
						if(temp<=suitSize){
							flag=false;
							break;
						}
					}
					folder.close(true);
					logger.info(type+"邮箱共有邮件：" + folder.getMessageCount()+" 封");
				}
			}
		} else {
			logger.warn("imap连接邮件服务器失败！");
		}
	}
	
	public static void main(String[] args) {
		try {
		  System.out.println(new CustomEmailManager().getCode("de").replace(">", "").replace("<", ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getCode(String country) throws Exception {
		if (connectToServerByImap()) {
			this.imapStore = (IMAPStore) session.getStore("imap"); // 使用imap会话机制，连接服务器
			if(!imapStore.isConnected()){
				try{
					imapStore.connect();
				}catch(Exception e){
					logger.warn("连接邮箱失败!!");
					return null;
				}
			}
			IMAPFolder folder = (IMAPFolder) imapStore.getFolder("INBOX"); // 收件箱
			folder.open(Folder.READ_WRITE);
			if(folder.isOpen()){
				String stuffix =country;
				if("jp".equals(country)||"uk".equals(country)){
					stuffix="co."+country;
				}else if("mx".equals(country)){
					stuffix="com."+country;
				}
				Pattern reg = Pattern.compile(">\\d{6}<");
			    SearchTerm term =new FromStringTerm("@amazon."+stuffix);   
			    Message[] msgs=folder.search(term);
			    for(int i=msgs.length-1;i>=0;i--){
			    	Message message=msgs[i];
			        String content="";
                    try{
                    	Multipart m = (Multipart) message.getContent();
     		            content=m.getBodyPart(0).getContent().toString().trim();
                    }catch(Exception e){
                    	logger.error("ContentType error！");
                    }
			    	Matcher matcher = reg.matcher(content);
					while(matcher.find()){
						return matcher.group().replace(">", "").replace("<", "");
					}
			    }
			}
			closeConnection();
		} else {
			logger.warn("imap连接邮件服务器失败！");
		}
		return null;
	}
	
}
