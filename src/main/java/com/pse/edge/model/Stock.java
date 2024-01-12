package com.pse.edge.model;

import java.math.BigDecimal;

public class Stock {
  
  private String name;
  private String symbol;
  private BigDecimal price;
  private BigDecimal fiftyTwoHigh;
  private BigDecimal fiftyTwoLow;
  private BigDecimal price50Gain;
  private BigDecimal percDiffPrice50Gain;
  
  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  /**
   * @return the symbol
   */
  public String getSymbol() {
    return symbol;
  }
  /**
   * @param symbol the symbol to set
   */
  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }
  /**
   * @return the price
   */
  public BigDecimal getPrice() {
    return price;
  }
  /**
   * @param price the price to set
   */
  public void setPrice(BigDecimal price) {
    this.price = price;
  }
  /**
   * @return the fiftyTwoHigh
   */
  public BigDecimal getFiftyTwoHigh() {
    return fiftyTwoHigh;
  }
  /**
   * @param fiftyTwoHigh the fiftyTwoHigh to set
   */
  public void setFiftyTwoHigh(BigDecimal fiftyTwoHigh) {
    this.fiftyTwoHigh = fiftyTwoHigh;
  }
  /**
   * @return the fiftyTwoLow
   */
  public BigDecimal getFiftyTwoLow() {
    return fiftyTwoLow;
  }
  /**
   * @param fiftyTwoLow the fiftyTwoLow to set
   */
  public void setFiftyTwoLow(BigDecimal fiftyTwoLow) {
    this.fiftyTwoLow = fiftyTwoLow;
  }
  /**
   * @return the price50Gain
   */
  public BigDecimal getPrice50Gain() {
    return price50Gain;
  }
  /**
   * @param price50Gain the price50Gain to set
   */
  public void setPrice50Gain(BigDecimal price50Gain) {
    this.price50Gain = price50Gain;
  }
  /**
   * @return the percDiffPrice50Gain
   */
  public BigDecimal getPercDiffPrice50Gain() {
    return percDiffPrice50Gain;
  }
  /**
   * @param percDiffPrice50Gain the percDiffPrice50Gain to set
   */
  public void setPercDiffPrice50Gain(BigDecimal percDiffPrice50Gain) {
    this.percDiffPrice50Gain = percDiffPrice50Gain;
  }

}
