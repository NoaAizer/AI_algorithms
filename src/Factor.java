import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
/**
 * @author ido shapira & noa aizer
 * class Factor is a class that represent a factor in variable elimination algorithm
 * @param rowList list of row (represent 2D table)
 * @param variableNameToIntMapping to encode values to numbers
 * @param probRow each values has his probability
 * @param factorTitle header of the factor
 */
public class Factor {
  ArrayList<Row> rowList = new ArrayList<Row>();
  HashMap<String, Integer> variableNameToIntMapping = new HashMap<String, Integer>();
  HashMap<String, Double> probRow = new HashMap<String, Double>();
  public ArrayList<String> intToVariableNameMapping;
  int id; //factor number
  String factorTitle = ""; //header of the factor
  
  public Factor(ArrayList<String> variables, int id) {
    intToVariableNameMapping = variables;
    for (int i = 0; i < variables.size(); i++) {
      variableNameToIntMapping.put(variables.get(i), i);
    }
    this.id = id;
    this.factorTitle = this.factorTitle();
  }
  public String factorTitle() {
	  String st = "";
	  if (this.intToVariableNameMapping.size() > 0) {
	      String val = this.intToVariableNameMapping.get(0);
	      for (int i = 1; i < this.intToVariableNameMapping.size(); i++) {
	        val += (", " + this.intToVariableNameMapping.get(i));
	      }
	      st = "f" + id +"(" + val + ")";
	    }
	  return st;
  }
  
  public Factor(ArrayList<String> variables, int id, String var, String restriction) {
    this(variables, id);
    if (intToVariableNameMapping.size() > 0) {
      String val = intToVariableNameMapping.get(0);
      for (int i = 1; i < intToVariableNameMapping.size(); i++) {
        val += (", " + intToVariableNameMapping.get(i));
      }
      factorTitle = "f" + id +" (" + val + ", " + var + " = " + restriction +")";
    }
  }

  public String getVariableByPosition(int position) {
    return intToVariableNameMapping.get(position);
  }
  
  public Integer getPositionByVariable(String name) {
    return variableNameToIntMapping.get(name);
  }
  
  public void addRow(ArrayList<String> vals, String key, double probability) {
    probRow.put(key, probability);
    rowList.add(new Row(vals, probability));
  }
  
  public void addRow(Row row) {
    probRow.put(row.toString(), row.probability);
    rowList.add(row);
  }
  
  public static String valueGivenPosition(int position, String key) {
    return String.valueOf(key.charAt(position));
  }
  
  public Set<String> getVarsSet() {
    Set<String> varSet = new HashSet<String>();
    for (int i = 0; i < intToVariableNameMapping.size(); i++) {
      varSet.add(intToVariableNameMapping.get(i));
    }
    return varSet;
  }
  
  public Row getRowGivenVals(HashMap<String, String> valsGivenVars) { //????
    for (Row row : rowList) {
      boolean match = true;
      for (int i = 0; i < row.ValuesList.size(); i++) {
        String varName = intToVariableNameMapping.get(i);
        String varInput = valsGivenVars.get(varName);
        if (varInput != null && !varInput.equals(row.ValuesList.get(i))) {
          match = false;
          break;
        }
      }
      if (match) {
        return row;
      }
    }
    return null;
  }
  
  public ArrayList<String> getVarsArray() {
    return intToVariableNameMapping;
  }
  
  public HashMap<String, Double> getProbRow() {
    return probRow;
  }
  
  public ArrayList<Row> getRowList() {
    return rowList;
  }
  /**
   * printing the factor
   */
  public void print() {
    System.out.println(factorTitle);
    
    for (int i = 0; i < intToVariableNameMapping.size(); i++) {
      System.out.print(intToVariableNameMapping.get(i) + "\t");
    }
    System.out.println("");
    for (String key : probRow.keySet()) {
      for (int i = 0; i < intToVariableNameMapping.size(); i++) {
        System.out.print(valueGivenPosition(i, key) + "\t");
      }
      System.out.println(probRow.get(key));
    }
    System.out.println("");
  }
}