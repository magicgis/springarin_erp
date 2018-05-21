package com.springrain.erp.modules.oa.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.oa.entity.FixedAssets;
import com.springrain.erp.modules.oa.entity.Recruit;
import com.springrain.erp.modules.oa.service.FixedAssetsService;
import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 
 * @author michael   
 * 2016-11-15
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/fixedAssets")
public class FixedAssetsController extends BaseController {

	@Autowired
	protected 	FixedAssetsService     fixedAssetsService;
	@Autowired
	protected 	SystemService     systemService;
	
	@RequestMapping(value = {"list",""})
	public String Page(FixedAssets fixedAssets, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(fixedAssets.getCreateDate()==null){
			Date today = sdf.parse(sdf.format(new Date())); 
			fixedAssets.setCreateDate(DateUtils.addYears(today,-1));
			fixedAssets.setUpdateDate(today);    
		}
		
		List<FixedAssets> list =fixedAssetsService.find(fixedAssets); 
        model.addAttribute("list", list);  
		model.addAttribute("fixedAssets", fixedAssets);
		return "modules/oa/fixedAssetsList";
	}

	
	
	@RequestMapping(value = "form")
	public String form(FixedAssets fixedAssets, Model model) {
		if(fixedAssets.getId()!=null){
			fixedAssets=this.fixedAssetsService.get(fixedAssets.getId());
		}
		List<User> 	users  = systemService.findActiveUsers();
		List<User> 	userTemps=Lists.newArrayList();
		List<Office> offices = UserUtils.getOfficeList();
		for(User user:users){
			if("0".equals(user.getDelFlag())){
				userTemps.add(user);
			}
		}
		model.addAttribute("users", userTemps);
		model.addAttribute("offices", offices);
		model.addAttribute("fixedAssets", fixedAssets);
		return "modules/oa/fixedAssetsForm";
	}
	
	@RequestMapping(value = "view")
	public String view(FixedAssets fixedAssets, Model model) {
		if(fixedAssets.getId()!=null){
			fixedAssets=this.fixedAssetsService.get(fixedAssets.getId());
		}
		List<User> 	users  = systemService.findActiveUsers();
		model.addAttribute("users", users);
		model.addAttribute("fixedAssets", fixedAssets);
		return "modules/oa/fixedAssetsView";
	}
	

	@RequestMapping(value = "save")
	public String save(FixedAssets fixedAssets,String owner,Model model, RedirectAttributes redirectAttributes) throws IOException, ParseException {
		fixedAssetsService.saveFixed(fixedAssets,owner);
		addMessage(redirectAttributes, "保存招聘信息成功");
		return "redirect:"+Global.getAdminPath()+"/oa/fixedAssets/";
	}
	
	
	
	@RequestMapping("/download")   
    public ModelAndView download(String fileName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		fileName = HtmlUtils.htmlUnescape(fileName);
        response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir();  
        String downLoadPath = ctxPath + fileName;
        fileName = fileName.substring(fileName.lastIndexOf("/")+1);
        try {   
            long fileLength = new File(downLoadPath).length();   
            response.setContentType("application/x-msdownload;");   
            response.setHeader("Content-disposition", "attachment; filename="   + URLEncoder.encode(fileName,"utf-8"));   
            response.setHeader("Content-Length", String.valueOf(fileLength));   
            bis = new BufferedInputStream(new FileInputStream(downLoadPath));   
            bos = new BufferedOutputStream(response.getOutputStream());   
            byte[] buff = new byte[2048];   
            int bytesRead;   
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {   
                bos.write(buff, 0, bytesRead);   
            }   
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
	
	
	@RequestMapping(value = "delete")
	public String delete(Integer id, RedirectAttributes redirectAttributes) {
		fixedAssetsService.delete(id);
		addMessage(redirectAttributes, "删除固定资产成功");
		return "redirect:"+Global.getAdminPath()+"/oa/fixedAssets/";
	}


	
	@RequestMapping(value = "excel")
	public String excel(Model model) {
		return "modules/oa/fixedAssetsExcel";
	}
	
	
	@RequestMapping(value = "excelSave")
	public String excelSave(Recruit recruit,MultipartFile excelPath,Model model, RedirectAttributes redirectAttributes) throws IOException, ParseException, InvalidFormatException {
		this.fixedAssetsService.initData(excelPath);
		return "init固定资产信息成功";
	}
	
}
