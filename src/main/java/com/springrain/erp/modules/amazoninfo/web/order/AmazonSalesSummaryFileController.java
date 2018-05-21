package com.springrain.erp.modules.amazoninfo.web.order;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonSalesSummaryFile;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonSalesSummaryFileService;

/**
 * 订单按月报表Controller
 * 
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/salesSummary")
public class AmazonSalesSummaryFileController extends BaseController {

	@Autowired
	private AmazonSalesSummaryFileService amazonSalesSummaryFileService;
	
	@ModelAttribute
	public AmazonSalesSummaryFile get(@RequestParam(required=false) Integer id) {
		if (id != null){
			return amazonSalesSummaryFileService.get(id);
		}else{
			return new AmazonSalesSummaryFile();
		}
	}

	@RequestMapping(value = { "list", "" })
	public String list(AmazonSalesSummaryFile amazonSalesSummaryFile, String end, HttpServletRequest request,
			HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		if (StringUtils.isEmpty(amazonSalesSummaryFile.getMonth())) {
			amazonSalesSummaryFile.setMonth(format.format(DateUtils.addMonths(new Date(), -6)));
			end = format.format(DateUtils.addMonths(new Date(), -1));
		} else {
			amazonSalesSummaryFile.setMonth(amazonSalesSummaryFile.getMonth().replace("-", ""));
			end = end.replace("-", "");
		}
		if (StringUtils.isEmpty(amazonSalesSummaryFile.getPlatform())) {
			amazonSalesSummaryFile.setPlatform("eu");
		}
		Page<AmazonSalesSummaryFile> page = new Page<AmazonSalesSummaryFile>(request, response);
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("id desc");
		} else {
			page.setOrderBy(orderBy + ",id desc");
		}
		page = amazonSalesSummaryFileService.find(page, amazonSalesSummaryFile, end);
		page.setOrderBy(orderBy);

		model.addAttribute("page", page);
		Date date = format.parse(amazonSalesSummaryFile.getMonth());
		Date endDate = format.parse(end);
		model.addAttribute("date", date);
		model.addAttribute("endDate", endDate);
		return "modules/amazoninfo/order/amazonOrderFileList";
	}
	
	@RequestMapping(value = "download")
	public String download(AmazonSalesSummaryFile amazonSalesSummaryFile, Model model,RedirectAttributes redirectAttributes,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, ParseException{
		String fileName = amazonSalesSummaryFile.getFilePath();
		fileName = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + fileName;
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(fileName);
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[4096];
			int len = 0;
			while ((len = inStream.read(buff, 0, 4096)) > 0) {
				swapStream.write(buff, 0, len);
				swapStream.flush();
			}
	        byte[] in2b = swapStream.toByteArray();
	        swapStream.close();
	        try {
	        	fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
			} catch (Exception e) {
				fileName = amazonSalesSummaryFile.getPlatform() + "SalesSummary-" + amazonSalesSummaryFile.getMonth() + ".xls";
			}
        	
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			out.write(in2b);
			out.close();
		} catch (Exception e) {
			logger.error("下载订单报表异常", e);
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					logger.warn("关闭流异常", e);
				}
			}
		}
		return null;
	}
	
	@RequestMapping(value = "downloadCsv")
	public String downloadCsv(AmazonSalesSummaryFile amazonSalesSummaryFile, Model model,RedirectAttributes redirectAttributes,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, ParseException{
		String fileName = amazonSalesSummaryFile.getFilePath();
		fileName = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + fileName;
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(fileName);
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[4096];
			int len = 0;
			while ((len = inStream.read(buff, 0, 4096)) > 0) {
				swapStream.write(buff, 0, len);
				swapStream.flush();
			}
	        byte[] in2b = swapStream.toByteArray();
	        swapStream.close();
	        try {
	        	fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
			} catch (Exception e) {
				fileName = amazonSalesSummaryFile.getPlatform() + "SalesSummary-" + amazonSalesSummaryFile.getMonth() + ".csv";
			}
        	
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			out.write(in2b);
			out.close();
		} catch (Exception e) {
			logger.error("下载订单报表异常", e);
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					logger.warn("关闭流异常", e);
				}
			}
		}
		return null;
	}
	
}
