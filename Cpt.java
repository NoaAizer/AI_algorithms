import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Cpt {

	private int RowsNumber;
	private HashMap<Integer,String[]>indexToRow;// getting the row by index
	private ArrayList<String> headerColumns;// a list of cpt's column headers
	private ArrayList<String[]> table;// a cpt table with the values and probabilities.
	
	/**
	 * Default constructor
	 */
	public Cpt() {
		this.RowsNumber = 0;
		this.table = new ArrayList<String[]>();
		this.headerColumns = new ArrayList<String>();
		this.indexToRow = new HashMap<Integer,String[]>();
	}
	/**
	 * Copy constructor
	 * @param c is a given cpt
	 */
	public Cpt(Cpt c) {
		this.RowsNumber = c.RowsNumber;
		this.indexToRow = new HashMap<Integer,String[]>();
		this.indexToRow.putAll(c.indexToRow);
		this.headerColumns = new ArrayList<String>();
		this.headerColumns.addAll(c.headerColumns);
		this.table = new ArrayList<String[]>();
		this.table.addAll(c.table);
		
	}
	/**
	 * Header columns getter
	 * @return a list of cpt's column headers (the variables names)
	 */
	public ArrayList<String> getHeaderColumns() {
		return headerColumns;
	}
	/**
	 * Table getter
	 * @return the cpt table of a node.
	 */
	public ArrayList<String[]> getTable() {
		return this.table;
	}
	/**
	 * Rows number getter
	 * @return the number of rows the cpt table has.
	 */
	public int getRowsNumber() {
		return this.RowsNumber;
	}
	/**
	 * @return the last row of the cpt table
	 */
	public String[] getLastRow() {
		return this.iloc(this.getRowsNumber()-1);
	}
	/**
	 * @return the last row index in the cpt table
	 */
	public int getLastRowIndex() {
		return this.getRowsNumber()-1;
	}
	/**
	 * add a new row to the cpt table
	 * @param row is a string array with values and a probability
	 */
	public void addRow(String[] row) {
		this.table.add(row);
		this.indexToRow.put(this.getRowsNumber(),row);
		this.RowsNumber++;
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
	 * Returns a string that represents the cpt table with the headers.
	 */
	@Override
	public String toString() {
		StringBuilder SB = new StringBuilder();
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
