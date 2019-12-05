import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Main {

	public static void main(String[] args) {
		getInput Input = new getInput("input.txt");
		bayesianNet bn1 = Input.SetNet();
		System.out.println(bn1.toString());
		System.out.println("******************************************");
		ArrayList<VariableElimination> queries = Input.SetQueries();
		for (Iterator<VariableElimination> iterator = queries.iterator(); iterator.hasNext();) {
			VariableElimination ve = (VariableElimination) iterator.next();
			System.out.println("___________________________________________");
			System.out.println(ve.toString());
			System.out.println("-------------------------------------------");
			System.out.println(Arrays.toString(ve.start()));
		}

		//		SetNet getInput2 = new SetNet("input2.txt");
		//		bayesianNet bn2 = getInput2.getNet();
		//		System.out.println(bn2.toString());

		//		VariableElimination ve = new VariableElimination("P(B=true|M=true,J=true),A-M",bn1);
		//		System.out.println(ve.toString())

	}
}
