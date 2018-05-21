package com.springrain.erp.common.utils.excel;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.web.context.ContextLoader;


public class ExportTransportExcel {

/**
        读取excel表格模板
 * @param name 模板名称
 * @return 配置信息 封装成map
 */
@SuppressWarnings("unchecked")
public Map<Object,Object> readExcelSetting(String name){
		
		String excelPath= ContextLoader.getCurrentWebApplicationContext()
				.getServletContext().getRealPath("/")
				+ "WEB-INF/classes/templates/psi/psiExcel/"+name+".xml";
		SAXReader saxReader = new SAXReader();

        Document document = null;
		try {
			document = saxReader.read(new File(excelPath));
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		Map<Object,Object> excelSetting = new HashMap<Object,Object>();
		List<Object> unitSettingList = new ArrayList<Object>();//单元格简单属性配置集合
		List<Object> entitylist = new ArrayList<Object>();//列表配置
		List<Object> totalList=new ArrayList<Object>();
		Map<Object,Object> listSettingMap = new HashMap<Object,Object>();////单元格对象配置集合
        // 获取根元素
        Element root = document.getRootElement();
        //System.out.println("Root: " + root.getName());

        // 获取固定单元格配置
        List<Element> unitElements =root.elements("data");
        Map<Object,Object> data = null;
        for(Iterator<Element> iter = unitElements.iterator();iter.hasNext();){
        	data = new HashMap<Object,Object>();//一个单元格的配置
        	Element e = (Element)iter.next();
        	System.out.println("row:"+e.attributeValue("row")+",cell:"+e.attributeValue("cell")+"value:"+e.getText());
        	data.put("row", e.attributeValue("row"));
        	data.put("cell", e.attributeValue("cell"));
        	data.put("fieldName", e.getTextTrim());
        	unitSettingList.add(data);
        }
        
        //获取单元格对象配置
        List<Element> unitEntityElements = root.elements("entity");
        Map<Object,Object> entityData=null;
        for(Iterator<Element> iter = unitEntityElements.iterator();iter.hasNext();){
        	entityData = new HashMap<Object,Object>();//一个单元格的配置
        	Element e = (Element)iter.next();
        	entityData.put("row", e.attributeValue("row"));
        	entityData.put("cell", e.attributeValue("cell"));
        	entityData.put("name", e.attributeValue("name"));
        	entityData.put("fieldName", e.getTextTrim());
        	entitylist.add(entityData);
        }
      
        //总计
        List<Element> totalElements = root.elements("total");
        Map<Object,Object> totalData=null;
        for(Iterator<Element> iter = totalElements.iterator();iter.hasNext();){
        	totalData = new HashMap<Object,Object>();//一个单元格的配置
        	Element e = (Element)iter.next();
        	totalData.put("space", e.attributeValue("space"));
        	totalData.put("cell", e.attributeValue("cell"));
        	totalData.put("name",  e.getTextTrim());
        	totalList.add(totalData);
        }
        // 获取列表单元格配置
        Element listElement = root.element("list");
        listSettingMap.put("startRow", listElement.attributeValue("startRow"));//起始行号
        listSettingMap.put("items", listElement.attributeValue("items"));//需要遍历的集合的字段名称
        List<Element> itemElements = listElement.elements("field");
		List<Element> itemObject = listElement.elements("fieldObject");
        //List<Element> itemFormula = listElement.elements("fieldFormula");
         
        List<Object> fieldsList = new ArrayList<Object>();
        Map<Object,Object> fieldData = null;
        for(Iterator<Element> iter = itemElements.iterator();iter.hasNext();){
        	fieldData = new HashMap<Object,Object>();//一个字段的配置
        	Element e = (Element)iter.next();
        	//System.out.println("row:"+e.attributeValue("row")+",cell:"+e.attributeValue("cell")+"value:"+e.getText());
        	
        	fieldData.put("cell", e.attributeValue("cell"));
        	fieldData.put("fieldName", e.getTextTrim());
        	fieldData.put("colspan", e.attributeValue("colspan"));//合并列数
        	fieldData.put("type", e.attributeValue("type"));//是否为公式
        	fieldsList.add(fieldData);
        }
        
        
        List<Object> fieldsList1 = new ArrayList<Object>();
        Map<Object,Object> fieldData1 = null;
        for(Iterator<Element> iter = itemObject.iterator();iter.hasNext();){
        	fieldData1 = new HashMap<Object,Object>();//一个字段的配置
        	Element e = (Element)iter.next();
        	fieldData1.put("cell", e.attributeValue("cell"));
        	fieldData1.put("fieldName", e.getTextTrim());
        	fieldData1.put("name",e.attributeValue("name"));
        	fieldData1.put("colspan", e.attributeValue("colspan"));
        	fieldData1.put("type", e.attributeValue("type"));//是否为公式
        	fieldsList1.add(fieldData1);
        }
        listSettingMap.put("fieldsList1", fieldsList1);
        listSettingMap.put("fieldsList", fieldsList);
        excelSetting.put("unitSetting", unitSettingList);
        excelSetting.put("unitEntitySetting",entitylist);
        excelSetting.put("totalList",totalList);
        excelSetting.put("listSetting", listSettingMap);
        return excelSetting;
	}
	

 public Workbook writeData(Object object,String name,String modelName,int index){
		InputStream is = null;
		Workbook workbook = null;
		Sheet sheet = null;
		try {
			String file= ContextLoader.getCurrentWebApplicationContext()
					.getServletContext().getRealPath("/")
					+ "WEB-INF/classes/templates/psi/psiExcel/"+modelName+".xlsx";
			is=new FileInputStream (file);//PI EU-空海
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			workbook = WorkbookFactory.create(is);//
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
			sheet = workbook.getSheetAt(index);//sheet
		
			//--------------------写入---------------------//
			Class orderClass = object.getClass();
			//读取配置文件
			Map<Object,Object> excelSettingMap = this.readExcelSetting(name);
			List unitSetting = (List)excelSettingMap.get("unitSetting");
			//写入固定单元格数据（简单属性）
			for(Iterator iter = unitSetting.iterator();iter.hasNext();){
				Map data = (Map)iter.next();
				PropertyDescriptor pd = null;
				Class type=null;
				try {
					pd = new PropertyDescriptor(data.get("fieldName").toString(),orderClass);
				} catch (IntrospectionException e) {
					e.printStackTrace();
				}
				Method method = pd.getReadMethod();
				Object value = null;
				try {
					value = method.invoke(object);
					type=method.getReturnType();
					if(value==null){
						value = "";
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			    
			/*	if(sheet.getRow(Integer.parseInt(data.get("row").toString()))==null){
					sheet.createRow(Integer.parseInt(data.get("row").toString()));
				}
				if(sheet.getRow(Integer.parseInt(data.get("row").toString())).getCell(Integer.parseInt(data.get("cell").toString()))==null){
					sheet.getRow(Integer.parseInt(data.get("row").toString())).createCell(Integer.parseInt(data.get("cell").toString()));
				}*/
			    if(getVauleType(type)==0&&value!=""){
					sheet.getRow(Integer.parseInt(data.get("row").toString())).getCell(Integer.parseInt(data.get("cell").toString())).setCellValue(Double.parseDouble(value.toString()));
				}else{
					sheet.getRow(Integer.parseInt(data.get("row").toString())).getCell(Integer.parseInt(data.get("cell").toString())).setCellValue(value.toString());
				}
			}
			
			//实体对象
			
			List unitEntitySetting = (List)excelSettingMap.get("unitEntitySetting");
			//写入固定单元格数据（对象）
			for(Iterator iter = unitEntitySetting.iterator();iter.hasNext();){
						Map data = (Map)iter.next();
						PropertyDescriptor pdc = null;
						PropertyDescriptor pdf = null;
						try {
							pdc = new PropertyDescriptor(data.get("name").toString(),orderClass);
						} catch (IntrospectionException e) {
							e.printStackTrace();
						}
						Method method = pdc.getReadMethod();
						Object value = null;
						try {
							value = method.invoke(object);
							if(value==null){
								value = "";
							}
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}				
						
						Object entity = (Object)value;
						Class type=null;
						try {
							pdf = new PropertyDescriptor(data.get("fieldName").toString(),entity.getClass());
						} catch (IntrospectionException e) {
							e.printStackTrace();
						}
						Method method1 = pdf.getReadMethod();
						Object itemFieldValue = null;
						try {
							itemFieldValue = method1.invoke(entity);
							type=method1.getReturnType();
							if(itemFieldValue==null){
								itemFieldValue = "";
							}
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
							
						if(getVauleType(type)==0&&itemFieldValue!=""){
							sheet.getRow(Integer.parseInt(data.get("row").toString())).getCell(Integer.parseInt(data.get("cell").toString())).setCellValue(Double.parseDouble(itemFieldValue.toString()));
						}else{
							sheet.getRow(Integer.parseInt(data.get("row").toString())).getCell(Integer.parseInt(data.get("cell").toString())).setCellValue(itemFieldValue.toString());
						}
		    }
			
			writeListItemData(sheet,excelSettingMap,object);//列表
			

		
		return workbook;
	}	


	
	
	
	public void writeListItemData(Sheet sheet,Map<Object,Object> excelSettingMap,Object object){
		//写入列表数据
				Map listSetting = (Map)excelSettingMap.get("listSetting");
				
				PropertyDescriptor itemspd = null;
				try {
					itemspd = new PropertyDescriptor(listSetting.get("items").toString(),object.getClass());
				} catch (IntrospectionException e) {
					e.printStackTrace();
				}
				Method itemspdMethod = itemspd.getReadMethod();
				Object value = null;
				try {
					value = itemspdMethod.invoke(object);
					if(value==null){
						value = "";
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				List items = (List)value;

				List fields = (List)listSetting.get("fieldsList");
				
				List fieldObject=(List)listSetting.get("fieldsList1");
				
				int startRowNum = Integer.parseInt(listSetting.get("startRow").toString());
				
				
				
				
				//sheet.shiftRows(14, 14, items.size());
				for(int i = 0;i<items.size();i++){
					Object item = items.get(i);
					Row row=null;
					if(i>0){
						 // row=sheet.createRow(startRowNum+i);
						try{
							sheet.shiftRows(startRowNum+i, sheet.getLastRowNum(), 1,true,false);
						}catch(Exception e){
							sheet.shiftRows(startRowNum+i,100, 1,true,false);
						}
						  row=sheet.createRow(startRowNum+i);
						  row.setHeight((short) 375);
					}
					for(Iterator iter = fields.iterator();iter.hasNext();){//简单单元格属性
						Map data = (Map)iter.next();
						PropertyDescriptor pd = null;
						
						Object itemFieldValue = null;
						Class  type=null; 
						int cellType=Integer.parseInt(data.get("type").toString());
						if(cellType!=1){//不处理公式类型
							try {
								pd = new PropertyDescriptor(data.get("fieldName").toString(),item.getClass());
							} catch (IntrospectionException e) {
								e.printStackTrace();
							}
							Method method = pd.getReadMethod();
							
							try {
								itemFieldValue = method.invoke(item);
								type=method.getReturnType();
								
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
						}
						
						
						int a1 = Integer.parseInt(data.get("cell").toString());
						int colspan=Integer.parseInt(data.get("colspan").toString());
						if(itemFieldValue==null){
							itemFieldValue = "";
						}
						String a2 = itemFieldValue.toString();
						Cell cell=null;
						if(i>0){
							  cell=row.createCell(a1);
							   if(colspan>0){//合并单元格
								   
								   for(int s=a1;s<a1+colspan;s++){
									   row.createCell(s);
									   row.getCell(s).setCellStyle(sheet.getRow(startRowNum).getCell(a1).getCellStyle());
								   }
								   sheet.addMergedRegion(new CellRangeAddress(startRowNum+i, startRowNum+i, a1, a1+colspan-1));
							   }
							   
								if(cellType==1){//公式类型
									cell.setCellFormula(sheet.getRow(startRowNum).getCell(a1).getCellFormula());
								}else{	 
									  
									  if(getVauleType(type)==0&&a2!=""){
										  cell.setCellValue(Double.parseDouble(a2));
									  }else{
										  cell.setCellValue(a2);
									  }
								}
								
								  cell.setCellStyle(sheet.getRow(startRowNum).getCell(a1).getCellStyle());
								   
								  cell.setCellType(sheet.getRow(startRowNum).getCell(a1).getCellType());
						   
						}else{
							if(cellType!=1){
								 if(getVauleType(type)==0&&a2!=""){
										sheet.getRow(startRowNum).getCell(a1).setCellValue(Double.parseDouble(a2));
								}else{
									try{
										sheet.getRow(startRowNum).getCell(a1).setCellValue(a2);
									}catch(Exception e){
										sheet.getRow(startRowNum).createCell(a1);
										sheet.getRow(startRowNum).getCell(a1).setCellValue(a2);
									}
										 
								 }
							}
							
						}
						
					}
					
					
					for(Iterator iter = fieldObject.iterator();iter.hasNext();){
						Map data = (Map)iter.next();
						PropertyDescriptor pdc = null;
						PropertyDescriptor pdf = null;
						try {
							pdc = new PropertyDescriptor(data.get("name").toString(),item.getClass());
						} catch (IntrospectionException e) {
							e.printStackTrace();
						}
						Method method = pdc.getReadMethod();
						Object value1 = null;
						try {
							value1 = method.invoke(item);
							if(value1==null){
								value1 = "";
							}
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}				
						
						Object entity = (Object)value1;
						
						try {
							pdf = new PropertyDescriptor(data.get("fieldName").toString(),entity.getClass());
						} catch (IntrospectionException e) {
							e.printStackTrace();
						}
						Method method1 = pdf.getReadMethod();
						Object itemFieldValue = null;
						Class type=null;
						try {
							itemFieldValue = method1.invoke(entity);
							type=method1.getReturnType();
							if(itemFieldValue==null){
								itemFieldValue = "";
							}
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
						
						int a1 = Integer.parseInt(data.get("cell").toString());
						String a2 = itemFieldValue.toString();
						Cell cell=null;
						int colspan=Integer.parseInt(data.get("colspan").toString());
						int cellType=Integer.parseInt(data.get("type").toString());
						if(i>0){
						  cell=row.createCell(a1);
						  if(colspan>0){//合并单元格
							   for(int s=a1;s<a1+colspan;s++){
								   row.createCell(s);
								  row.getCell(s).setCellStyle(sheet.getRow(startRowNum).getCell(a1).getCellStyle());
							   }
							   sheet.addMergedRegion(new CellRangeAddress(startRowNum+i, startRowNum+i, a1, a1+colspan-1));
						   }
						  
						  
							if(cellType==1){//公式类型
								cell.setCellFormula(sheet.getRow(startRowNum).getCell(a1).getCellFormula());
							}else{	 
								 if(getVauleType(type)==0&&a2!=""){
									  //cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									  cell.setCellValue(Double.parseDouble(a2));
								  }else{
									  cell.setCellValue(a2);
								  }
							}
						  
						  cell.setCellStyle(sheet.getRow(startRowNum).getCell(a1).getCellStyle());
						 cell.setCellType(sheet.getRow(startRowNum).getCell(a1).getCellType());
						 
						}else{
							if(cellType!=1){
							 if(getVauleType(type)==0&&a2!=""){
								sheet.getRow(startRowNum).getCell(a1).setCellValue(Double.parseDouble(a2));
							 }else{
								 sheet.getRow(startRowNum).getCell(a1).setCellValue(a2);
							 }
							}
						}
					}
					
				}
				
				List totalList = (List)excelSettingMap.get("totalList");
				//写入固定单元格数据（简单属性）
				for(Iterator iter = totalList.iterator();iter.hasNext();){
					Map data = (Map)iter.next();
					PropertyDescriptor pd = null;
					Class type=null;
					int space=Integer.parseInt(data.get("space").toString());
					try {
						pd = new PropertyDescriptor(data.get("name").toString(),object.getClass());
					} catch (IntrospectionException e) {
						e.printStackTrace();
					}
					Method method = pd.getReadMethod();
					Object total = null;
					try {
						total = method.invoke(object);
						type=method.getReturnType();
						if(total==null){
							total = "";
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				
					if(getVauleType(type)==0&&value!=""&&total!=""){
						 data.get(startRowNum+space+items.size()-1);
						sheet.getRow(startRowNum+space+items.size()).getCell(Integer.parseInt(data.get("cell").toString())).setCellValue(Double.parseDouble(total.toString()));
					}else{
						sheet.getRow(startRowNum+space+items.size()).getCell(Integer.parseInt(data.get("cell").toString())).setCellValue(total.toString());
					}
				}
	}
	
	
	
	private int getVauleType(Class type){
		 if(type.getName().equals("java.lang.Integer")||type.getName().equals("java.lang.Double")||type.getName().equals("java.lang.Float")||type.getName().equals("java.math.BigDecimal")){
			 return 0;	
		 }
		 return 1;
		
	}
	
	
	

}
