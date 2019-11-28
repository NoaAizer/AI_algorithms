import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Factor{
	public static int ID=1;

	private int id;
	private String name;
	private int RowsNumber;
	private HashMap<Integer,String[]>indexToRow;
	private ArrayList<String> headerColumns;
	private ArrayList<String[]> table;

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
		this.headerColumns= new ArrayList<String>();
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
	 * Returns the probability of a row by the index.
	 * @param index represents the row's index.
	 * @return the probability
	 */
	public double RowProb(int index) {
		return Double.valueOf(this.iloc(index)[this.iloc(index).length-1]);
	}
	/**
	 * Returns a string that represents the factor.
	 * (with the factor name and  table).
	 */
	public String toString() {
		StringBuilder SB = new StringBuilder();
		SB.append("f"+this.id + "(" + this.name + ")\n");
		for (int i = 0; i < this.headerColumns.size(); i++) {
			SB.append(this.headerColumns.get(i) + '|');
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
