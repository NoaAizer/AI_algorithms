import java.util.ArrayList;

public class Factor extends Cpt {

	public Factor(Cpt c) {
		
	}
	public void setHeaderColumns(ArrayList<String> headerColumns) {
		this.headerColumns = headerColumns;
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
	
}
