package com.springrain.erp.modules.amazoninfo.web.order;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.order.MfnPackage;
import com.springrain.erp.modules.amazoninfo.service.order.MfnPackageService;


@Controller
@RequestMapping(value = "${adminPath}/amazonAndEbay/mfnOrder/package")
public class MfnPackageController extends BaseController {

	@Autowired
	private MfnPackageService mfnPackageService;
	
	@ModelAttribute
	public MfnPackage get(@RequestParam(required = false) String id) {
		if (id != null) {
			return mfnPackageService.get(Integer.parseInt(id));
		} else {
			return new MfnPackage();
		}
	}

	@RequestMapping(value = "packageListDown")
	public String packageListDown(MfnPackage mfnPackage, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<MfnPackage> page = new Page<MfnPackage>(request, response);
		if (mfnPackage.getStart()== null) {
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			mfnPackage.setStart(DateUtils.addDays(today, -10));
			mfnPackage.setPrintTime(today);
		}
		page = mfnPackageService.getPackage(page, mfnPackage);
		model.addAttribute("page", page);
		return "/modules/ebay/order/packageListDownload";
	}
	
}
