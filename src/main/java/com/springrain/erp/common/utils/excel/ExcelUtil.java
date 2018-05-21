package com.springrain.erp.common.utils.excel;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelUtil {
	public static List<String[]> read(InputStream inputStream) throws Exception {
		List<String[]> result = new ArrayList<String[]>();
		Workbook wb = WorkbookFactory.create(inputStream);
		try {	
				Sheet sheet = wb.getSheet("template");
				if(sheet==null){
					sheet = wb.getSheet("Template");
				}
				if(sheet==null){
					sheet = wb.getSheet("TEMPLATE");
				}
				if(sheet==null){
					sheet = wb.getSheetAt(0);
				}
				sheet.setForceFormulaRecalculation(true);
				if (sheet.getSheetName() != null
						&& !"".equals(sheet.getSheetName().trim())) {
					int rows = sheet.getPhysicalNumberOfRows();
					if (rows <= 1)
						throw new Exception("Excel表单中没数据!");
					Row tabHeaderInExcel = sheet.getRow(0);
					if (tabHeaderInExcel == null)
						throw new Exception("Excel表单中没有找到表头!");
					int cells = tabHeaderInExcel.getPhysicalNumberOfCells();
					for (int r = 0; r < rows; r++) {
						Row row = sheet.getRow(r);
						if (row == null)
							continue;
						String[] excelCols = new String[cells];
						for (int c = 0; c < cells; c++) {
							Cell cell = row.getCell(c);
							if (cell == null){
								continue;
							}	
							Object value = null;
							int type = cell.getCellType();
							switch (type) {
							case Cell.CELL_TYPE_NUMERIC:
								SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
								if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
									value = simpleDateFormat.format(cell.getDateCellValue());
									break;
//								m月d日
								} 
								else if (cell.getCellStyle().getDataFormat() == 58) {
									value = simpleDateFormat.format(cell.getDateCellValue());
									break;
//								yyyy-MM-dd
								}else if(cell.getCellStyle().getDataFormat() == 14) {
									value = simpleDateFormat.format(cell.getDateCellValue());
									break;
//								yyyy年m月d日
								}else if(cell.getCellStyle().getDataFormat() == 31) {
									value = simpleDateFormat.format(cell.getDateCellValue());
									break;
//								yyyy年m月
							    }else if(cell.getCellStyle().getDataFormat() == 57) {
									value = simpleDateFormat.format(cell.getDateCellValue());
									break;
//								HH:mm
								}else if(cell.getCellStyle().getDataFormat() == 20) {
									value = simpleDateFormat.format(cell.getDateCellValue());
									break;
//								h时mm分
								}else if(cell.getCellStyle().getDataFormat() == 32) {
									value = simpleDateFormat.format(cell.getDateCellValue());
									break;
								}else {
									value =  cell.getNumericCellValue();
									//这里处理 数字的科学计算法
									if(value.toString().contains("E")){
										DecimalFormat bdf=new DecimalFormat("#");
										value = bdf.format(value);
										
									}
									break;
								}
							case Cell.CELL_TYPE_STRING:
								value = cell.getStringCellValue().trim();
								break;
							case Cell.CELL_TYPE_BOOLEAN:
								value = cell.getBooleanCellValue();
								break;
							case Cell.CELL_TYPE_ERROR:
								value = "";
								break;
							case Cell.CELL_TYPE_FORMULA:
								try{//获取excel单元格中的计算公式(这里获取的是excel单元格中计算出来的值)
									value=String.valueOf(cell.getNumericCellValue());
								}catch(IllegalStateException e){
									value=String.valueOf(cell.getRichStringCellValue());
								}
								break;
							case Cell.CELL_TYPE_BLANK:
								value = "";
							default:
								continue;
							}
							if (value == null)
								continue;
							excelCols[c] = value.toString();
						}
						for (String str : excelCols) {
							if(str!=null&&str.length()>0){
								result.add(excelCols);
								break;
							}
						}
					}
				}
			return result;
		} catch (IOException e) {
			throw new Exception(e.getMessage());
		} catch (InvalidFormatException e) {
			throw new Exception(e.getMessage());
		}catch (Exception e) {
			throw new Exception(e.getMessage());
		}

	}
	/*public static void main(String[] args) {
		try {
			
			//Workbook wb = new XSSFWorkbook("d:/111/MP1503.xlsx"); //WorkbookFactory.create(new File("d:/111/MP1503.xlsx"));
			WorkbookFactory.create(new File("d:/111/MP1503.xlsx"));
			
			System.out.println(1);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
