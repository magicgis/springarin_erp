/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web.lc;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.lc.LcHisPurchaseOrder;
import com.springrain.erp.modules.psi.service.lc.LcHisPurchaseOrderService;

/**
 * 采购订单Controller
 * @author Michael
 * @version 2014-10-29
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/lcHisPurchaseOrder")
public class LcHisPurchaseOrderController extends BaseController {

	
	@Autowired
	private LcHisPurchaseOrderService hisPurchaseOrderService;
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "hisView")
	public String hisView(LcHisPurchaseOrder hisOrder, Model model) {
		hisOrder = hisPurchaseOrderService.get(hisOrder.getId());
		model.addAttribute("hisOrder", hisOrder);
		return "modules/psi/lc/lcPurchaseOrderHisView";
		
	}
}
