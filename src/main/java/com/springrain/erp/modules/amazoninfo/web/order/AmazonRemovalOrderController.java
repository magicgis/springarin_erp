package com.springrain.erp.modules.amazoninfo.web.order;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.excel.ExportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonMonthlyStorageFees;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonRemovalOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonRemovalOrderItem;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonReturnOrderShipment;
import com.springrain.erp.modules.amazoninfo.service.SaleProfitService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonRemovalOrderService;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiInventoryIn;
import com.springrain.erp.modules.psi.entity.PsiInventoryInItem;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.entity.PsiTransportOrder;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrder;
import com.springrain.erp.modules.psi.service.PsiInventoryInService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiTransportOrderService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderService;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/removalOrder")
public class AmazonRemovalOrderController extends BaseController {

	@Autowired
	private AmazonRemovalOrderService amazonRemovalOrderService;
	
	@Autowired
	private PsiInventoryInService 			psiInventoryInService;
	@Autowired
	private PsiInventoryService 			psiInventoryService;
	@Autowired
	private StockService 					stockService;
	@Autowired
	private PsiProductService 				productService;
	@Autowired
	private PsiTransportOrderService 		tranSportService;
	@Autowired
	private LcPsiTransportOrderService 		lcTranSportService;
	
	@Autowired
	private SaleProfitService saleProfitService;
	
	@Autowired
	private PsiProductService psiProductService;

	@ModelAttribute
	public AmazonRemovalOrder get(@RequestParam(required=false) Integer id) {
		if (id != null){
			return amazonRemovalOrderService.get(id);
		}else{
			return new AmazonRemovalOrder();
		}
	}
	
	@RequestMapping(value = { "list", "" })
	public String list(AmazonRemovalOrder amazonRemovalOrder, String inStorage, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		if (StringUtils.isEmpty(amazonRemovalOrder.getCountry())) {
			amazonRemovalOrder.setCountry("com");
		}
		Page<AmazonRemovalOrder> page = amazonRemovalOrderService.find(
				new Page<AmazonRemovalOrder>(request, response), amazonRemovalOrder, inStorage);
		model.addAttribute("page", page);
		model.addAttribute("amazonRemovalOrder", amazonRemovalOrder);
		model.addAttribute("inStorage", inStorage);
		return "modules/amazoninfo/order/amazonRemovalOrderList";
	}
	
	@RequestMapping(value = "returnOrder")
	public String returnOrder(AmazonReturnOrderShipment amazonReturnOrderShipment, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		if(StringUtils.isBlank(amazonReturnOrderShipment.getTrackingNumber())){
			amazonReturnOrderShipment.setTrackingNumber("1");
		}
		Page<AmazonReturnOrderShipment> page = amazonRemovalOrderService.find(new Page<AmazonReturnOrderShipment>(request, response), amazonReturnOrderShipment);
		model.addAttribute("page", page);
		model.addAttribute("amazonReturnOrderShipment", amazonReturnOrderShipment);
		return "modules/amazoninfo/order/amazonRemovalOrderShipmentList";
	}
	
	@RequestMapping(value = "stored")
	public String stored(AmazonRemovalOrder amazonRemovalOrder, Model model) throws IOException {
		PsiInventoryIn psiInventoryIn = new PsiInventoryIn();
		psiInventoryIn.setDataDate(amazonRemovalOrder.getLastUpdateDate());
		//只查询本地仓库
		if ("de".equals(amazonRemovalOrder.getCountry())) {
			psiInventoryIn.setWarehouseId(19);
		} else if ("com,ca".contains(amazonRemovalOrder.getCountry())) {
			psiInventoryIn.setWarehouseId(120);
		} else {
			psiInventoryIn.setWarehouseId(130);
		}
		
		//获取仓库国家编码
		Set<String> countrySet=Sets.newHashSet();
		
		//根据用户权限获得仓库可选择信息
		List<Stock> stocks=stockService.findStocks("0");
		List<Stock> tempStocks = Lists.newArrayList();
		//首次进来
		Set<String> permissionsSet = Sets.newHashSet();
		//查询权限
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			for(Role role:UserUtils.getUser().getRoleList()){
				permissionsSet.addAll(role.getPermissions());
			}
			for(Stock stock:stocks){
				if(stock.getId().equals(psiInventoryIn.getWarehouseId())){
					if(StringUtils.isNotEmpty(stock.getCountrycode())){
						if("US".equals(stock.getCountrycode())){
							countrySet.add("com");
							countrySet.add("ca");
						}else if("DE".equals(stock.getCountrycode())){
							countrySet.add("de");
							countrySet.add("fr");
							countrySet.add("it");
							countrySet.add("uk");
							countrySet.add("es");
						}
					};
				}
				String countryCode=stock.getCountrycode();
				if(permissionsSet.contains("psi:inventory:edit:"+countryCode+"")){
					tempStocks.add(stock);
				}
			}
			stocks=tempStocks;
		}
		
		List<PsiSku> skuList = productService.getSkus(countrySet);
		Map<String,Object[]> skus=Maps.newHashMap();
		List<String> qualityTypes= Lists.newArrayList();
		qualityTypes.add("new");
		qualityTypes.add("old");
		qualityTypes.add("broken");
		qualityTypes.add("renew");
		//查询所有运单  ：已出库的        类型非批发发货      本地仓的 id数组
		List<PsiTransportOrder> tranOrders=tranSportService.findInventoryInTranOrder(new String[]{"1","2","3","4"},psiInventoryIn.getWarehouseId(),new String[]{"0","1"});
		//所有理诚的订单
		List<LcPsiTransportOrder> lcTranOrders=lcTranSportService.findInventoryInTranOrder(new String[]{"1","2","3","4"},psiInventoryIn.getWarehouseId(),new String[]{"0","1"});
		for(PsiSku sku:skuList){
			if("1".equals(sku.getUseBarcode())){   
				Object[] obj = {sku.getProductId(),sku.getProductName(),sku.getCountry(),sku.getColor()};
				skus.put(sku.getSku(), obj);
			}
		}
		
		//查询库存所有的sku  
		List<PsiInventory> inventorys=this.psiInventoryService.findByStock(psiInventoryIn.getWarehouseId());
		for(PsiInventory inventory:inventorys){
			if(skus.get(inventory.getSku())==null){
				Object[] obj = {inventory.getProductId(),inventory.getProductName(),inventory.getCountryCode(),inventory.getColorCode()};
				skus.put(inventory.getSku(), obj);
			}   
		}
		
		Map<String,String>  tranMap = Maps.newHashMap();
		for(PsiTransportOrder tranOrder:tranOrders){
			tranMap.put(tranOrder.getTransportNo(),tranOrder.getBillNo());
		}
		
		for(LcPsiTransportOrder tranOrder:lcTranOrders){
			tranMap.put(tranOrder.getTransportNo(),tranOrder.getBillNo());
		}
		model.addAttribute("qualityTypes", qualityTypes);
        model.addAttribute("stocks", stocks);
        model.addAttribute("skus", JSON.toJSON(skus));
        model.addAttribute("tranMap",tranMap);
		model.addAttribute("psiInventoryIn", psiInventoryIn);
		model.addAttribute("amazonRemovalOrder", amazonRemovalOrder);

		//asin对应的产品名
		Map<String,String> asinNameMap = saleProfitService.getProductNameByAsin();
        model.addAttribute("asinNameMap", asinNameMap);
		return "modules/amazoninfo/order/amazonRemovalOrderInAdd";
	}
	
	@RequestMapping(value = "storedSave")
	public String storedSave(AmazonRemovalOrder amazonRemovalOrder, Integer warehouseId, RedirectAttributes redirectAttributes) throws Exception {
		if(amazonRemovalOrder.getItems()==null||amazonRemovalOrder.getItems().size()==0){
			return null;
		}
		Map<String, Integer> productMap = Maps.newHashMap();
		for (PsiProduct product : psiProductService.find()) {
			productMap.put(product.getBrand() + " " + product.getModel(), product.getId());
		}
		Stock stock = stockService.get(warehouseId);
		PsiInventoryIn psiInventoryIn = new PsiInventoryIn();
		psiInventoryIn.setOperationType("Recall Storing");
		psiInventoryIn.setWarehouseId(warehouseId);
		psiInventoryIn.setWarehouseName(stock.getStockName());
		psiInventoryIn.setRemark("召回订单["+amazonRemovalOrder.getAmazonOrderId()+"]");
		psiInventoryIn.setSource(amazonRemovalOrder.getSource());
		psiInventoryIn.setDataDate(new Date());
		
		Map<Integer, Integer> itemQty = Maps.newHashMap();
		List<PsiInventoryInItem> items = Lists.newArrayList();
		for (AmazonRemovalOrderItem item : amazonRemovalOrder.getItems()) {
			Integer quantity = item.getCancelledQty();
			String sku = item.getSellersku();
			PsiInventoryInItem inItem = new PsiInventoryInItem();
			inItem.setSku(sku);
			String productName = item.getProductName();
			if (productName.contains("_")) {
				inItem.setProductName(productName.split("_")[0]);
				inItem.setColorCode(productName.split("_")[1]);
			} else {
				inItem.setProductName(productName);
				if (StringUtils.isNotEmpty(item.getColorCode())) {
					inItem.setColorCode(item.getColorCode());
				} else {
					inItem.setColorCode("");	//空字符串,避免null
				}
			}
			if (StringUtils.isNotEmpty(item.getCountryCode())) {
				inItem.setCountryCode(item.getCountryCode());
			} else {
				String country = amazonRemovalOrder.getCountry();
				if ("de".equals(country)) {
					if (sku.toLowerCase().contains("uk")) {
						country = "uk";
					} else if (sku.toLowerCase().contains("fr")) {
						country = "fr";
					} else if (sku.toLowerCase().contains("it")) {
						country = "it";
					} else if (sku.toLowerCase().contains("es")) {
						country = "es";
					}
				}
				inItem.setCountryCode(country);
			}
			inItem.setProductId(productMap.get(inItem.getProductName()));
			inItem.setQuantity(quantity);
			inItem.setQualityType(item.getQualityType());
			inItem.setRemark("召回订单item:" + item.getId());
			items.add(inItem);
			itemQty.put(item.getId(), quantity);
		}
		psiInventoryIn.setItems(items);
		this.psiInventoryInService.addSave(psiInventoryIn, null, null, null);
		//更新已入库数量20161014_RKD1024057
		amazonRemovalOrderService.updateStoredQty(itemQty);
		addMessage(redirectAttributes, "入库单"+psiInventoryIn.getBillNo()+"添加成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/removalOrder";
	}
	
	@RequestMapping(value = "updateTrackState")
	@ResponseBody
	public String updateTrackState(String delIds) {
		String[] idArr=delIds.split(",");
		amazonRemovalOrderService.updateTrackState(Sets.newHashSet(idArr));
		return "0";
	}
	
	@RequestMapping(value = {"export"})
    public void export(String country, String start, String end, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
        Map<String, List<Integer>> map = amazonRemovalOrderService.export();
         //title
        ExportExcel export = new ExportExcel("", Lists.newArrayList("产品名","召回数","已发货数","已到货数"));
        export.getSheet().setColumnWidth(0, 7000);
        for (Entry<String, List<Integer>> entry : map.entrySet()) {
            List<Integer> value = entry.getValue();
            Row row = export.addRow();
            export.addCell(row, 0, entry.getKey());
            export.addCell(row, 1, value.get(0));
            export.addCell(row, 2, value.get(1));
            export.addCell(row, 3, value.get(2));
         }
        try {
            export.write(response, "召回产品" + DateUtils.getDate() + ".xlsx").dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
