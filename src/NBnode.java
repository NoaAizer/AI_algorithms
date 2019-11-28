import java.util.ArrayList;

public class NBnode {
	private String name;
	private ArrayList<String> values;
	private Cpt table;
	ArrayList<NBnode> parents = new ArrayList<NBnode>();
	ArrayList<NBnode> childs = new ArrayList<NBnode>();
	private int type;
	
	public NBnode(String name) {
		this.name = name;
		this.values = new ArrayList<String>();
		this.parents = new ArrayList<NBnode>();
		this.childs = new ArrayList<NBnode>();
		this.table = new Cpt();
	}
	
	public int getType() {
		return type;
	}
	public ArrayList<NBnode> getChilds() {
		return childs;
	}

	public String getName() {
		return name;
	}
	public Cpt getTable() {
		return table;
	}
	public ArrayList<String> getValues() {
		return values;
	}
	public ArrayList<NBnode> getParents() {
		return parents;
	}
	public String getLastValue() {
		return this.values.get(this.values.size()-1);
	}
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
