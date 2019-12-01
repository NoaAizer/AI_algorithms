import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

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
				Factors.add(new Factor(n));
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
	
//	public void start() {
//		for (Iterator<String> iterator = this.JoinOrder.iterator(); iterator.hasNext();) {
//			String hiddenVar = (String) iterator.next();
//			joinAll(hiddenVar);
////			eliminate(hiddenVar);
//		}
////		narm
//	}
//	public void joinAll() {
//	       // To find the symmetric difference 
//        Set<Integer> difference = new HashSet<Integer>(a); 
//        difference.removeAll(b); 
//        System.out.print("Difference of the two Set"); 
//        System.out.println(difference); 
//	}
	private int getDiff(Factor factor1, Factor factor2) { //factor1 <= factor2
		Set<String> difference = new HashSet<String>(factor1.getHeaderColumns()); 
		difference.removeAll(factor2.getHeaderColumns()); 
		return difference.size();
	}
	public Factor join(Factor factor1, Factor factor2) {
        // To find union 
        Set<String> unionHeaderColumns = new HashSet<String>(factor1.getHeaderColumns()); 
        unionHeaderColumns.addAll(factor1.getHeaderColumns());
		Factor returnFactor = new Factor(unionHeaderColumns);
		if(getDiff(factor1,factor2) == 0) {
			returnFactor.setTable(factor2.getTable());
			
		}
		
		return returnFactor;
	}
	
	public static Factor sumout(Factor factor, String variable) {
		int position = factor.getPositionByVariable(variable);
		HashMap<String, Double> groupedMap = new HashMap<String, Double>();
		for (String[] row : factor.getTable()) {
			String key = "";
			for (int i = 0; i < row.length; i++) {
				if (i != position) {
					key += row[i];
				}
			}
			Double probability = groupedMap.get(key);
			if (probability != null) {
				probability += factor.RowProb(row);
			} else {
				probability = factor.RowProb(row);
			}
			groupedMap.put(key, probability);
		}

		ArrayList<String> variables = new ArrayList<String>();
		variables.addAll(factor.getHeaderColumns());
		Set<String> newVariables = new HashSet<String>(variables.size() - 1);
		for (String v : variables) {
			if (!v.equals(variable)) {
				newVariables.add(v);
			}
		}
		Factor returnFactor = new Factor(newVariables);
		for (String key : groupedMap.keySet()) {
			Double val = groupedMap.get(key);
			String[] newRow = {key,""+val};
			returnFactor.addRow(newRow);
		}

		return returnFactor;
	}
	
	public static Factor normalize(Factor factor) {
		Factor returnFactor = new Factor(factor.getHeaderColumns());
		double sum = 0;
		for (String[] row : factor.getTable()) {
			sum += factor.RowProb(row);
		}
		for (String[] row : factor.getTable()) {
			String[] newRow = new String[row.length+1];
			for (int i = 0; i < row.length; i++) {
				newRow[i] = row[i];
			}
			newRow[row.length] = "" +factor.RowProb(row)/sum;
			returnFactor.addRow(newRow);
		}
		return returnFactor;
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
