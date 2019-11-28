import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

public class VariableElimination {
	private String Q;
	private ArrayList<String> E;
	private PriorityQueue<String> JoinOrder;
	
	public VariableElimination(String request)
	{
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
	}
	public String toString() {
		StringBuilder SB = new StringBuilder();
        SB.append("Query:" + this.Q + "\nEvidence:");
        for (int i = 0; i < this.E.size(); i++) {
			SB.append(this.E.get(i) + " ");
		}
        SB.append("\nOrder:");
        SB.append(Arrays.toString(this.JoinOrder.toArray()));
        return SB.substring(0);
	}
}
