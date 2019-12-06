import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Main {

	public static void main(String[] args) {
		
		getInput Input1 = new getInput("input.txt");
		bayesianNet bn1 = Input1.SetNet();
//		System.out.println(bn1.toString());
//		System.out.println("******************************************");
		ArrayList<VariableElimination> queries1 = Input1.SetQueries();
		for (Iterator<VariableElimination> iterator = queries1.iterator(); iterator.hasNext();) {
			VariableElimination ve = (VariableElimination) iterator.next();
			System.out.println(ve.toString());
			System.out.println("______________________________________________________");
			ve.printActions = true;
			System.out.println(Arrays.toString(ve.start()));
			System.out.println("------------------------------------------------new query-------");
		}

		getInput Input2 = new getInput("input2.txt");
		bayesianNet bn2 = Input2.SetNet();
		System.out.println(bn2.toString());
//		System.out.println("******************************************");
		ArrayList<VariableElimination> queries2 = Input2.SetQueries();
		for (Iterator<VariableElimination> iterator = queries2.iterator(); iterator.hasNext();) {
			VariableElimination ve = (VariableElimination) iterator.next();
			System.out.println(ve.toString());
			System.out.println("______________________________________________________");
			ve.printActions = true;
			System.out.println(Arrays.toString(ve.start()));
			System.out.println("------------------------------------------------new query-------");
		}

	}
}
