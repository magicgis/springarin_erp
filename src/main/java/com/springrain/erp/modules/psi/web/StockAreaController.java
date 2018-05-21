/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.entity.StockArea;
import com.springrain.erp.modules.psi.entity.StockLocation;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.StockAreaService;
import com.springrain.erp.modules.psi.service.StockLocationService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 仓库分区
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/stockArea")
public class StockAreaController extends BaseController {

	@Autowired
	private StockAreaService	stockAreaService;
	@Autowired
	private StockService stockService;
	@Autowired
    private StockLocationService stockLocationService;
	
	@ModelAttribute
	public StockArea get(@RequestParam(required=false) Integer id) {
		if (id!=null&&id>0){
			return stockAreaService.get(id);
		}else{
			return new StockArea();
		}
	}
	
	
	@RequiresPermissions("psi:stock:view")
	@RequestMapping(value = {"list", ""})
	public String list(StockArea stockArea, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<StockArea> page = stockAreaService.find(new Page<StockArea>(request, response,10), stockArea); 
        model.addAttribute("page", page);
		return "modules/psi/stockAreaList";
	}

	
    @RequiresPermissions("psi:stock:view")
    @RequestMapping(value = "form")
    public String form(StockArea stockArea, Model model) {
        List<Stock> stocks = stockService.findStocks("0");
        model.addAttribute("stockArea", stockArea);
        model.addAttribute("stocks", stocks);
        return "modules/psi/stockAreaForm";
    }
    
    @RequiresPermissions("psi:stock:edit")
    @RequestMapping(value = "save")
    public String save(StockArea stockArea, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, stockArea)){
            return form(stockArea, model);
        }
        stockAreaService.save(stockArea);
        addMessage(redirectAttributes, "保存仓库分区'" + stockArea.getName() + "'成功");
        return "redirect:"+Global.getAdminPath()+"/psi/stockArea";
    }
    
    @RequiresPermissions("psi:stock:edit")
    @RequestMapping(value = "delete")
    public String delete(StockArea stockArea, RedirectAttributes redirectAttributes) {
        List<StockLocation> list = stockLocationService.get(stockArea.getId().toString());
        if(list.size()==0){
            stockArea.setDelFlag("1");
            stockAreaService.save(stockArea);
            addMessage(redirectAttributes, "删除仓库分区成功");
        }else{
            addMessage(redirectAttributes, "该库区下还有库位，请先删除库位");
        }
        return "redirect:"+Global.getAdminPath()+"/psi/stockArea/list/?repage";
    }
	
    @ResponseBody
    @RequiresPermissions("psi:stock:view")
    @RequestMapping(value = "form1")
    public StockArea form1(String id, Model model) {
        StockArea stockArea = stockAreaService.findAreaById(id);
        model.addAttribute("stockAreaEidt", stockArea);
        return stockArea;
    }
    
    
    
    @RequiresPermissions("psi:stock:edit")
    @RequestMapping(value = "save1")
    @ResponseBody
    public Map<String,String> save1(String id,String stockId,String remarks,String name, Model model, RedirectAttributes redirectAttributes) {
        StockArea stockArea = new StockArea();
        Map<String,String> map = Maps.newHashMap();
        if(id!=null){
            stockArea = stockAreaService.findAreaById(id);
            map.put("areaId", id);
        }else{
            stockArea.setStock(new Stock(Integer.parseInt(stockId)));
        }
        stockArea.setRemarks(remarks);
        stockArea.setName(name);
        map.put("stockName", name);
        
        stockAreaService.save(stockArea);
        map.put("areaId", stockAreaService.findAll().toString());
        return map;
    }
    
    @RequiresPermissions("psi:stock:edit")
    @ResponseBody
    @RequestMapping(value = "delete1")
    public List<Map<String, Object>> delete1(StockArea stockArea,String id, RedirectAttributes redirectAttributes,HttpServletResponse response) {
        stockArea.setDelFlag("1");
        stockAreaService.save(stockArea);
        response.setContentType("application/json; charset=UTF-8");
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<StockArea> list = stockAreaService.get(id);
        for (StockArea area : list) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", area.getId());
            map.put("pId", 0);
            map.put("name", area.getName());
            mapList.add(map);           
        }
        return mapList;
    }
    
    
    @RequestMapping(value = "positionArea")
    public String positionUserToRole(String flag, Model model,String type) {
        List<Stock> stocks = stockService.findStocks("0");
        model.addAttribute("stocks", stocks);
        model.addAttribute("flag",flag);
       return "modules/psi/selectStockArea";
    }
    
    @ResponseBody
    @RequestMapping(value = "areas")
    public List<Map<String, Object>> users(String officeId, HttpServletResponse response) {
        response.setContentType("application/json; charset=UTF-8");
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<StockArea> list = stockAreaService.get(officeId);
        for (StockArea area : list) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", area.getId());
            map.put("pId", 0);
            map.put("name", area.getName());
            mapList.add(map);           
        }
        return mapList;
    }
}


