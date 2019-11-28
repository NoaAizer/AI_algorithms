import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

public class VariableElimination {
	private String Q;
	private ArrayList<String> E;
	private PriorityQueue<String> JoinOrder;
	ArrayList<Factor> Factors;
	private bayesianNet bn;
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
		for (int i = 0; i < splitToInsert.length; i++) {
			this.E.add(evidences[i]);
		}
		this.Factors = new ArrayList<Factor>();
		for (int i = 0; i < bn.getNodesList().size(); i++) {
			Factors.add(new Factor(bn.getNodesList().get(i)));
		}		
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
