package com.semika;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class MainTestMarathonFile {

	public static void main(String[] args) throws FileNotFoundException {
		
		Date date = new Date(); 
		
		FileInputStream inputStream = new FileInputStream(new File("/Users/admin/Documents/Ascent_New_File.xlsx"));

			try {
				Workbook wb = WorkbookFactory.create(inputStream);
				Sheet mySheet = wb.getSheetAt(0);
				String record = "";

				// processing time, inserted as first column
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm"); 

				Iterator<Row> rowIter = mySheet.rowIterator();
				int rowNum = 0;
				
				while (rowIter.hasNext()) {
					rowNum++;
					Row nextRow = rowIter.next();
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					
					while (cellIterator.hasNext()) {
						
						Cell cell = cellIterator.next();
						
						if (cell.getColumnIndex() <= 25) {
							
							//System.out.println(cell.getColumnIndex() + ":" + cell.getCellType());
							
							if (rowNum == 1) { //Header row
								if (!cell.getCellType().equals(CellType.BLANK)) {
									record = record + cell.getStringCellValue() + ',';
								}
							} else {
								
								if (cell.getCellType().equals(CellType.NUMERIC)) {
									
									switch(cell.getColumnIndex()) {
										case 2:
										case 3:
										case 7:
										case 8:
										case 19:
										case 20:
											Double cellValue = cell.getNumericCellValue(); 
											record = record + cellValue.longValue() + ',';
											//System.out.println(cell.getColumnIndex() + ":" + cell.getCellType() + ":" + cellValue.longValue());
											break;
										default:
											if (HSSFDateUtil.isCellDateFormatted(cell)) {
												record = record + sdf.format(cell.getDateCellValue()) + ','; 
											} else {
												record = record + cell.getNumericCellValue() + ',';
											}
											break;
									
									}
									
									
								} else if (cell.getCellType().equals(CellType.STRING)) {
									
									String strCellValue = cell.getStringCellValue();
									//System.out.println(cell.getColumnIndex() + ":" + cell.getCellType() + ":" + strCellValue);
									
									//remove new lines.
									if (strCellValue.indexOf("\n") != -1) {
										strCellValue = strCellValue.replace("\n", " "); 
									}
									
									//Escape commas 
									if (strCellValue.indexOf(",") != -1) {
										strCellValue = "\"" + strCellValue + "\"";
									}
									
									record = record + strCellValue + ',';
									
									//System.out.println(cell.getStringCellValue() + ":" + cell.getCellType()); 
								} 
							}
						}
					} 
					
					//System.out.println(record + "***"+ "\n"); 
					//Add new line and remove last comma
					if (record.trim().length() > 0) {
						record = record.substring(0, record.lastIndexOf(","))  + '\n'; 
					}
					System.out.println(record); 
					record = "";
					//break;
				}

			}
			catch(Exception e) {
				e.printStackTrace(); 
			}
    }

}