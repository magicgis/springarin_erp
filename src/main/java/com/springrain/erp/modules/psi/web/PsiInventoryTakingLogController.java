/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiInventoryTakingLog;
import com.springrain.erp.modules.psi.service.PsiInventoryTakingLogService;

/**
 * 人工盘点记录Controller
 * @author Michael
 * @version 2015-06-01
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiInventoryTakingLog")
public class PsiInventoryTakingLogController extends BaseController {
	@Autowired
	private PsiInventoryTakingLogService psiInventoryTakingLogService;
	
	@RequestMapping(value = {"list", ""})
	public String list(PsiInventoryTakingLog psiInventoryTakingLog, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		Page<PsiInventoryTakingLog> page=new Page<PsiInventoryTakingLog>(request, response);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date today =sdf.parse(sdf.format(new Date()));
		if (psiInventoryTakingLog.getCreateDate() == null) {
			psiInventoryTakingLog.setCreateDate(DateUtils.addMonths(today, -3));
			psiInventoryTakingLog.setTakingDate(today);
		}
		
        psiInventoryTakingLogService.find(page, psiInventoryTakingLog); 
        model.addAttribute("page", page);
		return "modules/psi/psiInventoryTakingLogList";
	}
	
	@RequestMapping(value = "form")
	public String form(PsiInventoryTakingLog psiInventoryTakingLog, String isPass, Model model) {
		if(psiInventoryTakingLog.getTakingDate()!=null){
			psiInventoryTakingLog.setTakingDate(new Date());
		}
		return "modules/psi/psiInventoryTakingLogForm";
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
	
	@RequestMapping(value = "save")
	public String save(PsiInventoryTakingLog psiInventoryTakingLog, String isPass,@RequestParam("attchmentFile")MultipartFile[] attchmentFiles, Model model, RedirectAttributes redirectAttributes) {
		psiInventoryTakingLogService.save(psiInventoryTakingLog,attchmentFiles);
		addMessage(redirectAttributes, "保存人工盘点记录'" + psiInventoryTakingLog.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiInventoryTakingLog/?repage";
	}
	
	
	@RequestMapping(value = "delete")
	public String delete(Integer id, RedirectAttributes redirectAttributes) {
		psiInventoryTakingLogService.delete(id);
		addMessage(redirectAttributes, "删除人工盘点记录成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiInventoryTakingLog/?repage";
	}
	
	


}
