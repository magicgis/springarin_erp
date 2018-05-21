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

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.oa.entity.Recruit;
import com.springrain.erp.modules.oa.service.RecruitService;
import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 
 * @author michael   
 * 2016-11-15
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/recruit")
public class RecruitController extends BaseController {

	@Autowired
	protected 	RecruitService     recruitService;
	
	
	@RequestMapping(value = {"list",""})
	public String Page(Recruit recruit, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(recruit.getCreateDate()==null){
			Date today = sdf.parse(sdf.format(new Date())); 
			recruit.setCreateDate(DateUtils.addMonths(today,-1));
			recruit.setUpdateDate(today);    
		}
		
		List<Recruit> recruits=recruitService.find(recruit); 
		List<Office> offices=UserUtils.getOfficeList();
		model.addAttribute("offices", offices);
		model.addAttribute("recruits", recruits);
		model.addAttribute("recruit", recruit);
		return "modules/oa/recruitList";
	}

	
	
	@RequestMapping(value = "form")
	public String form(Recruit recruit, Model model) {
		if(recruit.getId()!=null){
			recruit=this.recruitService.get(recruit.getId());
		}
		
		List<Office> offices = UserUtils.getOfficeList();
		
		model.addAttribute("offices", offices);
		model.addAttribute("recruit", recruit);
		return "modules/oa/recruitForm";
	}
	
	@RequestMapping(value = "view")
	public String view(Recruit recruit, Model model) {
		if(recruit.getId()!=null){
			recruit=this.recruitService.get(recruit.getId());
		}
		model.addAttribute("recruit", recruit);
		return "modules/oa/recruitView";
	}
	

	@RequestMapping(value = "save")
	public String save(Recruit recruit,MultipartFile resumePath,Model model, RedirectAttributes redirectAttributes) throws IOException, ParseException {
		recruitService.save(recruit,resumePath);
		addMessage(redirectAttributes, "保存招聘信息成功");
		return "redirect:"+Global.getAdminPath()+"/oa/recruit/";
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
		recruitService.delete(id);
		addMessage(redirectAttributes, "删除简历成功");
		return "redirect:"+Global.getAdminPath()+"/oa/recruit/";
	}


	
	@RequestMapping(value = "excel")
	public String excel(Model model) {
		return "modules/oa/recruitExcel";
	}
	
	@RequestMapping(value = "excelSave")
	public String excelSave(Recruit recruit,MultipartFile excelPath,Model model, RedirectAttributes redirectAttributes) throws IOException, ParseException, InvalidFormatException {
		this.recruitService.initData(excelPath);
		return "init招聘信息成功";
	}
	
}
