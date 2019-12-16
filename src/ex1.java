import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Ex1 {

	/**
	 * Run the algorithm on the 2 given inputs, print the results
	 * and create 2 files with results.
	 */
	public static void main(String[] args) {

		getInput Input = new getInput("input.txt");
		bayesianNet bn = Input.SetNet();
		//System.out.println(bn.toString());
		//System.out.println("******************************************");
		ArrayList<VariableElimination> queries = Input.SetQueries();
		for (Iterator<VariableElimination> iterator = queries.iterator(); iterator.hasNext();) {
			VariableElimination ve = (VariableElimination) iterator.next();
			//System.out.println(ve.toString());
			//System.out.println("______________________________________________________");
			ve.printActions = false;
			ve.start("output.txt");
			//System.out.println("------------------------------------------------new query-------");
		}

		//		getInput Input2 = new getInput("input2.txt");
		//		bayesianNet bn2 = Input2.SetNet();
		//		System.out.println(bn2.toString());
		//		System.out.println("******************************************");
		//		ArrayList<VariableElimination> queries2 = Input2.SetQueries();
		//		for (Iterator<VariableElimination> iterator = queries2.iterator(); iterator.hasNext();) {
		//			VariableElimination ve = (VariableElimination) iterator.next();
		//			System.out.println(ve.toString());
		//			System.out.println("______________________________________________________");
		//			ve.printActions = true;
		//			System.out.println(Arrays.toString(ve.start("output2.txt")));
		//			System.out.println("------------------------------------------------new query-------");
		//		}
	}
}
