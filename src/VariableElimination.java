import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * implement VariableElimination algorithm.
 * - each query has his one VariableElimination object.
 * @author Ido Shapira & Noa Aizer
 *
 */
public class VariableElimination {
	private int idFactor;
	private String Q;// the variable of the query
	private ArrayList<String> E;// a list of the evidences
	private ArrayList<String> JoinOrder;// the order of the joining
	ArrayList<Factor> Factors;// a list of the factors
	private bayesianNet bn;
	private int numOfMul;// Counts the multiplication operations that occur during the algorithm
	private int numOfAdd;// Counts the adding operations that occur during the algorithm
	private HashMap<String,Integer> variableToValues;
	public boolean printActions;
	private boolean firstJoin;

	/**
	 * Constructor
	 * @param request represents a query.
	 * @param bn represents the Bayesian network of a given query.
	 */
	public VariableElimination(String request,bayesianNet bn)
	{
		this.firstJoin = false;
		this.idFactor = 1;
		this.bn = bn;
		this.variableToValues = new HashMap<String,Integer>();
		for (Iterator<NBnode> iterator = this.bn.getNodesList().iterator(); iterator.hasNext();) {
			NBnode n = (NBnode) iterator.next();
			this.variableToValues.put(n.getName(),n.getValues().size());
		}
		this.JoinOrder= new ArrayList<String>();
		request = request.replace("P(", "");

		String[] splitToInsertCalcOrder = request.split("\\),");
		try {
			String[] order = splitToInsertCalcOrder[1].split("-");
			for (int i = 0; i < order.length; i++) {
				this.JoinOrder.add(order[i]);
			}
		}
		catch (Exception e) {//join order = null
		}
		this.E = new ArrayList<String>();
		String[] splitToInsert = splitToInsertCalcOrder[0].split("\\|");
		this.Q= splitToInsert[0];
		try {
			String[] evidences = splitToInsert[1].split(",");
			for (int i = 0; i < evidences.length; i++) {
				this.E.add(evidences[i]);
			}
		}
		catch (Exception e) { //evidence array is empty
		}

		//creating factors
		this.Factors = new ArrayList<Factor>();
		for(Iterator<NBnode> iterator = this.bn.getNodesList().iterator(); iterator.hasNext();) {
			NBnode n = (NBnode) iterator.next(); 
			if(this.Q.contains(n.getName()) || isEvidence(n.getName()) || isAncestor(n)) {
				//returns true or false if we need to build for this node a factor
				this.Factors.add(new Factor(n,this.idFactor++));
				this.lastFactor().restrictFactor(this.E);
				if(this.lastFactor().size() <= 1) { //if the factor is just an evidence
					this.Factors.remove(this.lastFactor());
				}
			}
		}
		//remove the columns of the evidences from the factors
		for (int i = 0; i < this.Factors.size(); i++) {
			for (Iterator<String> iterator = E.iterator(); iterator.hasNext();) {
				String col = (String) iterator.next();
				this.Factors.set(i,this.Factors.get(i).removeColumn(col.substring(0,col.indexOf("="))));
			}
		}
	}

	/**
	 * checks if a node is one of the evidences 
	 * @param e represents an evidence
	 * @return True if the node is an evidence, otherwise returns False
	 */
	private boolean isEvidence(String e) { 
		if (!this.E.isEmpty()) {
			for (Iterator<String> iterator = this.E.iterator(); iterator.hasNext();) {
				String evidence = (String) iterator.next();
				if (evidence.contains(e)) return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * Checks whether the node is an ancestor of the Q or the evidences.
	 * @param n represents a given node.
	 * @return True if the node is an ancestor, otherwise returns False.
	 */
	private boolean isAncestor(NBnode n) {
		for (Iterator<String> iterator = this.E.iterator(); iterator.hasNext();) {
			String evidence = (String) iterator.next();
			if(isParent(n,bn.getNodeByName(evidence.substring(0,evidence.indexOf("="))))) {
				return true;
			}
		}
		return isParent(n,bn.getNodeByName(Q.substring(0,Q.indexOf("="))));
	}

	/**
	 * Checks whether the node is a parent of the Q or the evidences.
	 * @param ancestor represents a node 
	 * @param node represents query/evidence.
	 * @return True if the node is a parent, otherwise returns False.
	 */
	private boolean isParent(NBnode ancestor,NBnode node) { 
		if (node != null) {
			if (node.getParents().contains(ancestor)) return true;
			for (NBnode parent : node.parents) {
				return isParent(ancestor,parent);
			}
		}
		return false;
	}

	/**
	 * @return the last Factor that entered into the factors list.
	 */
	public Factor lastFactor() {
		return Factors.get(Factors.size()-1);
	}

	/**
	 * Writes to result of algorithm to a file
	 * @param result represents the result of the algorithm.
	 *  Contains: the probability of a given query, amount of adding and amount of multiplication.
	 * @param fileName represents the name of the new file.
	 */
	public static void writeToFile(String[] result ,String fileName){
		try {
			FileWriter fileWriter = new FileWriter(fileName, true);
			PrintWriter pw = new PrintWriter(fileWriter);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < result.length; i++) {
				sb.append(result[i] + ",");
			}
			pw.println(sb.substring(0,sb.length()-1));
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 

	/**
	 * Starts the Variable Elimination algorithm.
	 * @param NameFile represents the name of the given parameters file.
	 * @return an array with the results.
	 */
	public String[] start(String NameFile) {
		numOfMul = 0;
		numOfAdd = 0;
		ArrayList<Factor> hiddenFactorsList;
		Factor factorOfAllJoins;
		for (Iterator<String> iterator = this.JoinOrder.iterator(); iterator.hasNext();) {
			String hiddenVar = (String) iterator.next();
			hiddenFactorsList = factorsListByHiddenViarable(hiddenVar);
			this.Factors.removeAll(hiddenFactorsList);
			factorOfAllJoins = joinAll(hiddenFactorsList);
			if(factorOfAllJoins != null)
				this.Factors.add(eliminate(factorOfAllJoins,hiddenVar));
		}
		hiddenFactorsList = factorsListByHiddenViarable(this.Q.split("=")[0]);
		factorOfAllJoins = joinAll(hiddenFactorsList);
		this.Factors.add(factorOfAllJoins);
		normalize(factorOfAllJoins);
		String[] results=new String [3];
		results[0]=getAnswer(this.lastFactor());
		results[1]=""+this.numOfAdd;
		results[2]=""+this.numOfMul;
		writeToFile(results, NameFile);
		return results;
	}

	/**
	 * Gets the last factor and return the probability asked.
	 * @param f represents a given factor.
	 * @return the result of a query.
	 */
	public String getAnswer(Factor f) {
		String[] splitQ = this.Q.split("=");
		int colIndex=f.getPositionByVariable(splitQ[0]);
		String value=splitQ[1];
		int i=0;
		for(;i<f.getTable().size();i++) {
			if(f.iloc(i)[colIndex].equals(value))
				break;
		}
		double prob=f.RowProb(i);
		DecimalFormat df= new DecimalFormat("#.#####");// only 5 decimal places.
		df.setRoundingMode(RoundingMode.CEILING);
		return df.format(prob);
	}

	/**
	 * Finds the factors that a hidden variable contains.
	 * @param hiddenVariable represents a hidden variable to search.
	 * @return a list of factors that the hidden variable contains.
	 */
	public ArrayList<Factor> factorsListByHiddenViarable(String hiddenVariable) {
		ArrayList<Factor> result = new ArrayList<Factor>();
		for (Iterator<Factor> iterator = this.Factors.iterator(); iterator.hasNext();) {
			Factor factor = (Factor) iterator.next();
			if(factor.getHeaderColumns().contains(hiddenVariable)) {
				result.add(factor);
			}
		}
		return result;
	}

	/**
	 * Join all the factors of the hidden variables according to minimum actions order.
	 * @param factorsList represents a list of the factors of the hidden variables.
	 * @return the factor after all the joins operations.
	 */
	private Factor joinAll(ArrayList<Factor> factorsList) {
		while(factorsList.size() > 1) {
			Factor[] toJoin = minimumActions(factorsList);
			factorsList.remove(toJoin[0]);
			factorsList.remove(toJoin[1]);
			factorsList.add(join(toJoin[0],toJoin[1]));
		}
		if (!factorsList.isEmpty())
			return factorsList.get(0);
		return null;
	}

	/**
	 * Finds 2 factors with the minimal multiplication actions.
	 * If there are two options with the same amount of actions
	 * the function will return the two factors with the minimal ASCII code.
	 * @param factorsList the list of the factors.
	 * @return two factors that their joining will happen with minimum actions.
	 */
	private Factor[] minimumActions(ArrayList<Factor> factorsList) {
		HashMap<String,Integer> idToNumberOfActions = new HashMap<String,Integer>();
		HashMap<String,Factor[]> nameToFactors = new HashMap<String,Factor[]>();
		for (int i = 0; i < factorsList.size(); i++) {
			Factor factor1 = factorsList.get(i);
			for (int j = i; j < factorsList.size(); j++) {
				Factor factor2 = factorsList.get(j);
				if(!factor1.equals(factor2)) {
					int countActions = numberOfActions(factor1,factor2);
					idToNumberOfActions.put(setToString(factor1.getHeaderColumns()) + "," + setToString(factor2.getHeaderColumns()),countActions);
					Factor[] p = {factor1,factor2};
					nameToFactors.put(setToString(factor1.getHeaderColumns()) + "," + setToString(factor2.getHeaderColumns()),p);
				}
			}
		}
		return findMinAction(idToNumberOfActions,nameToFactors);
	}

	/**
	 * Find the minimal amount of actions required to join. 
	 * @param numberOfAction represents a hash map that
	 * the key is a number of action and the value is an array of two factors.
	 * @return the minimal amount.
	 */
	private Factor[] findMinAction(HashMap<String,Integer> idToNumberOfActions,HashMap<String,Factor[]> nameToFactors) {
		int minAction = Collections.min(idToNumberOfActions.values());
		ArrayList<String> potenchialFactors = getAllKeysForValue(idToNumberOfActions,minAction);
		this.numOfMul += minAction;
		//Consider ASCII case
		String answer = potenchialFactors.get(0);
		int asciiValue=asciiName(potenchialFactors.get(0).split(","));
		for (int i = 1; i < potenchialFactors.size(); i++) {
			int asciiValue2=asciiName(potenchialFactors.get(i).split(","));
			if(asciiValue2<asciiValue)
				answer=potenchialFactors.get(i);
		}
		return nameToFactors.get(answer);
	}

	/**
	 * Finds the ASCII value of a string.
	 * @param sp represents the given string.
	 * @return the ASCII value.
	 */
	private int asciiName(String[] sp)
	{
		int asciiValue =0;
		for (int i = 0; i < sp[0].length(); i++) {
			asciiValue += (int) sp[0].charAt(i);
		}
		for (int i = 0; i < sp[1].length(); i++) {
			asciiValue += (int) sp[1].charAt(i);
		}
		return asciiValue;
	}

	/**
	 * Converts a linked hash set to a string.
	 * @param a represents a given linked hash set.
	 * @return a string represents the set.
	 */
	private String setToString(LinkedHashSet<String> a) {
		StringBuilder listString = new StringBuilder();

		for (String s : a)
		{
			listString.append(s);
		}
		return listString.substring(0);
	}

	/**
	 * Finds all the keys in a hash map with the same value.
	 * @param idToNumberOfActions represents the given hash map to search.
	 * @param value represents the given value.
	 * @return the list of keys whose value matches the given value.
	 */
	private ArrayList<String> getAllKeysForValue(HashMap<String,Integer> idToNumberOfActions,int value) 
	{
		ArrayList<String> listOfKeys = null;
		//Check if the Map contains the given value
		if(idToNumberOfActions.containsValue(value))
		{
			// Create an Empty List
			listOfKeys = new ArrayList<>();
			// Iterate over each entry of map using entrySet
			for (Map.Entry<String,Integer> entry : idToNumberOfActions.entrySet()) 
			{
				// Checks if value matches with given value
				if (entry.getValue().equals(value))
				{
					// Stores the key from entry to the list
					listOfKeys.add(entry.getKey());
				}
			}
		}
		return listOfKeys;	
	}

	/**
	 * Finds the amount of actions required to join f1 and f2 factors.
	 * @param f1 the first factor
	 * @param f2 the second factor
	 * @return  the amount of actions.
	 */
	private int numberOfActions(Factor f1,Factor f2) {
		LinkedHashSet<String> difference= new LinkedHashSet<String>();
		int mul = 1;
		int numOfRows = 0;
		//Find the uncommon variables of 2 factors.
		if (f1.getHeaderColumns().size() >f2.getHeaderColumns().size()) {
			difference = new LinkedHashSet<String>(f2.getHeaderColumns()); 
			difference.removeAll(f1.getHeaderColumns());
			numOfRows = f1.size();// the number of rows in the larger table.
		}
		else {
			difference = new LinkedHashSet<String>(f1.getHeaderColumns()); 
			difference.removeAll(f2.getHeaderColumns());
			numOfRows = f2.size();
		}
		//Finds the number of values the uncommon variables have.
		for (Iterator<String> iterator = difference.iterator(); iterator.hasNext();) {
			String var = (String) iterator.next();
			mul *= this.variableToValues.get(var);
		}
		//Calculates the number of multiplication required.
		if (difference.size() != 0)
			return difference.size()*numOfRows*mul;
		return numOfRows*mul;
	}
	/**
	 * Retains the common variables from both factors.
	 * @param factor1 represents the first factor
	 * @param factor2 represents the second factor
	 * @return a set of the common variables.
	 */
	private LinkedHashSet<String> getInter(Factor factor1, Factor factor2) { //factor1 < factor2
		LinkedHashSet<String> intersection = new LinkedHashSet<String>(factor1.getHeaderColumns()); 
		intersection.retainAll(factor2.getHeaderColumns()); 
		return intersection;
	}

	/**
	 * Join two factors, a new factor (contains all the variables from the 2 factors and new probabilities)
	 *  will be generated from the joining.
	 * @param factor1 represents the first factor.
	 * @param factor2 represents the second factor.
	 * @return the new factor.
	 */
	private Factor join(Factor factor1, Factor factor2) {
		// To find the union of the variables. 
		this.firstJoin = true;
		LinkedHashSet<String> unionHeaderColumns;
		if(factor1.getHeaderColumns().size()<factor2.getHeaderColumns().size()) {
			unionHeaderColumns = new LinkedHashSet<String>(factor2.getHeaderColumns()); 
			unionHeaderColumns.addAll(factor1.getHeaderColumns());
		}
		else {
			unionHeaderColumns = new LinkedHashSet<String>(factor1.getHeaderColumns()); 
			unionHeaderColumns.addAll(factor2.getHeaderColumns());
		}
		Factor returnFactor = new Factor(unionHeaderColumns,this.idFactor++);
		LinkedHashSet<String> inter= getInter(factor1,factor2);
		int interArr[][]=new int[2][inter.size()];
		int i=0;
		//Finds the columns of the common variables in each of the factors.
		for (Iterator<String> iterator = inter.iterator(); iterator.hasNext();i++)
		{
			String comVar= iterator.next();
			interArr[0][i]=factor1.getPositionByVariable(comVar);
			interArr[1][i]=factor2.getPositionByVariable(comVar);		
		}
		//Builds the common row of the 2 factors.
		for(int rows1=0;rows1<factor1.getTable().size();rows1++)
		{
			for(int rows2=0;rows2<factor2.getTable().size();rows2++) 
			{
				for( i=0;i<inter.size();i++)
				{
					// if found an uncommon value in the row.
					if(!factor2.iloc(rows2)[interArr[1][i]].equals(factor1.iloc(rows1)[interArr[0][i]]))
						break;	
					// if found the last common value of the row.
					if(factor2.iloc(rows2)[interArr[1][i]].equals(factor1.iloc(rows1)[interArr[0][i]])&&(i==inter.size()-1))
					{
						//Use the variables of the larger factor and build the common row.
						if(factor1.getHeaderColumns().size()<factor2.getHeaderColumns().size())
						{
							String[] newRow= new String [factor2.getHeaderColumns().size()+1];
							for(int c2=0;c2<factor2.getHeaderColumns().size();c2++) 
								newRow[c2]=factor2.iloc(rows2)[c2];
							returnFactor.addRow(newRow);
							double newProb =factor2.RowProb(rows2)*factor1.RowProb(rows1);
							returnFactor.iloc(returnFactor.getRowsNumber()-1)[returnFactor.getHeaderColumns().size()]=""+newProb;
						}
						else {
							String[] newRow= new String [factor1.getHeaderColumns().size()+factor2.getHeaderColumns().size()-inter.size()+1];
							for(int c1=0;c1<factor1.getHeaderColumns().size();c1++) 
								newRow[c1]=factor1.iloc(rows1)[c1];
							int rowCol=factor1.getHeaderColumns().size();
							for(int c2=0;c2<factor2.getHeaderColumns().size();c2++) {
								if(!inter.contains(factor2.getVariableByPosition(c2)))
								{
									newRow[rowCol]=factor2.iloc(rows2)[c2];	
									rowCol++;
								}
							}
							//Set the common probability in the new row.
							newRow[rowCol]= ""+factor2.RowProb(rows2)*factor1.RowProb(rows1);
							returnFactor.addRow(newRow);
						}
					}
				}
			}
		}
		if(this.printActions) System.out.println("join: "+factor1.getId()+" with "+factor2.getId()+":\n" + returnFactor.toString() + "\nnumOfAdd=" +this.numOfAdd + "\tnumOfMul= "+this.numOfMul +"\n______________________________________\n");
		return returnFactor;
	}

	/**
	 * Sum out the column of the hidden variable.
	 * @param factor represents the given factor.
	 * @param variable represents the hidden variable.
	 * @return a new factor after the elimination.
	 */
	private Factor eliminate(Factor factor, String variable) { 
		//Removes the column  of the hidden variable.
		factor = factor.removeColumn(variable);
		Factor returnFactor = new Factor(factor.getHeaderColumns(),factor.getId());

		int stop =factor.getTable().size();
		//Finds 2 rows in the factor with the same values of the other variables and summarizes them.
		for(int rows1=0;rows1<stop;rows1++)
		{
			String[] newRow = new String [factor.getHeaderColumns().size()+1];
			double sumProb = factor.RowProb(factor.iloc(rows1));
			int count = 0;
			for(int nextRows=rows1+1;nextRows<factor.getTable().size();nextRows++) 
			{
				for( int c=0;c<factor.getHeaderColumns().size();c++)
				{
					if(!factor.iloc(rows1)[c].equals(factor.iloc(nextRows)[c])) break;
					int col_num=0;// the current column in the new row.
					if(c==factor.getHeaderColumns().size()-1)
					{
						for(c=0;c<factor.getHeaderColumns().size();c++) { 
							newRow[col_num]=factor.iloc(rows1)[c];
							col_num++;
						}
						sumProb += factor.RowProb(nextRows);
						count++;
					}
				}
			}
			//if the sum of probabilities has changed and has not been added to the new factor.
			if(sumProb != factor.RowProb(factor.iloc(rows1)) && !alreadyAdd(newRow,returnFactor)) {
				returnFactor.addRow(newRow);
				returnFactor.setRowProb(returnFactor.getRowsNumber()-1, sumProb);
				this.numOfAdd += count;
			}
		}
		if(this.printActions) System.out.println("eliminate " +variable+" from "+returnFactor.getId()+"\n" + returnFactor.toString() + "\nnumOfAdd=" +this.numOfAdd + "\tnumOfMul= "+this.numOfMul +"\n______________________________________\n");
		return returnFactor;
	}
	
	/**
	 * Checks if a row with the same value has been added to the new factor
	 * @param newRow represents the new row we want to add to to the new factor
	 * @param returnFactor represents the new factor to be checked.
	 * @return True if the row has been added to the factor, otherwise returns False.
	 */
	private boolean alreadyAdd(String[] newRow, Factor returnFactor) {
		boolean found=false;
		boolean equalRow = false;
		for(int r=0;r<returnFactor.getRowsNumber() && !found;r++) 
		{
			equalRow = true;
			for( int c=0;c<returnFactor.getHeaderColumns().size();c++)
			{
				if(!returnFactor.iloc(r)[c].equals(newRow[c])) equalRow = false;
			}
			if(equalRow) found = true;
		}
		return found;
	}

	/**
	 * Normalize the result factor.
	 * @param factor represents the factor that has to be normalized
	 */
	private void normalize(Factor factor) {
		if(this.firstJoin) {
			double sum = factor.RowProb(0);
			for (int i = 1; i < factor.getRowsNumber(); i++) {
				sum += factor.RowProb(i);			
			}
			this.numOfAdd += factor.getRowsNumber()-1; //we initialized the sum to be the first row probability and start to sum the probabilities from the second row
			for(int i=0;i<factor.getTable().size();i++) {
				factor.setRowProb(i,factor.RowProb(i)/sum);
			}
			if(this.printActions) System.out.println("normalize for "+factor.getId()+"\n" + factor.toString()+ "\nnumOfAdd=" +this.numOfAdd + "\tnumOfMul= "+this.numOfMul +"\n______________________________________\n");
		}
	}
	
	/**
	 * Represents the Query and the factors as a string.
	 */
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
