import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.PriorityQueue;

public class VariableElimination {
	private String Q;// the variable of the query
	private ArrayList<String> E;// a list of the evidences
	private PriorityQueue<String> JoinOrder;// the order of the joining
	ArrayList<Factor> Factors;
	private bayesianNet bn;
	/**
	 * Constructor
	 * @param request represents a query.
	 * @param bn represents the bayesian net of a given query.
	 */
	public VariableElimination(String request,bayesianNet bn)
	{
		this.bn = bn;
		this.JoinOrder= new PriorityQueue<String>();
		request = request.replace("P(", "");
		String[] splitToInsertCalcOrder = request.split("\\),");
		String[] order = splitToInsertCalcOrder[1].split("-");
		for (int i = 0; i < order.length; i++) {
			this.JoinOrder.add(order[i]);
		}
		this.E = new ArrayList<String>();
		String[] splitToInsert = splitToInsertCalcOrder[0].split("\\|");
		this.Q= splitToInsert[0];
		String[] evidences = splitToInsert[1].split(",");
		for (int i = 0; i < evidences.length; i++) {
			this.E.add(evidences[i]);
		}
		//creating factors
		this.Factors = new ArrayList<Factor>();
		for(Iterator<NBnode> iterator = this.bn.getNodesList().iterator(); iterator.hasNext();) {
			NBnode n = (NBnode) iterator.next(); //new NBnode ((NBnode) iterator.next(),this.E);
			if(this.Q.contains(n.getName()) || isEvidence(n.getName()) || isAncestor(n)) {
				//return true or false if we need to build for this node a factor
				Factors.add(new Factor(n)); //new NBnode (n,this.E)));
				this.lastFactor().restrictFactor(this.E);
				if(this.lastFactor().size() <= 1) { //if the factor is just evidence
					Factors.remove(this.lastFactor());
				}
			}
		}
	}
	private boolean isEvidence(String e) { //return true is the node is evidence and false otherwise
		for (Iterator<String> iterator = this.E.iterator(); iterator.hasNext();) {
			String evidence = (String) iterator.next();
			if (evidence.contains(e)) return true;
		}
		return false;
	}
	private boolean isAncestor(NBnode n) {//checking if the node is a ancestor of the Q or each evidence
		for (Iterator<String> iterator = this.E.iterator(); iterator.hasNext();) {
			String evidence = (String) iterator.next();
			if(isParent(n,bn.getNodeByName(evidence.substring(0,evidence.indexOf("="))))) {
				return true;
			}
		}
		return isParent(n,bn.getNodeByName(Q.substring(0,Q.indexOf("="))));
	}
	private boolean isParent(NBnode ancestor,NBnode node) { //node = query/evidence in the start
		if (node != null) {
			if (node.getParents().contains(ancestor)) return true;
			for (NBnode parent : node.parents) {
				return isParent(ancestor,parent);
			}
		}
		return false;
	}
	/*
	 * return the last factor from the list factors
	 */
	public Factor lastFactor() {
		return Factors.get(Factors.size()-1);
	}
	public String toString() {
		StringBuilder SB = new StringBuilder();
		SB.append("Query:" + this.Q + "\nEvidence:");
		for (int i = 0; i < this.E.size(); i++) {
			SB.append(this.E.get(i) + " ");
		}
		SB.append("\nOrder:");
		SB.append(Arrays.toString(this.JoinOrder.toArray()));
		SB.append("\nFactors:\n");
		for (int i = 0; i < this.Factors.size(); i++) {
			SB.append(this.Factors.get(i).toString() + "\n");
		}
		return SB.substring(0);
	}
}
