import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		SetNet getInput = new SetNet("input.txt");
		bayesianNet bn1 = getInput.getNet();
		//		System.out.println(bn1.toString());

		//		SetNet getInput2 = new SetNet("input2.txt");
		//		bayesianNet bn2 = getInput2.getNet();
		//		System.out.println(bn2.toString());

		//		VariableElimination ve = new VariableElimination("P(B=true|M=true,J=true),A-M",bn1);
		//		System.out.println(ve.toString());

		//		ArrayList<String> conditions = new ArrayList<String>();
		//		conditions.add("B=true");
		//		conditions.add("E=true");
		//		Cpt temp = bn1.getNodeByName("A").CptByCondition(conditions);
		////		System.out.println(bn1.getNodeByName("A").toString());
		//		System.out.println("temp:\n" + temp.toString());

		VariableElimination ve1 = new VariableElimination("P(B=true|J=true,M=true),A-E",bn1);
		System.out.println(ve1.toString());
		ve1.start();

	}
}
