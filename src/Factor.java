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
			int c=0;
			LinkedList<Double> rowProbs = new LinkedList<Double>();
			int indexToDuplicate = 0;
			boolean firstEqualsSign = false;
			String[] newRow = new String[n.getParents().size() + 2];
			for (;c < n.getTable().iloc(r).length;) {//columns of cpt
				int k=indexToDuplicate;
				int indexNextRow=c;
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
				rowProbs.add(Double.valueOf(n.getTable().iloc(r)[indexNextRow]));
				newRow[k] = n.getTable().iloc(r)[indexNextRow];
				this.addRow(newRow);
				c=indexNextRow+1;
				newRow = duplicateStartOfRow(n,indexToDuplicate);
			}
			String[] lastRow = duplicateStartOfRow(n,indexToDuplicate);
			lastRow[lastRow.length-2] = n.getLastValue();
			lastRow[lastRow.length-1] = "" + (1 - sumOfProb(rowProbs));
			this.addRow(lastRow);
		}
	}
	private double sumOfProb(LinkedList<Double> probs) {
		double answer = 0;
		Iterator<Double> iter = probs.iterator();
		while (iter.hasNext()) { 
			answer += iter.next();
		}
		return answer;
	}
	public void addRow(String[] row) {
		this.table.add(row);
		this.indexToRow.put(this.RowsNumber,row);
		this.RowsNumber++;
	}
	public String[] duplicateStartOfRow(NBnode n ,int index){
		String[] startNewRow = new String[n.getParents().size() + 2];
		for (int k2 = 0; k2 < index; k2++) {
			startNewRow[k2] = this.getLastRow()[k2];
		}
		return startNewRow;
	}
	public String[] iloc(int i) {
		return this.indexToRow.get(i);
	}
	public String[] getLastRow() {
		return this.iloc(this.RowsNumber-1);
	}
	public double RowProb(int index) {
		return Double.valueOf(this.iloc(index)[this.iloc(index).length-1]);
	}
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
