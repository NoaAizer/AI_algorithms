import java.util.ArrayList;

public class NBnode {
	private String name;
	private ArrayList<String> values;
	private cpt table;
	ArrayList<NBnode> parents = new ArrayList<NBnode>();
	ArrayList<NBnode> childs = new ArrayList<NBnode>();
	
	
	public NBnode(String name) {
		this.name = name;
		this.values = new ArrayList<String>();
		this.parents = new ArrayList<NBnode>();
		this.childs = new ArrayList<NBnode>();
		this.table = new cpt();
	}

	public ArrayList<NBnode> getChilds() {
		return childs;
	}

	public String getName() {
		return name;
	}
	public cpt getTable() {
		return table;
	}

	@Override
	public String toString() {
		StringBuilder SB = new StringBuilder();
        SB.append(this.getName());
        SB.append("\nparents:");
        boolean hasParents = false;
        for (int i = 0; i < this.parents.size(); i++) {
        	hasParents = true;
			SB.append(this.parents.get(i).getName());
        	SB.append(' ');
		}
        if (!hasParents) {SB.append("none");}
        SB.append("\nchilds:");
        boolean hasChilds = false;
        for (int i = 0; i < this.childs.size(); i++) {
        	hasChilds = true;
        	SB.append(this.childs.get(i).getName());
        	SB.append(' ');
		}
        if (!hasChilds) {SB.append("none");}
        SB.append("\ncpt:\n");
        SB.append(this.getTable().toString());
		return SB.substring(0);
	}

	public ArrayList<String> getValues() {
		return values;
	}
	public ArrayList<NBnode> getParents() {
		return parents;
	}
	
}
