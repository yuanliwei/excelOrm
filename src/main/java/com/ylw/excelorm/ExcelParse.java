package com.ylw.excelorm;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ylw.excelorm.annotation.Cell;
import com.ylw.excelorm.annotation.ErrorHandle;
import com.ylw.excelorm.annotation.Excel;

public class ExcelParse<T> {

	private static org.apache.commons.logging.Log log = LogFactory.getLog(ExcelParse.class);

	public static <T> List<T> parse(String excel, Class<T> clazz) throws Exception {
		File file = new File(excel);

		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
		Excel[] annotation = clazz.getAnnotationsByType(Excel.class);
		int sheet = annotation[0].sheet();
		XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(sheet);

		Field[] fileds = clazz.getDeclaredFields();
		Map<Integer, FieldObj> fieldMap = new HashMap<Integer, FieldObj>();
		for (int i = 0; i < fileds.length; i++) {
			Field field = fileds[i];
			Cell cellAnnotation = field.getAnnotation(Cell.class);
			if (cellAnnotation != null) {
				String setName = "set" + toUpperCaseFirstOne(field.getName());
				Method setMethod = clazz.getDeclaredMethod(setName, field.getType());
				if (setMethod == null) {
					log.debug("set method \"" + setName + "\" not exist!");
				}
				int col = cellAnnotation.col();
				fieldMap.put(col, new FieldObj(col, field, setMethod, field.getType().getName()));
				log.debug("field type name : " + field.getType().getName());
			}
		}

		Method[] methods = clazz.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			ErrorHandle errorHandleAnnotation = method.getAnnotation(ErrorHandle.class);
			if (errorHandleAnnotation != null) {
				int col = errorHandleAnnotation.col();
				FieldObj fieldObj = fieldMap.get(col);
				if (fieldObj != null) {
					fieldObj.setErrorHandle(method);
				}
			}
		}

		int rowstart = annotation[0].firstRow();
		int rowEnd = xssfSheet.getLastRowNum();
		List<T> list = new ArrayList<>();

		for (int i = rowstart; i <= rowEnd; i++) {
			XSSFRow row = xssfSheet.getRow(i);
			if (null == row)
				continue;
			list.add(getEntry(row, fieldMap, clazz));
		}
		xssfWorkbook.close();
		return list;
	}

	@SuppressWarnings("deprecation")
	private static <T> T getEntry(XSSFRow row, Map<Integer, FieldObj> fieldMap, Class<T> clazz) {
		T obj = null;
		try {
			obj = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e2) {
			log.error(e2);
			return null;
		}
		Iterator<Entry<Integer, FieldObj>> it = fieldMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, FieldObj> entry = (Map.Entry<Integer, FieldObj>) it.next();
			XSSFCell cell = row.getCell(entry.getKey());
			if (cell == null)
				continue;
			FieldObj fieldObj = entry.getValue();

			Object value = null;

			switch (cell.getCellTypeEnum()) {
			case STRING:
				value = cell.getRichStringCellValue().getString();
				break;
			case NUMERIC:
				value = cell.getNumericCellValue();
				break;
			case BOOLEAN:
				value = cell.getBooleanCellValue();
				break;
			case FORMULA:
				value = cell.getCellFormula();
				break;
			case BLANK:
				value = null;
				break;
			default:
			}

			if (value != null) {
				try {
					switch (fieldObj.getName()) {
					case "int":
						value = (int) ((Double) value).doubleValue();
						break;
					case "float":
						value = (float) ((Double) value).doubleValue();
						break;
					case "java.lang.String":
						value = (String) value;
						break;

					default:
						throw new IllegalStateException("unknown type : " + fieldObj.getName());
					}
				} catch (Exception e) {
					try {
						if (fieldObj.getErrorHandle() != null)
							value = fieldObj.getErrorHandle().invoke(obj, value);
						else {
							log.error(e);
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
						log.error(e1);
					}
				}
			}

			try {
				fieldObj.getSetMethod().invoke(obj, value);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error(e);
			}

		}
		return obj;
	}

	public static String toUpperCaseFirstOne(String name) {
		char[] ch = name.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if (i == 0) {
				ch[0] = Character.toUpperCase(ch[0]);
			} else {
				ch[i] = Character.toLowerCase(ch[i]);
			}
		}
		StringBuffer a = new StringBuffer();
		a.append(ch);
		return a.toString();
	}
}
