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

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ylw.excelorm.annotation.Cell;
import com.ylw.excelorm.annotation.ErrorHandle;
import com.ylw.excelorm.annotation.Sheet;
import com.ylw.excelorm.model.Vocebulary;

public class ExcelParse {
	public static List<Object> parse(String excel, Class<?> clazz) throws Exception {
		File file = new File(excel);

		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
		Sheet[] annotation = clazz.getAnnotationsByType(Sheet.class);
		int order = annotation[0].order();
		XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(order);

		Field[] fileds = clazz.getDeclaredFields();
		Map<Integer, FieldObj> fieldMap = new HashMap<Integer, FieldObj>();
		for (int i = 0; i < fileds.length; i++) {
			Field field = fileds[i];
			Cell cellAnnotation = field.getAnnotation(Cell.class);
			if (cellAnnotation != null) {
				String setName = "set" + toUpperCaseFirstOne(field.getName());
				Method setMethod = clazz.getDeclaredMethod(setName, field.getType());
				if (setMethod == null) {
					System.out.println("set method \"" + setName + "\" not exist!");
				}
				int index = cellAnnotation.value();
				fieldMap.put(index, new FieldObj(index, field, setMethod, field.getType().getName()));
				System.out.println("field type name : " + field.getType().getName());
			}
		}

		Method[] methods = clazz.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			ErrorHandle errorHandleAnnotation = method.getAnnotation(ErrorHandle.class);
			if (errorHandleAnnotation != null) {
				int index = errorHandleAnnotation.value();
				FieldObj fieldObj = fieldMap.get(index);
				if (fieldObj != null) {
					fieldObj.setErrorHandle(method);
				}
			}
		}

		// int rowstart = xssfSheet.getFirstRowNum();
		int rowstart = annotation[0].firstRow();
		int rowEnd = xssfSheet.getLastRowNum();
		List<Object> list = new ArrayList<Object>();

		for (int i = rowstart; i <= rowEnd; i++) {
			XSSFRow row = xssfSheet.getRow(i);
			if (null == row)
				continue;
			list.add(getEntry(row, fieldMap));
			System.out.println(getEntry(row, fieldMap).toString());
			int cellStart = row.getFirstCellNum();
			int cellEnd = row.getLastCellNum();
			// System.out.print("cellStart : " + cellStart);
		}
		xssfWorkbook.close();
		return null;
	}

	private static Object getEntry(XSSFRow row, Map<Integer, FieldObj> fieldMap) {
		Vocebulary obj = new Vocebulary();
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
				// System.out.println(cell.getRichStringCellValue().getString());
				value = cell.getRichStringCellValue().getString();
				break;
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					// System.out.println(cell.getDateCellValue());
				} else {
					// System.out.println(cell.getNumericCellValue());
				}
				value = cell.getNumericCellValue();
				break;
			case BOOLEAN:
				// System.out.println(cell.getBooleanCellValue());
				value = cell.getBooleanCellValue();
				break;
			case FORMULA:
				// System.out.println(cell.getCellFormula());
				value = cell.getCellFormula();
				break;
			case BLANK:
				// System.out.println();
				value = null;
				break;
			default:
				// System.out.println();
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
						System.err.println("unknown type : " + fieldObj.getName());
						break;
					}
				} catch (Exception e) {
					try {
						if (fieldObj.getErrorHandle() != null)
							value = fieldObj.getErrorHandle().invoke(obj, value);
						else {
							e.printStackTrace();
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
						e1.printStackTrace();
					}
				}
			}

			try {
				fieldObj.getSetMethod().invoke(obj, value);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
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
