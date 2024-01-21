/**
 * 
 */
package com.pse.edge.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import com.pse.edge.utils.ProjConstants;

/**
 * @author MC Carino
 *
 */
public class ParserTest {

	Parser parser = new Parser();
	
	/**
	 * Check the list of companies parsed in PSE Edge 
	 * (first page of the list)
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
		String lastSpan = spans.get(spans.size()-1).text();
		assertEquals(parser.getCompanyDirPageCount(docToTest), 
				Optional.ofNullable(Integer.parseInt(lastSpan)).orElse(0));
	}
}
