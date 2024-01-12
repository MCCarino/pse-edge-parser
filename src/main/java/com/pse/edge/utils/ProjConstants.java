/**
 * 
 */
package com.pse.edge.utils;

import java.awt.Color;
import java.math.BigDecimal;

/**
 * 
 * String constants for easier reference
 * 
 * @author MC
 *
 */
public final class ProjConstants {

  public static final String EDGE_COMPANY_DIR_URL = 
      "https://edge.pse.com.ph/companyDirectory/search.ax"; 
  
  public static final String EDGE_COMPANY_DIR_PAGE_NO_PARAM = 
      "?pageNo="; 
  
  public static final String EDGE_COMPANY_DETAILS_URL =
      "https://edge.pse.com.ph/companyPage/stockData.do";
  
  public static final String EDGE_COMPANY_DETAILS_COMP_ID_PARAM = 
      "?cmpy_id=";
  
  public static final String EXPORT_XLS_FILENAME = 
      "screener.xlsx"; 
  
  public static final BigDecimal BIG_DECIMAL_100 = 
      new BigDecimal(100); 
  
  public static final Color HEADER_COLOR = 
      new Color(154, 205, 50);
  
  public static final BigDecimal FIFTY_GAIN_DIVISOR =
      new BigDecimal(1.5);
  
  public static final BigDecimal FIVE_PERCENT =
      new BigDecimal(5);
}
