package com.springrain.erp.modules.amazoninfo.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.Collections3;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.excel.ExcelUtil;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.Feed;
import com.springrain.erp.modules.amazoninfo.entity.FeedSubmission;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.FeedSubmissionService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 亚马逊帖子上架Controller
 * @author tim
 * @version 2014-08-06
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/feedSubmission")
public class FeedSubmissionController extends BaseController {

	@Autowired
	private FeedSubmissionService feedSubmissionService;
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	private static Logger logger = LoggerFactory.getLogger(FeedSubmissionController.class);
	
	@ModelAttribute
	public FeedSubmission get(@RequestParam(required=false) Integer id) {
		if (id!=null&&id>0){
			return feedSubmissionService.get(id);
		}else{
			return new FeedSubmission();
		}
	}
	
	@RequiresPermissions("amazoninfo:posts:view")
	@RequestMapping(value = {"list", ""})
	public String list(FeedSubmission feedSubmission, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if(feedSubmission.getCreateDate()==null){
			feedSubmission.setCreateDate(DateUtils.addMonths(today,-1));
			feedSubmission.setEndDate(today);
		}
		User user = UserUtils.getUser();
		if(feedSubmission.getCreateBy()==null){
			feedSubmission.setCreateBy(user);
		}
		Page<FeedSubmission> page = new Page<FeedSubmission>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("id desc");
		}else{
			page.setOrderBy(orderBy+",id desc");
		}	
        page = feedSubmissionService.find(page, feedSubmission); 
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
        model.addAttribute("cuser",user);
		return "modules/amazoninfo/feedSubmissionList";
	}

	@RequiresPermissions("amazoninfo:posts:view")
	@RequestMapping(value = "form")
	public String form(FeedSubmission feedSubmission, Model model) {
		Map<String,List<String>> accountMap=amazonAccountConfigService.findAccountByCountry();
		model.addAttribute("feedSubmission", feedSubmission);
		model.addAttribute("accountMap", accountMap);
		return "modules/amazoninfo/feedSubmissionForm";
	}

	
	@RequiresPermissions("amazoninfo:posts:view")
	@RequestMapping(value = "view")
	public String view(FeedSubmission feedSubmission, Model model) {
		model.addAttribute("feedSubmission", feedSubmission);
		return "modules/amazoninfo/feedSubmissionView";
	}
	private static DateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmssSS"); 
	
	public static String computeContentMD5HeaderValue(FileInputStream fis)
			throws IOException, NoSuchAlgorithmException {
		DigestInputStream dis = new DigestInputStream(fis,
				MessageDigest.getInstance("MD5"));
		byte[] buffer = new byte[8192];
		while (dis.read(buffer) > 0)
			;
		String md5Content = new String(
				org.apache.commons.codec.binary.Base64.encodeBase64(dis
						.getMessageDigest().digest()));
		// Effectively resets the stream to be beginning of the file
		// via a FileChannel.
		fis.getChannel().position(0);
		return md5Content;
	}
	
	private static List<String> priceColName =Lists.newArrayList("standard_price","ItemPrice","item-price"); 
	private static List<String> salePriceColName =Lists.newArrayList("sale_price","SalePrice","sale-price");
	
	private static List<String> startDateColName =Lists.newArrayList("sale_from_date","SaleStartDate","sale-from-date");
	private static List<String> endDateColName =Lists.newArrayList("sale_end_date","SaleEndDate","sale-end-date");
	
	private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
	private static DateFormat format1 = new SimpleDateFormat("yyyy/MM/dd"); 
	
	private static List<String> parentChildColName =Lists.newArrayList("parent_child","Parentage","parent-child");
	
	private static List<String> relationshipTypeColName =Lists.newArrayList("relationship_type","RelationshipType","relationship-type");
	
	private static List<String> parentSkuColName =Lists.newArrayList("parent_sku","ParentSKU","parent-sku");
	
	private static List<String> subjectColName =Lists.newArrayList("Title","item_name","title");
	
	private static List<String> descColName =Lists.newArrayList("product_description","Description","description");
	
	private static List<String> bulletPointColName =Lists.newArrayList("bullet_point1","BulletPoint1","bullet-point1");
	
	private static List<String> genericKeywordsColName =Lists.newArrayList("generic_keywords1","SearchTerms1","search-terms1");
	
	public static File excelToTabTxt(boolean isJp,FeedSubmission feedSubmission,File excel) throws Exception{
		if(excel==null||!excel.exists()){
			return null;
		}else{
			String name = excel.getName().replaceAll("\\s+", " ");
			if(!name.equals(excel.getName())){
				File dist = new File(excel.getParentFile(),name);
				Files.copy(excel,dist);
				excel.delete();
				excel = dist;
			}
		}
		File result = new File(excel.getParentFile(),excel.getName().replace(".xlsx", ".txt").replace(".xls", ".txt"));
		List<String[]> data  = ExcelUtil.read(new FileInputStream(excel));
		OutputStreamWriter osw = null;
		if(isJp){
			osw = new OutputStreamWriter(new FileOutputStream(result),"Shift_JIS");
		}else{
			osw = new OutputStreamWriter(new FileOutputStream(result),"Iso8859-1");
		}
		int i = 0;
		List<String> colName = null;
		List<String> cellList =  null;
		for (String[] cells : data) {
			//找列名
			if(i>1&&colName==null){
				if(cells.length>2){
					for (String val : cells) {
						if("EAN".equalsIgnoreCase(val)||"ASIN".equalsIgnoreCase(val)||"GCID".equalsIgnoreCase(val)){
							colName = cellList;
							break;
						}
					}
					
				}
			}
			cellList = Lists.newArrayList();
			for (String cell : cells) {
				if(cell!=null){
					cell = cell.replaceAll("\\n", "");
					cellList.add(cell);
				}else{
					cellList.add("");
				}
			}
			if(colName!=null){
				//解析需要的数据
				Feed feed = new Feed();
				feed.setEan(cellList.get(1));
				feed.setFeedSubmission(feedSubmission);
				feed.setSku(cellList.get(0));
				if(feedSubmission.getFeeds()==null){
					feedSubmission.setFeeds(Lists.newArrayList(feed));
				}else{
					feedSubmission.getFeeds().add(feed);
				}
				for (String name : priceColName) {
					int index = colName.indexOf(name);
					if(index>=0){
						try {
							if(cellList.get(index).length()>0)
								feed.setPrice(Float.parseFloat(cellList.get(index)));
						} catch (Exception e) {
							logger.warn("解析价格发现不是数字");
						}
						break;
					}
				}
					
				for (String name : salePriceColName) {
					int index = colName.indexOf(name);
					if(index>=0){
						try {
							if(cellList.get(index).length()>0)
								feed.setSalePrice(Float.parseFloat(cellList.get(index)));
						} catch (Exception e) {
							logger.warn("解析销售价格发现不是数字");
						}
						break;
					}
				}	
					
				for (String name : startDateColName) {
					int index = colName.indexOf(name);
					if(index>=0){
						try {
							if(cellList.get(index).length()>0){
								if(cellList.get(index).contains("-"))
									feed.setSaleStartDate(format.parse(cellList.get(index)));
								else
									feed.setSaleStartDate(format1.parse(cellList.get(index)));
							}	
						} catch (Exception e) {
							logger.warn("解析销售价格开始时间有误");
						}
						break;
					}
				}	
				
				for (String name : endDateColName) {
					int index = colName.indexOf(name);
					if(index>=0){
						try {
							if(cellList.get(index).length()>0){
								if(cellList.get(index).contains("-"))
									feed.setSaleEndDate(format.parse(cellList.get(index)));
								else
									feed.setSaleEndDate(format1.parse(cellList.get(index)));
							}	
						} catch (Exception e) {
							logger.warn("解析销售价格结束时间有误");
						}
						break;
					}
				}	
				
				for (String name : parentChildColName) {
					int index = colName.indexOf(name);
					if(index>=0){
						feed.setParentChild(cellList.get(index));
						break;
					}
				}	
				
				for (String name : parentSkuColName) {
					int index = colName.indexOf(name);
					if(index>=0){
						feed.setParentSku(cellList.get(index));
						break;
					}
				}	
				
				for (String name : relationshipTypeColName ) {
					int index = colName.indexOf(name);
					if(index>=0){
						feed.setRelationshipType(cellList.get(index));
						break;
					}
				}	
				
				for (String name : subjectColName) {
					int index = colName.indexOf(name);
					if(index>=0){
						feed.setSubject(cellList.get(index));
						break;
					}
				}	
				
				for (String name : descColName) {
					int index = colName.indexOf(name);
					if(index>=0){
						feed.setDescription(cellList.get(index));
						break;
					}
				}	
				
				for (String name : bulletPointColName) {
					int index = colName.indexOf(name);
					if(index>=0){
						feed.setBulletPoint1(cellList.get(index));
						feed.setBulletPoint2(cellList.get(index+1));
						feed.setBulletPoint3(cellList.get(index+2));
						feed.setBulletPoint4(cellList.get(index+3));
						feed.setBulletPoint5(cellList.get(index+4));
						break;
					}
				}	
				for (String name : genericKeywordsColName) {
					int index = colName.indexOf(name);
					if(index>=0){
						feed.setGenericKeywords1(cellList.get(index));
						feed.setGenericKeywords2(cellList.get(index+1));
						feed.setGenericKeywords3(cellList.get(index+2));
						feed.setGenericKeywords4(cellList.get(index+3));
						feed.setGenericKeywords5(cellList.get(index+4));
						break;
					}
				}	
			}else{
				i++;
			}
			String line = Collections3.convertToString(cellList, "\t");
			/*if(!isJp){
				line = HtmlUtils.htmlEscape(line);
			}*/
			osw.write(line+"\n");
		}
		osw.flush();
		osw.close();
		return result;
	}
	
	public static String getSavePath(File file){
		String abs = file.getAbsolutePath().replace("\\","/");
		String[]temp = abs.split("/");
		if(temp.length>2){
			return temp[temp.length-2]+"/"+temp[temp.length-1];
		}
		return "";
	}
	
	@RequiresPermissions("amazoninfo:posts:view")
	@RequestMapping(value = "delete")
	public String delete(Integer id, RedirectAttributes redirectAttributes) {
		feedSubmissionService.delete(id);
		addMessage(redirectAttributes, "删除帖子成功!");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/feedSubmission/?repage";
	}
	
	@RequestMapping("/download")   
    public ModelAndView download(String fileName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		fileName = HtmlUtils.htmlUnescape(fileName);
        response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/feeds/";  
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
	
	public static void main(String[] args) {
		try {
			excelToTabTxt(false,new FeedSubmission(),new File("D:\\111\\FE2004-1HardDriveEnclosures.xls"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
