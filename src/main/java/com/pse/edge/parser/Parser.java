package com.pse.edge.parser;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pse.edge.export.ExcelExporter;
import com.pse.edge.model.Stock;
import com.pse.edge.utils.ProjConstants;

/**
 * Main parser class for PSE Edge
 * 
 * @author MC
 *
 */
public class Parser {
  
  private static final Logger log = Logger.getLogger(Parser.class.getName());
  
  private ArrayList<Stock> stocks = new ArrayList<Stock>();
  private static final int MAX_PARSE_RETRY = 10;
	
  /**
   * Main parser method
   * 
   * @throws Exception
   */
  public void parseEdge() throws Exception {
    Document doc = getCompanyDirPage();
    //parse first page
    parseCompanies(doc);

    int pageCount = getCompanyDirPageCount(doc);
    for (int a = 2; a <= pageCount; a++) {
      doc = getCompanyDirPage(a);
	  try {
	    parseCompanies(doc);
      } catch (SocketTimeoutException ste) {
        continue;
      }
    }
    generateStocksXls(); 
  }
	
	/**
	 * Get the number of pages on the company directory document
	 * 
	 * @param doc
	 * @return
	 */
  private int getCompanyDirPageCount(Document doc) {
  	Elements spans = doc.select("span");
  	String lastSpan = spans.get(spans.size()-1).text();
  	try {
  	  int pageCnt = Integer.parseInt(lastSpan);
  	  return pageCnt;
  	} catch (NumberFormatException nfe) {
  	  return 0;
  	}
  }

  /**
	 * Parse a page of the company directory listing grid
	 * 
	 * @param doc
	 * @throws Exception
	 */
	private void parseCompanies(Document doc) throws Exception {
    Elements dataList = doc.select("table[class=list] tbody tr");
    for (Element data: dataList) {
      int retryCnt = 0;
      Elements companyProps = data.children();
      String onclickAttr = companyProps.get(0).selectFirst("a").attr("onclick");
      String name = companyProps.get(0).text();
      String symbol = companyProps.get(1).text();
      
      //extract company id for retrieval of details document
      Pattern pattern = Pattern.compile("'(.*?)'");
      Matcher matcher = pattern.matcher(onclickAttr);
      if (matcher.find()) {
        while (retryCnt < MAX_PARSE_RETRY) {
          try {
            parseYearHighLow(matcher.group(1), name, symbol);
            break;
          } catch (SocketTimeoutException ste) {
            log.info("Retrying " + symbol + "...");
            retryCnt++;
            continue;
          }
        }
      }
    }  
  }

	/**
	 * Exports stock data to xlsx
	 * 
	 * @throws Exception
	 */
  private void generateStocksXls() throws Exception {
    ExcelExporter exporter = new ExcelExporter();
    exporter.setFilename(ProjConstants.EXPORT_XLS_FILENAME);
    exporter.setStockList(stocks);
    exporter.export();
  }

  /**
   * Parser method for company details page
   * 
   * @param compId
   * @param name
   * @param symbol
   * @throws SocketTimeoutException
   * @throws IOException
   */
  private void parseYearHighLow(String compId, String name, String symbol) throws SocketTimeoutException, IOException {
  	Document doc = 
	    Jsoup.connect(
	        ProjConstants.EDGE_COMPANY_DETAILS_URL +
	        ProjConstants.EDGE_COMPANY_DETAILS_COMP_ID_PARAM 
	        + compId).get();
    
    Element view = doc.select("table[class=view] tbody").get(1);
    Elements rows = view.select("tr");
    String priceHtml = rows.get(0).select("td").get(0).html().replace(",", "");
    String highHtml = rows.get(4).select("td").get(0).html().replace(",", "");
    String lowHtml = rows.get(4).select("td").get(1).html().replace(",", "");
    
    BigDecimal price = new BigDecimal(StringUtils.isEmpty(priceHtml) ? "0" : priceHtml);
    BigDecimal high = new BigDecimal(StringUtils.isEmpty(highHtml) ? "0" : highHtml);
    BigDecimal low = new BigDecimal(StringUtils.isEmpty(lowHtml) ? "0" : lowHtml);
    BigDecimal priceFiftyGain = high.divide(ProjConstants.FIFTY_GAIN_DIVISOR, 
        RoundingMode.HALF_EVEN);
    
    BigDecimal priceDiff = price.subtract(priceFiftyGain).abs();
    BigDecimal percDiff = BigDecimal.ZERO;
    if (price.compareTo(BigDecimal.ZERO) != 0) {
      percDiff = priceDiff.divide(price, 3, RoundingMode.HALF_EVEN).
          multiply(ProjConstants.BIG_DECIMAL_100);
      
      if (percDiff.compareTo(ProjConstants.FIVE_PERCENT) <= 0) {
        log.fine("Name: " + name);
        log.fine("Symbol: " + symbol);
        log.fine("Price: " + price);
        log.fine("52 week high: " + high);
        log.fine("52 week low: " + low);
        log.fine("Divide by 1.5: " + priceFiftyGain);
        log.fine("Percent diff: " + percDiff);
        
        Stock stock = new Stock();
        stock.setName(name);
        stock.setSymbol(symbol);
        stock.setPrice(price);
        stock.setFiftyTwoHigh(high);
        stock.setFiftyTwoLow(low);
        stock.setPrice50Gain(priceFiftyGain);
        stock.setPercDiffPrice50Gain(percDiff);
        
        stocks.add(stock);
      }
    }
  }
  
  /**
   * Get JSoup Document of the company directory page
   * 
   * @return
   * @throws Exception
   */
  private Document getCompanyDirPage() throws Exception {
    return getCompanyDirPage(0);
  }
  
  /**
   * Get JSoup Document of the nth company directory page
   * 
   * @param page
   * @return
   * @throws Exception
   */
  private Document getCompanyDirPage(int page) throws Exception {
    return Jsoup.connect(ProjConstants.EDGE_COMPANY_DIR_URL + 
        (page != 0 ? ProjConstants.EDGE_COMPANY_DIR_PAGE_NO_PARAM + page : ""))
          .get();
  }
}
