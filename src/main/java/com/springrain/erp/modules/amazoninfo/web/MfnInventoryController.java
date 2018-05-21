package com.springrain.erp.modules.amazoninfo.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.endpoint.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.entity.MfnInventoryFeed;
import com.springrain.erp.modules.amazoninfo.entity.MfnItem;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.MfnInventoryFeedService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 自发货库存管理Controller
 * @author Tim
 * @version 2015-07-09
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/mfnInventory")
public class MfnInventoryController extends BaseController {

	@Autowired
	private MfnInventoryFeedService mfnInventoryService;
	
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	
	@RequestMapping(value = {"list", ""})
	public String list(MfnInventoryFeed mfnInventory, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if(mfnInventory.getRequestDate()==null){
			mfnInventory.setRequestDate(DateUtils.addMonths(today,-1));
			mfnInventory.setEndDate(today);
		}
		User user = UserUtils.getUser();
		if (!user.isAdmin()&& mfnInventory.getCreateBy()==null){
			mfnInventory.setCreateBy(user);
		}; 
		if(UserUtils.hasPermission("it:special:permission")){
			mfnInventory.setCreateBy(null);
		}
		Page<MfnInventoryFeed> page = new Page<MfnInventoryFeed>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("id desc");
		}else{
			page.setOrderBy(orderBy+",id desc");
		}	
        page = mfnInventoryService.find(page, mfnInventory); 
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
        model.addAttribute("cuser",user);
		return "modules/amazoninfo/mfnInventoryList";
	}
	
	private static DateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmssSS"); 
	
	@RequestMapping(value = "save")
	public String save(final MfnInventoryFeed mfnInventory, Model model, RedirectAttributes redirectAttributes) {
		for (MfnItem item : mfnInventory.getItems()) {
			if(item.getId()==null){
				item.setMfnInventory(mfnInventory);
			}
		}
		
		Map<String,AmazonAccountConfig> configMap=amazonAccountConfigService.findConfigByAccountName();
		final AmazonAccountConfig config=configMap.get(mfnInventory.getAccountName());
		String rs = submit(mfnInventory,config);
		addMessage(redirectAttributes, rs);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/mfnInventory/?repage";
	}
	
	private  String submit(final MfnInventoryFeed mfnInventory,final AmazonAccountConfig config){
		String rs = "正在发到服务器，请等待结果。。。";
		try {
			if(mfnInventory.getId()==null){
				mfnInventory.setCreateBy(UserUtils.getUser());
				mfnInventory.setRequestDate(new Date());
			}
			mfnInventory.setState("1");
			mfnInventoryService.save(mfnInventory);
			new Thread(){
				public void run() {
					try {
						String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
						Client client = BaseService.getCxfClient(interfaceUrl);
						Object[] str = new Object[]{Global.getConfig("ws.key"),mfnInventory.getId(),config.getAccountName()};
						client.invoke("submitMfnInventoryFeed", str);
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			}.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			rs = "修改本地贴库存出错了!";
		}
		return rs;
	}
	
	
	@RequestMapping("/download")   
    public ModelAndView download(String fileName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		fileName = HtmlUtils.htmlUnescape(fileName);
        response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/mfnfeeds/";  
        String downLoadPath = ctxPath + fileName;
        fileName = fileName.substring(fileName.lastIndexOf("/")+1);
        try {   
            long fileLength = new File(downLoadPath).length();   
            response.setContentType("application/x-msdownload;");   
            response.setHeader("Content-disposition", "attachment; filename="  
                    + URLEncoder.encode(fileName,"utf-8"));   
            response.setHeader("Content-Length", String.valueOf(fileLength));   
            bis = new BufferedInputStream(new FileInputStream(downLoadPath));   
            bos = new BufferedOutputStream(response.getOutputStream());   
            byte[] buff = new byte[2048];   
            int bytesRead;   
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {   
                bos.write(buff, 0, bytesRead);   
            }   
            bos.flush();
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            if (bis != null)   
                bis.close();   
            if (bos != null)   
                bos.close();   
        }   
        return null;   
    }   
}
