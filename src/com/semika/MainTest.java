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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class MainTest {

	public static void main(String[] args) throws FileNotFoundException {
		
		Date date = new Date(); 
		
		FileInputStream inputStream = new FileInputStream(new File("/Users/semika/Documents/PFJ_EncinoAutomated_Report_Last100.xlsx"));

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
						
						if (cell.getColumnIndex() <= 36) {
							
							if (rowNum == 1) { //Header row
								//System.out.println(cell.getCellType()); 
								//System.out.println(cell.getStringCellValue() + ":" + cell.getCellType());
								if (!cell.getCellType().equals(CellType.BLANK)) {
									record = record + cell.getStringCellValue() + ',';
								}
							} else {
								
								if (cell.getCellType().equals(CellType.NUMERIC)) {
									
									if (HSSFDateUtil.isCellDateFormatted(cell)) {
										record = record + sdf.format(cell.getDateCellValue()) + ','; 
									} else if (cell.getColumnIndex() == 1) {
										Double cellValue = cell.getNumericCellValue(); 
										record = record + cellValue.longValue() + ',';
										//System.out.println(cell.getColumnIndex() + ":" + cell.getCellType() + ":" + cellValue.longValue());
									} else {
										record = record + cell.getNumericCellValue() + ',';
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
								} else if (cell.getColumnIndex() == 33 && cell.getCellType().equals(CellType.BLANK)) {
									//System.out.println(cell.getStringCellValue().length()); 
									record = record + cell.getStringCellValue() + ',';
								}
							}
						}
					} 
					
					//System.out.println(record + "***"+ "\n"); 
					//Add new line and remove last comma
					if (record.trim().length() > 0) {
						record = record.substring(0, record.lastIndexOf(","))  + '\n'; 
					}

					//if(rowNum > 1) {
						// insert tstamp, row num, drop last comma and add end line.
						// Note: tstamp + row num are composite key
						//record = tstamp + ',' + rowNum + ',' + record[0..-2] + '\n'
						//outputStream.write(record.getBytes(StandardCharsets.UTF_8))
						
						System.out.println(record); 
					//}
					record = "";
					//break;
				}

			}
			catch(Exception e) {
				e.printStackTrace(); 
				//log.error("Error during processing of spreadsheet name = xx, sheet = xx", e);
				//session.transfer(inputStream, REL_FAILURE)
			}
		//} as StreamCallback)

		//def filename = flowFile.getAttribute('filename').split('\\.')[0] + '_' + new SimpleDateFormat("YYYYMMdd-HHmmss").format(date)+'.csv'
		//flowFile = session.putAttribute(flowFile, 'filename', filename)
		//session.transfer(flowFile, REL_SUCCESS)
    }

}