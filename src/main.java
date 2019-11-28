public class Main {

	public static void main(String[] args) {
//		SetNet getInput = new SetNet("C:\\Users\\idsha\\eclipse-workspace\\Algoritmim_in_AI\\input.txt");
//		bayesianNet bn = getInput.getNet();
//		System.out.println(bn.toString());
		
		VariableElimination ve = new VariableElimination("P(B=true|E,J),A-M");
		System.out.println(ve.toString());
	}
}
