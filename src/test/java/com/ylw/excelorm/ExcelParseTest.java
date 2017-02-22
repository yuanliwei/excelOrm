package com.ylw.excelorm;

import java.io.File;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ylw.excelorm.model.Vocebulary;

public class ExcelParseTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		// fail("Not yet implemented");
		File file = new File("src/test/resources/工作簿1.xlsx");

		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
		XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

		int rowstart = xssfSheet.getFirstRowNum();
		int rowEnd = xssfSheet.getLastRowNum();
		for (int i = rowstart; i <= rowEnd; i++) {
			XSSFRow row = xssfSheet.getRow(i);
			if (null == row)
				continue;
			int cellStart = row.getFirstCellNum();
			int cellEnd = row.getLastCellNum();
			System.out.print("cellStart : " + cellStart);
			for (int k = cellStart; k <= cellEnd; k++) {
				XSSFCell cell = row.getCell(k);
				if (null == cell)
					continue;

				switch (cell.getCellType()) {
				case HSSFCell.CELL_TYPE_NUMERIC: // 数字
					System.out.print("数字 " + cell.getNumericCellValue() + "   ");
					break;
				case HSSFCell.CELL_TYPE_STRING: // 字符串
					System.out.print("字符串 " + cell.getStringCellValue() + "   ");
					break;
				case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
					System.out.println("布尔 " + cell.getBooleanCellValue() + "   ");
					break;
				case HSSFCell.CELL_TYPE_FORMULA: // 公式
					System.out.print("公式 " + cell.getCellFormula() + "   ");
					break;
				case HSSFCell.CELL_TYPE_BLANK: // 空值
					System.out.println("空值  ");
					break;
				case HSSFCell.CELL_TYPE_ERROR: // 故障
					System.out.println("故障  ");
					break;
				default:
					System.out.print("未知类型   ");
					break;
				}

			}
			System.out.print("\n");
		}
	}

	@Test
	public void testParse() throws Exception {
		String excel = "src/test/resources/工作簿1.xlsx";
		ExcelParse.parse(excel, Vocebulary.class);
	}

}
