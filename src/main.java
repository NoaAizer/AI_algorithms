
public class Main {

	public static void main(String[] args) {
		SetNet getInput = new SetNet("input.txt");
		bayesianNet bn1 = getInput.getNet();
//		System.out.println(bn1.toString());
		
		SetNet getInput2 = new SetNet("input2.txt");
		bayesianNet bn2 = getInput2.getNet();
//		System.out.println(bn2.toString());
		
		VariableElimination ve = new VariableElimination("P(B=true|M=true,J=true),A-M",bn2);
		System.out.println(ve.toString());

	}
}
