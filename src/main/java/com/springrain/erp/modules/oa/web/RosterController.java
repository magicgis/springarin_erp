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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.oa.entity.Roster;
import com.springrain.erp.modules.oa.service.RosterService;
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
@RequestMapping(value = "${adminPath}/oa/roster")
public class RosterController extends BaseController {

	@Autowired
	protected 	RosterService     rosterService;
	@Autowired
	protected 	SystemService     systemService;
	
	
	@RequestMapping(value = {"list",""})
	public String list(Roster roster,String month, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		if(roster.getWorkSta()==null){
			roster.setWorkSta("1");
		}
		List<Roster> rosters = rosterService.find(roster,month); 
		List<Office> offices=UserUtils.getOfficeList();
		model.addAttribute("offices", offices);
        model.addAttribute("rosters", rosters);
		model.addAttribute("roster", roster);
		model.addAttribute("month", month);
		return "modules/oa/rosterList";
	}

	
	
	@RequestMapping(value = "form")
	public String form(Roster roster, Model model) {
		if(roster.getId()!=null){
			roster=this.rosterService.get(roster.getId());
		}
		List<User> 	 users   = systemService.findActiveUsers();
		List<Office> offices = UserUtils.getOfficeList();
		
		
		model.addAttribute("users", users);
		model.addAttribute("offices", offices);
		model.addAttribute("roster", roster);
		return "modules/oa/rosterForm";
	}
	
	@RequestMapping(value = "view")
	public String view(Roster roster, Model model) {
		if(roster.getId()!=null){
			roster=this.rosterService.get(roster.getId());
		}
		model.addAttribute("roster", roster);
		return "modules/oa/rosterView";
	}
	

	@RequestMapping(value = "save")
	public String save(Roster roster,MultipartFile resumePath,MultipartFile probationPath,MultipartFile trainPath, Model model, RedirectAttributes redirectAttributes) throws IOException, ParseException {
		rosterService.save(roster,resumePath,probationPath,trainPath);
		addMessage(redirectAttributes, "保存花名册成功");
		return "redirect:"+Global.getAdminPath()+"/oa/roster/";
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
		rosterService.delete(id);
		addMessage(redirectAttributes, "删除花名册成功");
		return "redirect:"+Global.getAdminPath()+"/oa/roster/";
	}

	@ResponseBody
	@RequestMapping(value = "existUser")
	public String existUser(String userId,Integer id, RedirectAttributes redirectAttributes) {
		return rosterService.existUser(userId,id);
	}

	
	@RequestMapping(value = "excel")
	public String excel(Model model) {
		return "modules/oa/rosterExcel";
	}
	
	@RequestMapping(value = "excelSave")
	public String excelSave(Roster roster,MultipartFile excelPath,Model model, RedirectAttributes redirectAttributes) throws IOException, ParseException, InvalidFormatException {
		this.rosterService.initData(excelPath);
		return "init人事信息成功";
	}
	
}
