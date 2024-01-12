/**
 * 
 */
package com.pse.edge.main;

import java.util.logging.Logger;

import com.pse.edge.parser.Parser;


/**
 * Main runner class
 * 
 * @author MC
 *
 */
public class Main {
  
  private static final Logger log = Logger.getLogger(Main.class.getName());

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    log.info("Start job.");
    Parser parser = new Parser();
    parser.parseEdge();
    log.info("End job.");
  }

}
