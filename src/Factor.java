import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Factor{
	private int id;
	private String name;// the variables of the factor
	private int RowsNumber;// the number of rows in the table of the factor
	private HashMap<Integer,String[]>indexToRow;// getting the row by index
	private LinkedHashSet<String> headerColumns;// the variables in the table
	private ArrayList<String[]> table;// the table of the factor

	/**
	 * Constructor
	 * @param n is the node we creates its factor.
	 */
	public Factor(NBnode n,int id) {
		this.id = id;
		StringBuilder SB = new StringBuilder();
		if(n.parents != null) {
			for (int i = 0; i < n.parents.size(); i++) {
				SB.append("," + n.parents.get(i).getName());
			}
		}
		SB.append("," + n.getName());
		this.name = SB.substring(1);
		this.RowsNumber = 0;
		this.indexToRow= new HashMap<Integer,String[]>();
		this.headerColumns= new LinkedHashSet<String>();
		this.headerColumns.addAll(n.getTable().getHeaderColumns());
		this.table= new ArrayList<String[]>();
		for (int r = 0; r < n.getTable().getRowsNumber(); r++) {//rows of cpt
			int col=0;
			LinkedList<Double> rowProbs = new LinkedList<Double>(); // a list of the factor probabilities.
			int indexToDuplicate = 0;// the index to which the row is duplicated
			boolean firstEqualsSign = false;
			String[] newRow = new String[n.getParents().size() + 2];
			for (;col < n.getTable().iloc(r).length;) {//columns of cpt
				// the row has the parents values and the variable value with the probability.
				int k=indexToDuplicate;
				int indexNextRow=col;// the index which we start to write the new values of a duplicated row.
				while(!n.getTable().iloc(r)[indexNextRow].contains(".") && indexNextRow < n.getTable().iloc(r).length) {// runs on r row
					if(!n.getTable().iloc(r)[indexNextRow].contains("=")) {
						newRow[k] = n.getTable().iloc(r)[indexNextRow];
					}
					else {
						newRow[k] = n.getTable().iloc(r)[indexNextRow].replace("=","");
						if(!firstEqualsSign) {
							indexToDuplicate = indexNextRow;
							firstEqualsSign=true;
						}
					}
					indexNextRow++;
					k++;
				}
				//adds a row with the same parent vaules but with a different value of the variable.
				rowProbs.add(Double.valueOf(n.getTable().iloc(r)[indexNextRow]));
				newRow[k] = n.getTable().iloc(r)[indexNextRow];
				this.addRow(newRow);
				col=indexNextRow+1;
				newRow = duplicateStartOfRow(n,indexToDuplicate);
			}
			//adds a row with the last variable value (but the parents values are the same as the previous rows).
			String[] lastRow = duplicateStartOfRow(n,indexToDuplicate);
			lastRow[lastRow.length-2] = n.getLastValue();
			lastRow[lastRow.length-1] = "" + (1 - sumOfProb(rowProbs));
			this.addRow(lastRow);
		}
	}
	/**
	 * Id getter
	 * @return the id number of the factor.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Contractor - creating an empty new factor only with columns headers.
	 * @param HeaderColumns represents the variables of the factor table.
	 * @param id represents the factor id.
	 */
	public Factor(LinkedHashSet<String> HeaderColumns,int id) {
		this.id = id;
		StringBuilder SB = new StringBuilder();
		if(HeaderColumns != null) {
			for (Iterator<String> iterator = HeaderColumns.iterator(); iterator.hasNext();) {
				SB.append((String) iterator.next() + ' ');
			}
		}
		this.name = SB.substring(0);
		this.RowsNumber =0;
		this.indexToRow= new HashMap<Integer,String[]>();
		this.headerColumns= new LinkedHashSet<String>(HeaderColumns);
		this.table= new ArrayList<String[]>();
	}
	
	/**
	 * Checks if 2 given factors are the same.
	 * @param f represents a given factor.
	 * @return True- if equals , False- not equals.
	 */
	public boolean equals (Factor f) {
		return this.id == f.id;
	}
	
	/**
	 * Table getter
	 * @return the factor table.
	 */
	public ArrayList<String[]> getTable() {
		return table;
	}
	
	/**
	 * Create a table.
	 * @param table represents a given table we want to set.
	 */
	public void setTable(ArrayList<String[]> table) {
		for (Iterator<String[]> iterator = table.iterator(); iterator.hasNext();) {
			String[] row = (String[]) iterator.next();
			String[] copyRow = new String[row.length];
			for (int j = 0; j < row.length; j++) {
				copyRow[j] = row[j];
			}
			this.addRow(copyRow);
			this.indexToRow.put(this.table.size()-1,this.table.get(this.table.size()-1));
		}
	}

	/**
	 * Header Column getter.
	 * @return a list of the variables of the factor
	 */
	public LinkedHashSet<String> getHeaderColumns() {
		return headerColumns;
	}
	
	/**
	 * Sum all the probabilities of the variable with the same parents values.
	 * @param probs represents the probabilities of the variable values.
	 * @return the summarize of the probabilities.
	 */
	private double sumOfProb(LinkedList<Double> probs) {
		double answer = 0;
		Iterator<Double> iter = probs.iterator();
		while (iter.hasNext()) { 
			answer += iter.next();
		}
		return answer;
	}
	
	/**
	 * Adds a new row to the table.
	 * @param row represents the new row.
	 */
	public void addRow(String[] row) {
		this.table.add(row);
		this.indexToRow.put(this.RowsNumber,row);
		this.RowsNumber++;
	}
	
	/**
	 * Duplicates the start of a row of the rows that have the same parents values.
	 * @param n represents the node.
	 * @param index represents the indexToDuplicate.
	 * @return the start of the duplicated row.
	 */
	public String[] duplicateStartOfRow(NBnode n ,int index){
		String[] startNewRow = new String[n.getParents().size() + 2];
		for (int k = 0; k < index; k++) {
			startNewRow[k] = this.getLastRow()[k];
		}
		return startNewRow;
	}
	
	/**
	 * Returns a row of the cpt table by a given row index.
	 * @param i represents the index.
	 * @return the i row of the table.
	 */
	public String[] iloc(int i) {
		return this.indexToRow.get(i);
	}
	
	/**
	 * @return the last row of the cpt table.
	 */
	public String[] getLastRow() {
		return this.iloc(this.RowsNumber-1);
	}
	
	/**
	 * Sets the probability of a row.
	 * @param index represents the row's index.
	 */
	public void setRowProb(int index, double prob) {
		this.iloc(index)[this.iloc(index).length-1] = "" +prob;
	}
	
	/**
	 * Rows number getter
	 * @return the number of rows in the factor's table.
	 */
	public int getRowsNumber() {
		return RowsNumber;
	}

	/**
	 * Returns the probability of a row by the index.
	 * @param index represents the row's index.
	 * @return the probability.
	 */

	public double RowProb(int index) {
		return Double.valueOf(this.iloc(index)[this.iloc(index).length-1]);
	}
	
	/**
	 * Returns the probability of a specific row by the index.
	 * @param row represents a row in the factor.
	 * @return the probability.
	 */
	public double RowProb(String[] row) {
		return Double.valueOf(row[row.length-1]);
	}
	
	/**
	 * @return the size of the factor table.
	 */
	public int size() {
		return this.table.size();
	}
	
	/**
	 * Removes a column from the table by a variable name.
	 * @param col represents the variable column name.
	 * @return a new factor without the column.
	 */
	public Factor removeColumn(String col) {
		LinkedHashSet<String> headerCol = new LinkedHashSet<>(this.headerColumns);
		if(headerCol.remove(col)) {
			Factor returnFactor= new Factor(headerCol,this.id);
			for(int row=0;row<this.table.size();row++) {
				String[] newRow= new String [this.headerColumns.size()];
				int col_num=0;
				for(int c=0;c<this.headerColumns.size();c++) { 
					if(!this.getVariableByPosition(c).equals(col)) {
						newRow[col_num]=this.iloc(row)[c];
						col_num++;
					}
				}
				newRow[col_num]= ""+(this.RowProb(row));
				returnFactor.addRow(newRow);
			}
			return returnFactor;
		}
		else {return this;}
	}
	
	/**
	 * Finds the variable column index.
	 * @param variable represents the given variable.
	 * @return the position of the column.
	 */
	public int getPositionByVariable(String variable) {
		int varIndex = 0;
		for (Iterator<String> iterator = headerColumns.iterator(); iterator.hasNext() && !iterator.next().equals(variable);) {
			varIndex++;
		}
		return varIndex;
	}
	
	/**
	 * Finds the variable by the column index.
	 * @param column represents the column index.
	 * @return the variable in the given column.
	 */
	public String getVariableByPosition(int column) {
		int varIndex = 0;
		String var="";
		for (Iterator<String> iterator = headerColumns.iterator(); iterator.hasNext();) {
			if(varIndex == column)
				return iterator.next();
			iterator.next();
			varIndex++;
		}
		return var;
	}

	/**
	 * Restricts a factor from given conditions.
	 * @param conditions represent a list of conditions (such as B=true..)
	 */
	public void restrictFactor(ArrayList<String> conditions) {
		if(!conditions.isEmpty()) {
			ArrayList<String[]> result = new ArrayList<String[]>(); //(this.table);
			int foundVar = 0;
			LinkedHashSet<String> headerColumns = this.headerColumns;
			for (String condition : conditions) {
				int varIndex = getPositionByVariable(condition.substring(0,condition.indexOf("=")));
				if(varIndex < headerColumns.size()) {
					foundVar++;
					for (int i = 0; i < this.table.size(); i++) {
						if(this.iloc(i)[varIndex].equals(condition.substring(condition.indexOf("=")+1))) {
							//add only the row that uphold the specific evidence
							result.add(this.iloc(i));
						}
					}
				}
			}
			//updates the indexToRow hash map
			if(foundVar >= 1) {
				if(foundVar>1)
					result = stayDuplicatedRow(result);
				this.indexToRow.clear();
				for (int i = 0; i < result.size(); i++) {
					this.indexToRow.put(i,result.get(i));
				}
			}
			this.table = (result.size() == 0) ? this.table : result;
		}
	}
	
	/**
	 * In case that we have more then 1 condition we need to save only the rows that uphold the all condition.
	 * @param table represents the result before the changes.
	 * @return the table after the change.
	 */
	private ArrayList<String[]> stayDuplicatedRow(ArrayList<String[]> table) {
		ArrayList<String[]> result = new ArrayList<String[]>();
		Map<String[], Integer> nameAndCount = new HashMap<>();
		// build hash table with count
		for (String[] row : table) {
			Integer count = nameAndCount.get(row);
			if (count == null) { nameAndCount.put(row, 1); }
			else { nameAndCount.put(row, ++count);}
		} 
		// Print duplicate elements from array in Java
		Set<Entry<String[], Integer>> entrySet = nameAndCount.entrySet();
		for (Entry<String[], Integer> entry : entrySet) {
			if (entry.getValue() > 1) {
				result.add(entry.getKey()); 
			} 
		} 
		return result;
	}
	
	/**
	 * Returns a string that represents the factor.
	 * (with the factor name and  table).
	 */
	public String toString() {
		StringBuilder SB = new StringBuilder();
		SB.append("f"+this.id + "(" + this.name + ")\n");
		for (Iterator<String> iterator = headerColumns.iterator(); iterator.hasNext();) {
			SB.append((String) iterator.next() + '|');
		}
		SB.append("\n");
		Iterator<String[]> iter = this.table.iterator(); 
		while (iter.hasNext()) {
			String[] row = iter.next();
			for (int i = 0; i < row.length; i++) {
				SB.append(row[i] + '|');
			}
			SB.append("\n");
		} 
		return SB.substring(0);
	}
}
