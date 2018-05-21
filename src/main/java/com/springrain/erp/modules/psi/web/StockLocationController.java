/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.excel.ExportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.plan.dto.Month;
import com.springrain.erp.modules.plan.entity.Plan;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.entity.StockArea;
import com.springrain.erp.modules.psi.entity.StockLocation;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiInventoryLocationService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.StockAreaService;
import com.springrain.erp.modules.psi.service.StockLocationService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 仓库分区
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/stockLocation")
public class StockLocationController extends BaseController {

	@Autowired
	private StockLocationService	stockLocationService;
	@Autowired
	private StockService stockService;
	@Autowired
	private StockAreaService stockAreaService;
	@Autowired
	private PsiInventoryLocationService inventoryLocationService;
	
	@ModelAttribute
	public StockLocation get(@RequestParam(required=false) Integer id) {
		if (id!=null&&id>0){
			return stockLocationService.get(id);
		}else{
			return new StockLocation();
		}
	}
	
	
	@RequiresPermissions("psi:stock:view")
	@RequestMapping(value = {"list", ""})
	public String list(StockLocation stockLocation, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<StockLocation> page = stockLocationService.find(new Page<StockLocation>(request, response,10),stockLocation); 
        model.addAttribute("page", page);
		return "modules/psi/stockLocationList";
	}
	
	
    @RequiresPermissions("psi:stock:view")
    @RequestMapping(value = "form")
    public String form(StockLocation stockLocation, Model model) {
        List<Stock> stocks = stockService.findStocks("0");
        List<StockArea> stockAreas = stockLocation.getId()!=null?stockAreaService.findStockAreaByStockId(stockLocation.getStockArea().getStock().getId().toString()):stockAreaService.findStockAreaByStockId(stocks.get(0).getId().toString());
        String stockId = stockLocation.getId()!=null?stockLocation.getStockArea().getStock().getId().toString():"-1";
        model.addAttribute("stocks", stocks);
        model.addAttribute("stockAreas", stockAreas);
        model.addAttribute("stockLocation",stockLocation);
        model.addAttribute("stockId",stockId);
        return "modules/psi/stockLocationForm";
    }
    
    @RequiresPermissions("psi:stock:edit")
    @RequestMapping(value = "save")
    public String save(StockLocation stockLocation, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, stockLocation)){
            return form(stockLocation, model);
        }
        stockLocationService.save(stockLocation);
        addMessage(redirectAttributes, "保存库位'" + stockLocation.getName() + "'成功");
        return "redirect:"+Global.getAdminPath()+"/psi/stockLocation";
    }
	
    @RequiresPermissions("psi:stock:edit")
    @RequestMapping(value = "delete")
    public String delete(StockLocation stockLocation, RedirectAttributes redirectAttributes,HttpServletResponse response) {
        if(inventoryLocationService.findByLocation(stockLocation.getId()) == 0){
            stockLocation.setDelFlag("1");
            stockLocationService.save(stockLocation);
            addMessage(redirectAttributes, "删除库位成功");
        }else{
            addMessage(redirectAttributes, "提醒：该库位上有货物，无法删除！");
        }
        return "redirect:"+Global.getAdminPath()+"/psi/stockLocation/list/?repage";
    }
    
    @RequestMapping(value = "view")
    public String view(StockLocation stockLocation,String stockLocationId,String stockId,String name,Model model) {
        List<StockArea> stockAreas = stockAreaService.findStockAreaByStockId(stockId);
        if(StringUtils.isNotBlank(stockLocationId)){
            stockLocation = stockLocationService.findById(Integer.parseInt(stockLocationId));
        }
        if(StringUtils.isNotBlank(name)){
                stockLocation.setName(name );
        }
        List<Stock> stocks = stockService.findStocks("0");
        if(stockLocation != null){
            model.addAttribute("stockLocation", stockLocation);
        }
        model.addAttribute("stockAreas", stockAreas);
        model.addAttribute("stocks", stocks);
        model.addAttribute("stockId", stockId);
        return "modules/psi/stockLocationForm";
    }
    
    @ResponseBody
    @RequestMapping(value = "locations")
    public List<Map<String, Object>> users(String officeId, HttpServletResponse response) {
        response.setContentType("application/json; charset=UTF-8");
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<StockLocation> list = stockLocationService.get(officeId);
        for (StockLocation location : list) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", location.getId());
            map.put("pId", 0);
            map.put("name", location.getName());
            mapList.add(map);           
        }
        return mapList;
    }
    
    @ResponseBody
    @RequiresPermissions("psi:stock:view")
    @RequestMapping(value = "form1")
    public StockLocation form1(String id, Model model) {
        StockLocation stockLocation = stockLocationService.findById(Integer.parseInt(id));
        return stockLocation;
    }
    
    
    
    @RequiresPermissions("psi:stock:edit")
    @RequestMapping(value = "save1")
    @ResponseBody
    public Map<String,String> save1(String id,String stockAreaId,String remarks,String name, Model model, RedirectAttributes redirectAttributes) {
        Map<String,String> map = Maps.newHashMap();
        StockLocation stockLocation = new StockLocation();
        if(id!=null){
            stockLocation = stockLocationService.findById(Integer.parseInt(id));
        }else{
            stockLocation.setStockArea(stockAreaService.findAreaById(stockAreaId));
        }
        stockLocation.setRemarks(remarks);
        stockLocation.setName(name);
        map.put("locationName", name);
        
        stockLocationService.save(stockLocation);
        map.put("locationId", stockLocationService.getCount().toString());
        return map;
    }
    
    @RequiresPermissions("psi:stock:edit")
    @ResponseBody
    @RequestMapping(value = "delete1")
    public String delete1(StockLocation stockLocation,String id, RedirectAttributes redirectAttributes,HttpServletResponse response) {
        String flag="0";
        if(inventoryLocationService.findByLocation(Integer.parseInt(id)) == 0){
            stockLocation.setDelFlag("1");
            stockLocationService.save(stockLocation);
            flag="1";
        }
        return flag;
    }
}

