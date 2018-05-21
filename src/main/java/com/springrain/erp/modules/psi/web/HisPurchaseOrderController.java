/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.HisPurchaseOrder;
import com.springrain.erp.modules.psi.service.HisPurchaseOrderService;

/**
 * 采购订单Controller
 * @author Michael
 * @version 2014-10-29
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/hisPurchaseOrder")
public class HisPurchaseOrderController extends BaseController {

	@Autowired
	private HisPurchaseOrderService hisPurchaseOrderService;
	

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "hisView")
	public String hisView(HisPurchaseOrder hisOrder, Model model) {
		hisOrder = hisPurchaseOrderService.get(hisOrder.getId());
		model.addAttribute("hisOrder", hisOrder);
		return "modules/psi/purchaseOrderHisView";
		
	}
}
