import java.util.ArrayList;
import java.util.Iterator;

public class bayesianNet {
	private String name;
	private ArrayList<NBnode> nodesList;
	
	/**
	 * @param name
	 * @param nodesList
	 */
	public bayesianNet(String name) {
		this.name = name;
		this.nodesList = new ArrayList<NBnode>();
	}
	public String getName() {
		return name;
	}
	
	public ArrayList<NBnode> getNodesList() {
		return nodesList;
	}
	@Override
	public String toString() {
        StringBuilder SB = new StringBuilder();
        SB.append("bayesianNet: ");
        SB.append(this.getName());
        SB.append("\nVariable:");
        boolean hasVariables = false;
        for (int i = 0; i < this.nodesList.size(); i++) {
        	hasVariables = true;
			SB.append(this.nodesList.get(i).getName());
        	SB.append(' ');
		}
//        if (!hasVariables) {SB.append("none\n");}
        SB.append(hasVariables ? "\n" : "none\n");
        Iterator<NBnode> iter = this.getNodesList().iterator(); 
        while (iter.hasNext()) { 
        	SB.append(iter.next().toString());
            SB.append("\n");
        } 
		return SB.substring(0);
	} 
	public NBnode getNodeByName(String name) {
		ArrayList<NBnode> list = this.getNodesList();
		int i=0;
		for (; i < list.size() && !list.get(i).getName().equals(name);) {
			i++;
		}
		return list.get(i);
	}
	

}
