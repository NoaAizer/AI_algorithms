import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Cpt {

	private int RowsNamber;
	private HashMap<Integer,String[]>indexToRow;
	private ArrayList<String> headerColumns;
	private ArrayList<String[]> table;
	
	public Cpt() {
		this.RowsNamber = 0;
		this.table = new ArrayList<String[]>();
		this.headerColumns = new ArrayList<String>();
		this.indexToRow = new HashMap<Integer,String[]>();
	}
	public Cpt(Cpt c) {
		this.RowsNamber = c.RowsNamber;
		this.indexToRow = new HashMap<Integer,String[]>();
		this.indexToRow.putAll(c.indexToRow);
		this.headerColumns = new ArrayList<String>();
		this.headerColumns.addAll(c.headerColumns);
		this.table = new ArrayList<String[]>();
		this.table.addAll(c.table);
		
	}
	public ArrayList<String> getHeaderColumns() {
		return headerColumns;
	}
	public void setHeaderColumns(ArrayList<String> headerColumns) {
		this.headerColumns = headerColumns;
	}
	public ArrayList<String[]> getTable() {
		return this.table;
	}
	public int getRowsNumber() {
		return this.RowsNamber;
	}
	public String[] getLastRow() {
		return this.iloc(this.getRowsNumber()-1);
	}
	public int getLastRowIndex() {
		return this.getRowsNumber()-1;
	}
	public void addRow(String[] row) {
		this.table.add(row);
		this.indexToRow.put(this.getRowsNumber(),row);
		this.RowsNamber++;
	}
	public String[] iloc(int i) {
		return this.indexToRow.get(i);
	}
	public void duplicateLastRow() {
		String[] lastRow = this.iloc(this.getRowsNumber()-1);
		String[] newRow = new String[lastRow.length];
		for (int i = 0; i < newRow.length; i++) {
			newRow[i] = lastRow[i];
		}
		this.addRow(newRow);
	}
	public double RowProb(int index) {
		return Double.valueOf(this.iloc(index)[this.iloc(index).length-1]);
	}

	@Override
	public String toString() {
		StringBuilder SB = new StringBuilder();
		for (int i = 0; i < this.headerColumns.size(); i++) {
			SB.append(this.headerColumns.get(i));
			SB.append('|');
		}
		SB.append("\n");
		Iterator<String[]> iter = this.table.iterator(); 
        while (iter.hasNext()) {
        	String[] row = iter.next();
        	for (int i = 0; i < row.length; i++) {
				SB.append(row[i]);
				SB.append('|');
			}
            SB.append("\n");
        } 
		return SB.substring(0);
	}
}
