package com.springrain.erp.modules.psi.web;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductTieredPrice;
import com.springrain.erp.modules.psi.entity.PsiProductTieredPriceDto;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PurchaseOrder;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTieredPriceService;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "${adminPath}/psi/productTieredPrice")
public class PsiProductTieredPriceController extends BaseController {
	@Autowired
	private      PsiProductTieredPriceService		 psiProductTieredPriceService;
	@Autowired
	private      PsiSupplierService                  psiSupplierService;
	@Autowired
	private      PsiProductService                   productService;
	@Autowired
	private      MailManager              	         mailManager;
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = { "list", "" })
	public String list(PsiProductTieredPrice tieredPrice,HttpServletRequest request,HttpServletResponse response, Model model) {
		Map<String,PsiProductTieredPriceDto> map=psiProductTieredPriceService.findPrices(tieredPrice);
		List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
		List<PsiProduct> products = this.productService.findAll();
		model.addAttribute("productTieredPrice",tieredPrice);
		model.addAttribute("products",products);
		model.addAttribute("suppliers",suppliers);
		model.addAttribute("priceDtos",map.values());
		return "modules/psi/psiProductTieredPriceList";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"reviewlist"})
	public String reviewlist(PsiProductTieredPriceDto tieredPriceDto,HttpServletRequest request,HttpServletResponse response, Model model) {
		Page<PsiProductTieredPriceDto> page =new Page<PsiProductTieredPriceDto>(request, response);
		page=psiProductTieredPriceService.findDtos(page,tieredPriceDto);
		for(PsiProductTieredPriceDto dto :page.getList()){
			PsiProductTieredPriceDto tempDto=this.psiProductTieredPriceService.findPrices(dto.getProductId(), dto.getSupplierId(), dto.getCurrencyType(), dto.getColor());
			dto.setLeval500usd(tempDto.getLeval500cny());//现在都是用美元记录，所以用美元记录改前价格
			dto.setLeval1000usd(tempDto.getLeval1000cny());
			dto.setLeval2000usd(tempDto.getLeval2000cny());
			dto.setLeval3000usd(tempDto.getLeval3000cny());
			dto.setLeval5000usd(tempDto.getLeval5000cny());
			dto.setLeval10000usd(tempDto.getLeval10000cny());
			dto.setLeval15000usd(tempDto.getLeval15000cny());
		}
		List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
		List<PsiProduct> products = this.productService.findAll();
		model.addAttribute("productTieredPriceDto",tieredPriceDto);
		model.addAttribute("products",products);
		model.addAttribute("suppliers",suppliers);
		model.addAttribute("page",page);
		return "modules/psi/psiProductTieredPriceReviewList";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"noTax"})
	public String noTax(PsiProductTieredPrice tieredPrice,HttpServletRequest request,HttpServletResponse response, Model model) {
		Map<String,PsiProductTieredPriceDto> map=psiProductTieredPriceService.findPrices(tieredPrice);
		List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
		List<PsiProduct> products = this.productService.findAll();
		model.addAttribute("productTieredPrice",tieredPrice);
		model.addAttribute("products",products);
		model.addAttribute("suppliers",suppliers);
		model.addAttribute("priceDtos",map.values());
		return "modules/psi/psiProductTieredPriceNoTaxList";
	}
	
	
	@RequiresPermissions("psi:product:viewPrice")
	@RequestMapping(value = { "setPrice"})
	public String setPrice(Integer productId,Integer supplierId,String color,String productIdColor,HttpServletRequest request,HttpServletResponse response, Model model) {
		PsiProductTieredPriceDto  priceDto = this.psiProductTieredPriceService.find(productId, supplierId, color,productIdColor);
		Map<String,String> productColorMap= this.psiProductTieredPriceService.getProductColors();
		model.addAttribute("productColorMap",productColorMap);
		model.addAttribute("priceDto",priceDto);
		return "modules/psi/psiProductTieredPriceEdit";
	}
	
	
	@RequiresPermissions("psi:product:viewPrice")
	@RequestMapping(value = { "viewChangeLog"})
	public String viewChangeLog(Integer productId,Integer supplierId,String color,String productIdColor,HttpServletRequest request,HttpServletResponse response, Model model) {
		PsiProductTieredPriceDto  priceDto = this.psiProductTieredPriceService.find(productId, supplierId, color,productIdColor);
		model.addAttribute("priceDto",priceDto);
		return "modules/psi/psiProductTieredPriceLog";
	}
	
	
	@RequiresPermissions("psi:product:viewPrice")
	@RequestMapping(value = { "applyPrice"})
	public String applyPrice(PsiProductTieredPriceDto  priceDto,String isCheck,MultipartFile supplierFile,RedirectAttributes redirectAttributes, Model model) throws IOException {
		this.psiProductTieredPriceService.applyPrice(priceDto,supplierFile);
		//发送邮件通知emma
		String content = "Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;产品单价改动申请单已创建，请点击：<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/productTieredPrice/reviewPrice?id="+priceDto.getId()+"'>"+priceDto.getProNameColor()+"</a>进行审批";
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();
			final MailInfo mailInfo = new MailInfo("emma.chao@inateck.com,bella@inateck.com","产品改价申请已创建"+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(content);
			mailInfo.setCcToAddress(UserUtils.getUser().getEmail());
			//发送成功不成功都能保存
			new Thread(){
				@Override
				public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		}
		addMessage(redirectAttributes, "产品价格改动申请'" + priceDto.getProNameColor() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/productTieredPrice/reviewlist/?repage";
	}
	
	
	@RequiresPermissions("psi:product:reviewPrice")
	@RequestMapping(value = { "reviewPrice"})
	public String reviewPrice(PsiProductTieredPriceDto  priceDto,String isCheck,RedirectAttributes redirectAttributes, Model model) {
		priceDto=this.psiProductTieredPriceService.findDto(priceDto.getId());
		model.addAttribute("priceDto",priceDto);
		if("0".equals(priceDto.getReviewSta())){
			return "modules/psi/psiProductTieredPriceReview";
		}else{
			return "modules/psi/psiProductTieredPriceView";
		}
		
	}
	
	@RequiresPermissions("psi:product:viewPrice")
	@RequestMapping(value = { "view"})
	public String view(PsiProductTieredPriceDto  priceDto,String isCheck,RedirectAttributes redirectAttributes, Model model) {
		priceDto=this.psiProductTieredPriceService.findDto(priceDto.getId());
		model.addAttribute("priceDto",priceDto);
		return "modules/psi/psiProductTieredPriceView";
	}
	
	@RequiresPermissions("psi:product:reviewPrice")
	@RequestMapping(value = { "reviewSave"})
	public String reviewSave(PsiProductTieredPriceDto  priceDto,String isCheck,RedirectAttributes redirectAttributes) {
		this.psiProductTieredPriceService.reviewSave(priceDto);
		addMessage(redirectAttributes, "审核价格改动'" + priceDto.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/productTieredPrice/reviewlist/?repage";
	}
	
	@RequiresPermissions("psi:product:viewPrice")
	@RequestMapping(value = { "cancelPrice"})
	public String cancelPrice(PsiProductTieredPriceDto  priceDto,RedirectAttributes redirectAttributes) {
		PsiProductTieredPriceDto  dto=this.psiProductTieredPriceService.findDto(priceDto.getId());
		this.psiProductTieredPriceService.cancelPrice(dto);
		//发邮件通知创建人
		String content = "Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;产品单价改动申请单已取消，请点击：<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/productTieredPrice/view?id="+priceDto.getId()+"'>"+priceDto.getId()+"</a>查看";
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();
			final MailInfo mailInfo = new MailInfo(dto.getCreateUser().getEmail(),"产品改价申请已取消"+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(content);
			//发送成功不成功都能保存
			new Thread(){
				@Override
				public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		}
		addMessage(redirectAttributes, "取消价格改动'" + priceDto.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/productTieredPrice/reviewlist/?repage";
	}
	
	
}
