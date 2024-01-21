/**
 * 
 */
package com.pse.edge.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.IterableSortedMap;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import com.pse.edge.model.Stock;
import com.pse.edge.utils.ProjConstants;

/**
 * @author MC Carino
 *
 */
public class ParserTest {

	Parser parser = new Parser();

	/**
	 * Check the list of companies parsed in PSE Edge (first page of the list)
	 * 
	 * @throws Exception
	 */
	@Test
	void testCompanyListFirstPage() throws Exception {
		Document docToTest = parser.getCompanyDirPage(0);
		Document hardCodedDoc = Jsoup.connect(ProjConstants.EDGE_COMPANY_DIR_URL).get();
		assertEquals(docToTest.html(), hardCodedDoc.html());
	}

	/**
	 * Test the counter of all companies found in PSE Edge website
	 * 
	 * @throws Exception
	 */
	@Test
	void testCompanyDirPageCount() throws Exception {
		Document docToTest = parser.getCompanyDirPage(0);
		Elements spans = docToTest.select("span");
		String lastSpan = spans.get(spans.size() - 1).text();
		assertEquals(parser.getCompanyDirPageCount(docToTest), Optional.ofNullable(Integer.parseInt(lastSpan)).orElse(0));
	}

	
  @Test 
  void testStockDetails() throws Exception { 
  	Document docToTest = parser.getCompanyDirPage(0); 
   	Elements dataList = docToTest.select("table[class=list] tbody tr"); 
   	
   	//get details of first company only
   	Element firstCompany = dataList.iterator().next();
    Elements companyProps = firstCompany.children(); 
    String onclickAttr = companyProps.get(0).selectFirst("a").attr("onclick"); 
    String name = companyProps.get(0).text(); 
    String symbol = companyProps.get(1).text();
    
    Pattern pattern = Pattern.compile("'(.*?)'"); 
    Matcher matcher = pattern.matcher(onclickAttr);
    
    if (!matcher.find()) { 
    	fail("Company detail parsing failed"); 
    }
    
    Document detailsDoc = Jsoup.connect( 
    		ProjConstants.EDGE_COMPANY_DETAILS_URL +
    		ProjConstants.EDGE_COMPANY_DETAILS_COMP_ID_PARAM + 
    	matcher.group(1)).get(); 
    
    Stock parsedStock = parser.parseYearHighLow(
    		matcher.group(1), name, symbol);
    
    Element view = detailsDoc.select("table[class=view] tbody").get(1);
    Elements rows = view.select("tr");
    String priceHtml = rows.get(0).select("td").get(0).html().replace(",", "");
    String highHtml = rows.get(4).select("td").get(0).html().replace(",", "");
    String lowHtml = rows.get(4).select("td").get(1).html().replace(",", "");
    
    BigDecimal price = new BigDecimal(StringUtils.isEmpty(priceHtml) ? "0" : priceHtml);
    BigDecimal high = new BigDecimal(StringUtils.isEmpty(highHtml) ? "0" : highHtml);
    BigDecimal low = new BigDecimal(StringUtils.isEmpty(lowHtml) ? "0" : lowHtml);
    BigDecimal priceFiftyGain = high.divide(ProjConstants.FIFTY_GAIN_DIVISOR, 
        RoundingMode.HALF_EVEN);
    
    assertEquals(parsedStock.getName(), name);
    assertEquals(parsedStock.getSymbol(), symbol);
    assertEquals(parsedStock.getPrice(), price);
    assertEquals(parsedStock.getFiftyTwoHigh(), high);
    assertEquals(parsedStock.getFiftyTwoLow(), low);
    assertEquals(parsedStock.getPrice50Gain(), priceFiftyGain);
  }
}
