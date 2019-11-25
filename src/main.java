public class main {

	public static void main(String[] args) {
		SetNet getInput = new SetNet("C:\\Users\\idsha\\eclipse-workspace\\Algoritmim_in_AI\\input.txt");
		bayesianNet bn = getInput.getNet();
		System.out.println(bn.toString());
	}
}
