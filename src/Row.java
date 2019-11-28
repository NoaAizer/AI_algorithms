import java.util.ArrayList;

/**
 * 
 * @author ido shapira & noa aizer
 * class Row is a class that represent the row of a factor
 * @param ValuesList list of values (e.g T,F)
 * @param probability the provability of the all row
 */
public class Row {
  ArrayList<String> ValuesList;
  double probability;
  /**
   * constructor
   * @param rowVals get list of values of this row (e.g T,F or set,noset,maybe)
   * @param prob the probability of the row 
   */
  public Row(ArrayList<String> rowVals, double prob) {
    ValuesList = rowVals;
    probability = prob;
  }
  /**
   * returns a string that represent a row
   */
  public String toString() {
    String rowString = "";
    for (String var : ValuesList) {
      rowString += var;
    }
    return rowString;
  }
  /**
   * copy constructor
   * @param varString represents the values of the row.
   * @param probability the provability of the all row
   * @return copy row a new row with the same values
   */
  public static Row copyRow(String varString, double probability) {
    ArrayList<String> varStrings = new ArrayList<String>(varString.length());
    for (int i = 0; i < varString.length(); i++) {
      varStrings.add(String.valueOf(varString.charAt(i)));
    }
    return new Row(varStrings, probability);
  }
}