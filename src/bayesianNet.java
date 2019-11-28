import java.util.ArrayList;
import java.util.Iterator;

public class bayesianNet {
	private String name;
	private ArrayList<NBnode> nodesList;
	
	/**
	 * constructor
	 * @param name
	 * @param nodesList
	 */
	public bayesianNet(String name) {
		this.name = name;
		this.nodesList = new ArrayList<NBnode>();
	}
	/**
	 * Name getter
	 * @return the name of the bayesian net
	 */
	public String getName() {
		return name;
	}
	/**
	 * Node list getter
	 * @return a list of the nodes in the bayesian net
	 */
	public ArrayList<NBnode> getNodesList() {
		return nodesList;
	}
	/**
	 * return the bayesian network as a string with the name,
	 * list of the variables and the details about each node.
	 */
	@Override
	public String toString() {
        StringBuilder SB = new StringBuilder();
        SB.append("bayesianNet: " + this.getName() + "\nVariable:");
        boolean hasVariables = false;
        for (int i = 0; i < this.nodesList.size(); i++) {
        	hasVariables = true;
			SB.append(this.nodesList.get(i).getName() +' ');
		}
        SB.append(hasVariables ? "\n" : "none\n");
        Iterator<NBnode> iter = this.getNodesList().iterator(); 
        while (iter.hasNext()) { 
        	SB.append(iter.next().toString()+"\n");
        } 
		return SB.substring(0);
	} 
	/**
	 * return the node by his name
	 * @param name is the name of the requested node
	 * @return the requested node 
	 */
	public NBnode getNodeByName(String name) {
		ArrayList<NBnode> list = this.getNodesList();
		int i=0;
		for (; i < list.size() && !list.get(i).getName().equals(name);) {
			i++;
		}
		return list.get(i);
	}
	

}
