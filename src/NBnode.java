import java.util.ArrayList;

public class NBnode {
	private String name;
	private ArrayList<String> values;//e.g True/False..
	private Cpt table;
	ArrayList<NBnode> parents = new ArrayList<NBnode>();
	ArrayList<NBnode> childs = new ArrayList<NBnode>();
	//private int type;
	/**
	 * Constructor
	 * @param name represents the node name
	 */
	public NBnode(String name) {
		this.name = name;
		this.values = new ArrayList<String>();
		this.parents = new ArrayList<NBnode>();
		this.childs = new ArrayList<NBnode>();
		this.table = new Cpt();
	}
//	/**
//	 * Type getter
//	 * @return the type of the node
//	 */
//	public int getType() {
//		return type;
//	}
	/**
	 * Childs getter
	 * @return a list with the node's childs.
	 */
	public ArrayList<NBnode> getChilds() {
		return childs;
	}
/**
 * Name getter
 * @return the name of the node variable.
 */
	public String getName() {
		return name;
	}
	/**
	 * Cpt getter
	 * @return a table represents the node.
	 */
	public Cpt getTable() {
		return table;
	}
	/**
	 * Values getter
	 * @return the values the node has.
	 */
	public ArrayList<String> getValues() {
		return values;
	}
	/**
	 * Parents getter
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
	 * returns a string that represnts the node:
	 * name, values list, parents list, childs list and the cpt table of the node.
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
