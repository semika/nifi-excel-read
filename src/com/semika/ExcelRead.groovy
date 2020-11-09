import org.apache.commons.io.IOUtils
import java.nio.charset.*
import java.text.SimpleDateFormat

import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
 
def flowFile = session.get()
if(!flowFile) return

def date = new Date()
 
flowFile = session.write(flowFile, {inputStream, outputStream ->
    try {
		
		Workbook wb = WorkbookFactory.create(inputStream,);
		Sheet mySheet = wb.getSheetAt(0);
		def record = ''
   
		// processing time, inserted as first column
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm"); 
		
		Iterator<Row> rowIter = mySheet.rowIterator();
		def rowNum = 0
		while (rowIter.hasNext()) {
			rowNum++
			Row nextRow = rowIter.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();
              
			while (cellIterator.hasNext()) {
				
				Cell cell = cellIterator.next();

				if (rowNum == 1) { //Header row
					//System.out.println(cell.getStringCellValue() + ":" + cell.getCellType());
					if (!cell.getCellType().equals(CellType.BLANK)) {
						record = record + cell.getStringCellValue() + ',';
					}
				} else {
					if (cell.getCellType().equals(CellType.NUMERIC)) {
						if (HSSFDateUtil.isCellDateFormatted(cell)) {
							record = record + sdf.format(cell.getDateCellValue()) + ','; 
						} else {
							record = record + cell.getNumericCellValue() + ',';
						}
					} else if (cell.getCellType().equals(CellType.STRING)) {
						String strCellValue = cell.getStringCellValue();
						//remove new lines.
						strCellValue = strCellValue.replace("\n", " ");

						//Escape commas
						if (strCellValue.indexOf(",") != -1) {
							strCellValue = "\"" + strCellValue + "\"";
						}

						record = record + strCellValue + ',';
					} else {
						//nothing happening here..
					}
				}
			}
			
			//Add new line and remove last comma
			if (record.trim().length() > 0) {
				record = record.substring(0, record.lastIndexOf(","))  + '\n'; 
			}
			outputStream.write(record.getBytes(StandardCharsets.UTF_8))
			
            if(rowNum > 1) {
                // insert tstamp, row num, drop last comma and add end line.  
				// Note: tstamp + row num are composite key
              //record = tstamp + ',' + rowNum + ',' + record[0..-2] + '\n'
              //outputStream.write(record.getBytes(StandardCharsets.UTF_8))
            }
             record = ''
         }
   
    }
    catch(e) {
     log.error("Error during processing of spreadsheet name = xx, sheet = xx", e)
     //session.transfer(inputStream, REL_FAILURE)
    }
} as StreamCallback)
 
def filename = flowFile.getAttribute('filename').split('\\.')[0] + '.csv'
//def filename = flowFile.getAttribute('filename').split('\\.')[0] + '_' + new SimpleDateFormat("YYYYMMdd-HHmmss").format(date)+'.csv'
flowFile = session.putAttribute(flowFile, 'filename', filename) 

session.transfer(flowFile, REL_SUCCESS)