/**
 * 
 */
package com.pse.edge.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pse.edge.model.Stock;
import com.pse.edge.utils.ProjConstants;

/**
 * Generates data to the designated spreadsheet
 * 
 * @author MC
 *
 */
public class ExcelExporter implements Exporter {
  
  //date format for sheet naming
  private final String DATE_FORMAT = "ddMMMyyy HHmmss";
  
  private String filename;
  private ArrayList<Stock> stockList = new ArrayList<Stock>();
  
  //static column headers
  static final List<String> HEADERS = 
      Arrays.asList(
          "Stock Name", 
          "Stock Symbol",
          "Current Price",
          "52-week High",
          "52-week Low",
          "Buy at for 50% Gain",
          "Percentage from Target Buy");
  
  /**
   * Perform excel export
   * 
   * 1. Create excel workbook
   * 2. Create sheet within the workbook
   * 3. Create a row inside the sheet
   * 4. Create cells inside each row
   */
  public void export() throws Exception {
	//create excel workbook
    try (XSSFWorkbook wb = getXlsWorkBook()) {
      //create sheet
      String sheetName = (new SimpleDateFormat(DATE_FORMAT)).
          format(Calendar.getInstance().getTime());
      XSSFSheet sheet = wb.createSheet(sheetName);
      //create header row for the sheet
      generateHeaderRow(wb, sheet);
      
      int stockSize = getStockList().size();
      //create row for each stock
      for (int a=1; a<=stockSize; a++) {
        Stock currStock = getStockList().get(a-1);
        Row row = sheet.createRow(a);
        
        //name of the stock
        Cell nameCell = row.createCell(0);
        nameCell.setCellValue(currStock.getName());
        
        //symbol of the stock
        Cell symbolCell = row.createCell(1);
        symbolCell.setCellValue(currStock.getSymbol());
        
        //current price of the stock
        Cell priceCell = row.createCell(2);
        priceCell.setCellValue(String.valueOf(currStock.getPrice()));
        
        //52-week high of the stock
        Cell high52cell = row.createCell(3);
        high52cell.setCellValue(String.valueOf(currStock.getFiftyTwoHigh()));
        
        //52-week low of the stock
        Cell low52cell = row.createCell(4);
        low52cell.setCellValue(String.valueOf(currStock.getFiftyTwoLow()));
        
        //Price to buy for 50 percent gain
        Cell fiftyGainCell = row.createCell(5);
        fiftyGainCell.setCellValue(String.valueOf(currStock.getPrice50Gain()));
        
        //Percent difference from price to buy for 50 percent gain
        //This indicates to user how far the price is for buying for potential 50
        //percent gain
        Cell diffFiftyGainCell = row.createCell(6);
        diffFiftyGainCell.setCellValue(String.valueOf(currStock.getPercDiffPrice50Gain()));
      }
      
      //fit width to contents
      for (int k=0; k<HEADERS.size(); k++) {
        sheet.autoSizeColumn(k);
      }
      
      //write workbook to file
      try (OutputStream fileOut = new FileOutputStream(getFilename())) {
        wb.write(fileOut);
      }
    }
  }

  /**
   * Get the existing xls file or create one if none exists
   * 
   * @return
   * @throws Exception
   */
  @SuppressWarnings("resource")
  private XSSFWorkbook getXlsWorkBook() throws Exception {
    File xlsFile = new File(getFilename());
    if (xlsFile.isFile() && xlsFile.exists()) {
      return new XSSFWorkbook(new FileInputStream(xlsFile));
    }
    return new XSSFWorkbook();
  }

  /**
   * Generate the header row
   * 
   * @param wb
   * @param sheet
   */
  private void generateHeaderRow(XSSFWorkbook wb, XSSFSheet sheet) {
    Row row = sheet.createRow(0); //write on the first row
    for (int k=0; k<HEADERS.size(); k++) {
      Cell newCell = row.createCell(k);
      newCell.setCellValue(HEADERS.get(k));
      newCell.setCellStyle(getHeaderStyle(wb));
    }
  }
  
  /** 
   * Set style of the sheet header
   * 
   * @param wb
   * @return
   */
  private XSSFCellStyle getHeaderStyle(XSSFWorkbook wb) {
    XSSFCellStyle style = wb.createCellStyle();
    //background color
    style.setFillForegroundColor(
        new XSSFColor(ProjConstants.HEADER_COLOR, 
            new DefaultIndexedColorMap()));
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    //font style
    XSSFFont font = wb.createFont();
    font.setBold(true);
    style.setFont(font);
    
    return style;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public ArrayList<Stock> getStockList() {
    return stockList;
  }

  public void setStockList(ArrayList<Stock> stockList) {
    this.stockList = stockList;
  }

}
