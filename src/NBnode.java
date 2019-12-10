import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NBnode {
	private String name;
	private ArrayList<String> values;//e.g True/False..
	private Cpt cptTable;
	ArrayList<NBnode> parents = new ArrayList<NBnode>();
	ArrayList<NBnode> childs = new ArrayList<NBnode>();
	
	/**
	 * Constructor
	 * @param name represents the node name.
	 */
	public NBnode(String name) {
		this.name = name;
		this.values = new ArrayList<String>();
		this.parents = new ArrayList<NBnode>();
		this.childs = new ArrayList<NBnode>();
		this.cptTable = new Cpt();
	}
	/**
	 * Builds the same NBnode except for the cpt.
	 * @param n the original node.
	 * @param conditions is a list of the condition.
	 */
	public NBnode(NBnode n,ArrayList<String> conditions) {
		this.name = n.name;
		this.values = new ArrayList<String>();
		this.values.addAll(n.values);
		this.parents = new ArrayList<NBnode>();
		this.parents.addAll(n.parents);
		this.childs = new ArrayList<NBnode>();
		this.childs.addAll(n.childs);
		this.cptTable = new Cpt();
		this.cptTable = n.CptByCondition(conditions);
	}

	/**
	 * Creates new CPT table according to the conditions.
	 * @param conditions represents the required conditions (such as B=true. e.g).
	 * @return a new CPT table.
	 */
	public Cpt CptByCondition(ArrayList<String> conditions) {
		Cpt result = new Cpt();
		int foundVar = 0;
		for (String condition : conditions) {
			int varIndex = 0;
			ArrayList<String> headerColumns = this.cptTable.getHeaderColumns();
			for (; varIndex < headerColumns.size() && !headerColumns.get(varIndex).equals(condition.substring(0,condition.indexOf("=")));) {
				varIndex++;
			}
			if(varIndex < headerColumns.size()) {
				foundVar++;
				for (int i = 0; i < this.cptTable.getTable().size(); i++) {
					if(this.cptTable.iloc(i)[varIndex].equals(condition.substring(condition.indexOf("=")+1))) {
						result.addRow(this.cptTable.iloc(i));
					}
				}
			}
		}
		if(foundVar > 1) { // leaves only the duplicate row
			result = stayDuplicatedRow(result);
		}
		return (result.getTable().size() == 0) ? this.cptTable : result;
	}
	
	/**
	 * Leaves duplicate rows.
	 * @param table is a given CPT table to check.
	 * @return a new CPT table.
	 */
	private Cpt stayDuplicatedRow(Cpt table) {
		Cpt result = new Cpt();
		Map<String[], Integer> nameAndCount = new HashMap<>();
		// build hash table with a counter
		for (String[] row : table.getTable()) {
			Integer count = nameAndCount.get(row);
			if (count == null) { nameAndCount.put(row, 1); }
			else { nameAndCount.put(row, ++count);}
		} 
		// Print duplicate elements from array in Java
		Set<Entry<String[], Integer>> entrySet = nameAndCount.entrySet();
		for (Entry<String[], Integer> entry : entrySet) {
			if (entry.getValue() > 1) {
				result.addRow(entry.getKey()); 
			} 
		} 
		return result;
	}

	/**
	 * Childs getter.
	 * @return a list with the node's childs.
	 */
	public ArrayList<NBnode> getChilds() {
		return childs;
	}
	
	/**
	 * Name getter.
	 * @return the name of the node variable.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * CPT table getter.
	 * @return a table represents the node.
	 */
	public Cpt getTable() {
		return cptTable;
	}
	
	/**
	 * Values getter.
	 * @return the values the node has.
	 */
	public ArrayList<String> getValues() {
		return values;
	}
	/**
	 * Parents getter.
	 * @return the parents the node has.
	 */
	public ArrayList<NBnode> getParents() {
		return parents;
	}
	
	/**
	 * @return the last value in the values list of the node.
	 */
	public String getLastValue() {
		return this.values.get(this.values.size()-1);
	}
	
	/**
	 * Returns a string that represents the node:
	 * name, values list, parents list, childs list and the CPT table of the node.
	 */
	@Override
	public String toString() {
		StringBuilder SB = new StringBuilder();
		SB.append(this.getName() +"\nvalues:");
		boolean hasValues = false;
		for (int i = 0; i < this.values.size(); i++) {
			hasValues = true;
			SB.append(this.values.get(i) + ' ');
		}
		if (!hasValues) {SB.append("none");}
		SB.append("\nparents:");
		boolean hasParents = false;
		for (int i = 0; i < this.parents.size(); i++) {
			hasParents = true;
			SB.append(this.parents.get(i).getName() + ' ');
		}
		if (!hasParents) {SB.append("none");}
		SB.append("\nchilds:");
		boolean hasChilds = false;
		for (int i = 0; i < this.childs.size(); i++) {
			hasChilds = true;
			SB.append(this.childs.get(i).getName() +' ');
		}
		if (!hasChilds) {SB.append("none");}
		SB.append("\ncpt:\n");
		SB.append(this.getTable().toString());
		return SB.substring(0);
	}	
}
