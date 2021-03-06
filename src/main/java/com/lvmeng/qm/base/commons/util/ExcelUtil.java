package com.lvmeng.qm.base.commons.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.lvmeng.qm.base.commons.CodeTable;
import com.lvmeng.qm.base.vo.SetString;
import com.lvmeng.qm.base.vo.questionnaire.Questionnaire;

public class ExcelUtil {
	/**
	 * 读取Excel并封装为对象
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static List<Questionnaire> toVO(InputStream file) throws Exception{
		HSSFWorkbook workbook = null;
		List<Questionnaire> list = new ArrayList<>();
		try {
			workbook = new HSSFWorkbook(file);
			if (workbook != null && workbook.getNumberOfSheets() > 0) {
				HSSFSheet sheet = workbook.getSheetAt(0);
				if (sheet != null && sheet.getLastRowNum() > 1) {
					if (sheet.getRow(0).getLastCellNum() != 179){
						throw new Exception("总列数不等于179,请检查表格");
					}
					int rows = sheet.getLastRowNum();
					for (int i = 1; i <= rows; i++) {
						HSSFRow row = sheet.getRow(i);
						if (row == null) {
							continue;
						}
						int cells = row.getLastCellNum();
						Questionnaire qn = new Questionnaire();
						List<String> baseInfo = new ArrayList<>();
						List<String> questionnaire = new ArrayList<>();
						//遍历每一个cell
						for (int j = 0; j < cells; j++) {
							HSSFCell cell = row.getCell(j);
							//将cell取值为String
							String value = getStringCellValue(cell);
							//下标为41(包含41)以前的表格为联系人基本信息
							if (j <= 41) {
								baseInfo.add(value);
								setQnFieldsValue(qn, j, value);
							}else {//41以后的为问卷答案
								questionnaire.add(value);
							}
						}
						qn.setBaseInfo(baseInfo);
						qn.setQuestionnaire(questionnaire);
						list.add(qn);
					}
				}else {
					throw new Exception("第一个sheet没有数据");
				}
			}else {
				throw new Exception("工作簿不存在或工作簿是空的");
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (workbook != null) {
				workbook.close();
			}
		}
		return list;
	}

	
	/**
	 * 将对应列的值赋值给联系人基本信息相关字段
	 * @param qn
	 * @param j
	 * @param value
	 */
	private static void setQnFieldsValue(Questionnaire qn, int j, String value) {
		switch (j) {
		case 0:
			qn.setQnId(intValueOf(value));
			break;
		case 1:
			qn.setPanelId(intValueOf(value));
			break;
		case 2:
			qn.setStartTime(value);
			break;
		case 3:
			qn.setEndTime(value);
			break;
		case 16:
			qn.setCustomerName(value);
			break;
		case 17:
			qn.setName(value);
			break;
		case 18:
			qn.setEmail(value);
			break;
		case 19:
			qn.setPhone(value);
			break;
		case 20:
			qn.setTelephone(value);
			break;
		case 22:
			qn.setProName(value);
			break;
		case 23:
			qn.setSaler(value);
			break;
		case 24:
			qn.setSecondBranch(value);
			break;
		case 26:
			qn.setProManager(value);
			break;
		case 27:
			qn.setSerialNum(value);
			break;
		case 28:
			qn.setProductName(value);
			break;
		case 31:
			qn.setProCoder(value);
			break;
		case 37:
			qn.setAddress(value);
			break;
		case 38:
			qn.setEngineer(value);
			break;
		case 39:
			qn.setProvince(value);
			break;
		default:
			break;
		}
	}
	
	private static Integer intValueOf(String value) {
		if (StringUtils.isNumeric(value)) {
			return Integer.valueOf(value);
		}
		return 0;
	}

	/**
	 * 将每个cell取值为String
	 * @param cell
	 * @return
	 */
	private static String getStringCellValue(HSSFCell cell) {
		String value;
		if (cell == null) {
			return "";
		}
		switch (cell.getCellTypeEnum()) {
		case NUMERIC:
			short format = cell.getCellStyle().getDataFormat();  
		    SimpleDateFormat sdf = null;  
		    if(format == 14 || format == 31 || format == 57 || format == 58 || format == 22){  
		        //日期  
		        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		    }else if (format == 20 || format == 32) {  
		        //时间  
		        sdf = new SimpleDateFormat("HH:mm");  
		    }
		    if (sdf == null) {
		    	value = (long)cell.getNumericCellValue() + "";
		    }else {
		    	double va = cell.getNumericCellValue();
		    	Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(va);
		    	value = sdf.format(date);
		    }
			break;
		case STRING:
			value = cell.getStringCellValue();
			break;
		case BOOLEAN:
			value = cell.getBooleanCellValue() + "";
			break;
		default:
			value = "";
			break;
		}
		return value;
	}
	
	public static <T> void toExcel(OutputStream out, String[] headers, List<T> list, Class<?> c){
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		sheet.setDefaultColumnWidth((short) 25);
		HSSFRow row = sheet.createRow(0);
		if (ArrayUtils.isNotEmpty(headers)) {
			for (short i = 0; i < headers.length; i++) {
				HSSFCell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
			}
		}
		int lastRowNum = 1;
		if (!list.isEmpty()) {
			List<Field> fields = new ArrayList<>();
			while (c != null && !c.getName().toLowerCase().equals("java.lang.object")) {//当父类为null的时候说明到达了最上层的父类(Object类).
				fields.addAll(Arrays.asList(c.getDeclaredFields()));
				c = c.getSuperclass(); //得到父类,然后赋给自己
			}
			List<Field> newFields = new ArrayList<>();
			//排序算法有待优化
			for (String fieldName : CodeTable.fieldSort) {
				for (Field field : fields) {
					if (field.getName().equals(fieldName)) {
						newFields.add(field);
					}
				}
			}
			for (T t : list) {
				row = sheet.createRow(lastRowNum++);
				int i = 0;
				for (Field field : newFields) {
		            String fieldName = field.getName();
		            String getMethodName = "get"
		                   + fieldName.substring(0, 1).toUpperCase()
		                   + fieldName.substring(1);
		            try {
		                Method getMethod = t.getClass().getMethod(getMethodName, new Class[] {});
		                Object value = getMethod.invoke(t, new Object[] {});
		                if ((value instanceof Set) ) {
		                	@SuppressWarnings("unchecked")
							Set<SetString> set = (Set<SetString>)value;
		                	for (SetString s : set) {
		                		HSSFCell cell = row.createCell(fields.size()-1+s.getIndex());
								cell.setCellValue(s.toString());
							}
		                }else {
		                	if (value != null) {
		                		HSSFCell cell = row.createCell(i++);
		                		cell.setCellValue(value.toString());
		                	}
		                }
		            }catch (Exception e) {
		            	e.printStackTrace();
					}
				}
			}
		}
		try {
			workbook.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 创建新sheet
	 * @param workbook
	 * @param sheetName
	 * @param headers
	 * @param list
	 * @param c
	 * @throws Exception
	 */
	public static <T> void toSheet(HSSFWorkbook workbook, String sheetName, String[] headers, List<T> list, Class<?> c) throws Exception{
		HSSFSheet sheet = workbook.createSheet(sheetName);
		sheet.setDefaultColumnWidth((short) 25);
		HSSFRow row = sheet.createRow(0);
		//设置表格的表头
		if (ArrayUtils.isNotEmpty(headers)) {
			for (short i = 0; i < headers.length; i++) {
				HSSFCell cell = row.createCell(i);
				cell.setCellValue(headers[i]);
			}
		}
		int lastRowNum = 1;
		if (!list.isEmpty()) {
			List<Field> fields = new ArrayList<>();
			//反射获取对应类的所有属性   包含其父类的属性(到Object以下)
			while (c != null && !c.getName().toLowerCase().equals("java.lang.object")) {//当父类为null的时候说明到达了最上层的父类(Object类).
				fields.addAll(Arrays.asList(c.getDeclaredFields()));
				c = c.getSuperclass(); //得到父类,然后赋给自己
			}
			List<Field> newFields = new ArrayList<>();
			//排序算法有待优化
			//将所有字段按照正确的顺序排列
			for (String fieldName : CodeTable.fieldSort) {
				for (Field field : fields) {
					if (field.getName().equals(fieldName)) {
						newFields.add(field);
					}
				}
			}
			for (T t : list) {
				
				Object obj = t.getClass().getMethod("getQuestionnaire", new Class[] {}).invoke(t, new Object[] {});
				//取值后问卷内容为空的不添加到Excel中
				@SuppressWarnings("unchecked")
				Set<SetString> qn = (Set<SetString>)obj;
				if (qn.isEmpty()){
					continue;
				}
				
				row = sheet.createRow(lastRowNum++);
				int i = 0;
				//按照排列好的字段列表的顺序依次取值
				for (Field field : newFields) {
					String fieldName = field.getName();
					String getMethodName = "get"
							+ fieldName.substring(0, 1).toUpperCase()
							+ fieldName.substring(1);
					try {
						//反射调用getter方法取值
						Method getMethod = t.getClass().getMethod(getMethodName, new Class[] {});
						Object value = getMethod.invoke(t, new Object[] {});
						if ((value instanceof Set) ) {
							@SuppressWarnings("unchecked")
							Set<SetString> set = (Set<SetString>)value;
							for (SetString s : set) {
								//按照SetString的下标index值创建cell
								HSSFCell cell = row.createCell(fields.size()-1+s.getIndex());
								cell.setCellValue(s.toString());
							}
						}else {
							if (value != null) {
								HSSFCell cell = row.createCell(i++);
								cell.setCellValue(value.toString());
							}
						}
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
