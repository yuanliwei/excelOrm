package com.ylw.excelorm;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ylw.excelorm.model.Vocebulary;

public class ExcelParseTest {
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(ExcelParseTest.class);
	
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

	@SuppressWarnings("deprecation")
	@Test
	public void testReadExcel() throws Exception {
		// http://poi.apache.org/spreadsheet/quick-guide.html#CellContents

		File file = new File("src/test/resources/工作簿1.xlsx");

		XSSFWorkbook wb = new XSSFWorkbook(file);
		DataFormatter formatter = new DataFormatter();
		Sheet sheet1 = wb.getSheetAt(0);
		for (Row row : sheet1) {
			for (Cell cell : row) {
				CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
				log.debug(cellRef.formatAsString());
				log.debug(" - ");

				// get the text that appears in the cell by getting the cell
				// value and applying any data formats (Date, 0.00, 1.23e9,
				// $1.23, etc)
				String text = formatter.formatCellValue(cell);
				log.debug(text);

				// Alternatively, get the value and format it yourself
				switch (cell.getCellTypeEnum()) {
				case STRING:
					log.debug(cell.getRichStringCellValue().getString());
					break;
				case NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						log.debug(cell.getDateCellValue());
					} else {
						log.debug(cell.getNumericCellValue());
					}
					break;
				case BOOLEAN:
					log.debug(cell.getBooleanCellValue());
					break;
				case FORMULA:
					log.debug(cell.getCellFormula());
					break;
				case BLANK:
					log.debug("");
					break;
				default:
					log.debug("");
				}
			}
		}
		wb.close();
	}

	@Test
	public void testParse() throws Exception {
		String excel = "src/test/resources/工作簿1.xlsx";
		List<Vocebulary> vocebularies = ExcelParser.parse(excel, Vocebulary.class);
		vocebularies.forEach(action -> {
			log.debug(action.toString());
		});
	}

}
