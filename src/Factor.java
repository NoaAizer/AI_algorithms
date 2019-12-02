import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Factor{
	private   static int ID=1;

	private int id;
	private String name;
	private int RowsNumber;
	private HashMap<Integer,String[]>indexToRow;
	private Set<String> headerColumns;
	private ArrayList<String[]> table;
	
	
	public ArrayList<String[]> getTable() {
		return table;
	}

	public void setTable(ArrayList<String[]> table) {
		for (Iterator<String[]> iterator = table.iterator(); iterator.hasNext();) {
			String[] row = (String[]) iterator.next();
			String[] copyRow = new String[row.length];
			for (int j = 0; j < row.length; j++) {
				copyRow[j] = row[j];
			}
			this.addRow(copyRow);
		}
		this.table = table;
	}

	/**
	 * Constructor
	 * @param n is the node we creates its factor.
	 */
	public Factor(NBnode n) {
		this.id = Factor.ID;
		Factor.ID++;
		StringBuilder SB = new StringBuilder();
		SB.append(n.getName());
		if(n.parents != null) {
			for (int i = 0; i < n.parents.size(); i++) {
				SB.append("," + n.parents.get(i).getName());
			}
		}
		this.name = SB.substring(0);
		this.indexToRow= new HashMap<Integer,String[]>();
		this.headerColumns= new HashSet<String>();
		this.headerColumns.addAll(n.getTable().getHeaderColumns());
		this.table= new ArrayList<String[]>();
		for (int r = 0; r < n.getTable().getRowsNumber(); r++) {//rows of cpt
			int col=0;
			LinkedList<Double> rowProbs = new LinkedList<Double>(); // a list of the factor probabilities.
			int indexToDuplicate = 0;// the index to which the row is duplicated
			boolean firstEqualsSign = false;
			for (;col < n.getTable().iloc(r).length;) {//columns of cpt
				// the row has the parents values and the variable value with the probability.
				String[] newRow = new String[n.getParents().size() + 2];
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
	 * Contractor 
	 * creating empty factor with just columns
	 * @param HeaderColumns
	 */
	public Factor(Set<String> HeaderColumns) {
		this.id = Factor.ID;
		Factor.ID++;
		StringBuilder SB = new StringBuilder();
		if(HeaderColumns != null) {
			for (Iterator<String> iterator = HeaderColumns.iterator(); iterator.hasNext();) {
				SB.append((String) iterator.next() + ' ');
			}
		}
		this.name = SB.substring(0);
		this.indexToRow= new HashMap<Integer,String[]>();
		this.headerColumns= new HashSet<String>(HeaderColumns);
		this.table= new ArrayList<String[]>();
	}

	/**
	 * Header Column getter
	 * @return a list of the variables of the factor
	 */
	public Set<String> getHeaderColumns() {
		return headerColumns;
	}
	/**
	 * sum all the probabilities of the variable with the same parents values
	 * @param probs represents the probabilities of the variable values.
	 * @return
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
	 * Adds a row to the table
	 * @param row is the new row 
	 */
	public void addRow(String[] row) {
		this.table.add(row);
		this.indexToRow.put(this.RowsNumber,row);
		this.RowsNumber++;
	}
	/**
	 * Duplicate the start of the row for the rows that have the same parents values.
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
	 * @param i
	 * @return the i row of the table.
	 */
	public String[] iloc(int i) {
		return this.indexToRow.get(i);
	}
	/**
	 * @return the last row of the cpt table
	 */
	public String[] getLastRow() {
		return this.iloc(this.RowsNumber-1);
	}
	/**
	 * set the probability of a row
	 * @param index represents the row's index.
	 */
	public void setRowProb(int index, double prob) {
		this.iloc(index)[this.iloc(index).length-1] = "" +prob;
		
	}
	/**
	 * Returns the probability of a row by the index.
	 * @param index represents the row's index.
	 * @return the probability
	 */
	
	public double RowProb(int index) {
		return Double.valueOf(this.iloc(index)[this.iloc(index).length-1]);
	}
	/**
	 * Returns the probability of specific row by the index.
	 * @param row a row in the factor
	 * @return the probability
	 */
	public double RowProb(String[] row) {
		return Double.valueOf(row[row.length-1]);
	}
	/*
	 * the size of the factor
	 */
	public int size() {
		return this.table.size();
	}
	/**
	 * finding the variable column index
	 * @param variable
	 * @return the position of the columns
	 */
	public int getPositionByVariable(String variable) {
		int varIndex = 0;
		for (Iterator<String> iterator = headerColumns.iterator(); iterator.hasNext() && !iterator.next().equals(variable);) {
			varIndex++;
		}
		return varIndex;
	  }
	/**
	 * 
	 * @param where condition
	 * @return new CPT table in accordance to the condition
	 */
	public void restrictFactor(ArrayList<String> conditions) {
		ArrayList<String[]> result = new ArrayList<String[]>(); //(this.table);
		int foundVar = 0;
		Set<String> headerColumns = this.headerColumns;
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
		if(foundVar > 1) { // stay only the duplicated row
			result = stayDuplicatedRow(result);
		}
		this.table = (result.size() == 0) ? this.table : result;
//		for (Iterator<String> iterator = headerColumns.iterator(); iterator.hasNext();) {
//			String evidence = (String) iterator.next();
//			removeEvidenceColumns(evidence);
//		}
	}
//	private void removeEvidenceColumns(String evidence) {
//		ArrayList<String> coulmns = this.headerColumns;
//		for (Iterator<String> iterator = coulmns.iterator(); iterator.hasNext();) {
//			int indexCol = 0;
//			String col = (String) iterator.next();
//			if(!col.equals(evidence)) {
//				removeColumns(indexCol);
//			}
//			
//		}
//	}
	/*
	 * in case that we have more then 1 condition we need to save just the rows that uphold the all condition
	 * @param table the result before the changes
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
