import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * implement VariableElimination algorithm.
 * - each query has his one VariableElimination object.
 * @author ido shapira & noa aizer
 *
 */
public class VariableElimination {
	private int idFactor;
	private String Q;// the variable of the query
	private ArrayList<String> E;// a list of the evidences
	private PriorityQueue<String> JoinOrder;// the order of the joining
	ArrayList<Factor> Factors;
	private bayesianNet bn;
	private int numOfMul;
	private int numOfAdd;
	private HashMap<String,Integer> variableToValues;
	public boolean printActions;
	/**
	 * Constructor
	 * @param request represents a query.
	 * @param bn represents the bayesian net of a given query.
	 */
	public VariableElimination(String request,bayesianNet bn)
	{
		this.idFactor = 1;
		this.bn = bn;
		this.variableToValues = new HashMap<String,Integer>();
		for (Iterator<NBnode> iterator = this.bn.getNodesList().iterator(); iterator.hasNext();) {
			NBnode n = (NBnode) iterator.next();
			this.variableToValues.put(n.getName(),n.getValues().size());
		}
		this.JoinOrder= new PriorityQueue<String>();
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
			NBnode n = (NBnode) iterator.next(); //new NBnode ((NBnode) iterator.next(),this.E);
			if(this.Q.contains(n.getName()) || isEvidence(n.getName()) || isAncestor(n)) {
				//return true or false if we need to build for this node a factor
				this.Factors.add(new Factor(n,this.idFactor++));
				this.lastFactor().restrictFactor(this.E);
				if(this.lastFactor().size() <= 1) { //if the factor is just evidence
					this.Factors.remove(this.lastFactor());
				}
			}
		}
		for (int i = 0; i < this.Factors.size(); i++) {
			for (Iterator<String> iterator = E.iterator(); iterator.hasNext();) {
				String col = (String) iterator.next();
				this.Factors.set(i,this.Factors.get(i).removeColumn(col.substring(0,col.indexOf("="))));
			}
			//			Factor pointer = removeSameValuesColumn(this.Factors.get(i));
			//			this.Factors.set(i,(pointer != null) ? pointer : this.Factors.get(i));
		}
	}
	private boolean isEvidence(String e) { //return true is the node is evidence and false otherwise
		if (!this.E.isEmpty()) {
			for (Iterator<String> iterator = this.E.iterator(); iterator.hasNext();) {
				String evidence = (String) iterator.next();
				if (evidence.contains(e)) return true;
			}
			return false;
		}
		return false;
	}
	private boolean isAncestor(NBnode n) {//checking if the node is a ancestor of the Q or each evidence
		for (Iterator<String> iterator = this.E.iterator(); iterator.hasNext();) {
			String evidence = (String) iterator.next();
			if(isParent(n,bn.getNodeByName(evidence.substring(0,evidence.indexOf("="))))) {
				return true;
			}
		}
		return isParent(n,bn.getNodeByName(Q.substring(0,Q.indexOf("="))));
	}
	private boolean isParent(NBnode ancestor,NBnode node) { //node = query/evidence in the start
		if (node != null) {
			if (node.getParents().contains(ancestor)) return true;
			for (NBnode parent : node.parents) {
				return isParent(ancestor,parent);
			}
		}
		return false;
	}
	/*
	 * return the last factor from the list factors
	 */
	public Factor lastFactor() {
		return Factors.get(Factors.size()-1);
	}



	/**
	 * starting the variableElimination algorithm
	 */
	public String[] start() {
		numOfMul = 0;
		numOfAdd = 0;
		for (Iterator<String> iterator = this.JoinOrder.iterator(); iterator.hasNext();) {
			String hiddenVar = (String) iterator.next();
			ArrayList<Factor> hiddenFactorsList = factorsListByHiddenViarable(hiddenVar);
			this.Factors.removeAll(hiddenFactorsList);
			Factor factorOfAllJoins = joinAll(hiddenFactorsList);
			if(factorOfAllJoins != null)
				this.Factors.add(eliminate(factorOfAllJoins,hiddenVar));
		}
		normalize(this.lastFactor());
		String[] results=new String [3];
		results[0]=getAnswer(this.lastFactor());
		results[1]=""+this.numOfAdd;
		results[2]=""+this.numOfMul;
		writeToFile(results);
		return results;
	}
	public String getAnswer(Factor f) {
		String[] splitQ = this.Q.split("=");
		int colIndex=f.getPositionByVariable(splitQ[0]);
		String value=splitQ[1];
		int i=0;
		for(;i<f.getTable().size();i++) {
			if(f.iloc(i)[colIndex].equals(value))
				break;
		}
		return ""+f.RowProb(i);

	}
	private void writeToFile(String[] result){
		String fileName = "output.txt";
		try 
		{
			PrintWriter pw = new PrintWriter(new File(fileName));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < result.length; i++) {
				sb.append(result[i] + ",");
			}
			pw.write(sb.toString());
			pw.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
/**
 * given a hidden variable and return list of factor that the hidden variable contains
 * @param hiddenVariable a hidden variable to search
 * @return list of factor that the hidden variable contains
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
 * get a factor list and return two factors that the join will be happen with a minimum actions
 * if there are two options the function will return the two factor with the smallest ascii code
 * @param factorsList
 * @return array with to factors
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
 * 
 * @param numberOfAction hash map that key is number of action and value is the an array of two factor.
 * @return the number of minimum action for join 
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

private String setToString(Set<String> a) {
	StringBuilder listString = new StringBuilder();

	for (String s : a)
	{
		listString.append(s);
	}
	return listString.substring(0);
}

private ArrayList<String> getAllKeysForValue(HashMap<String,Integer> idToNumberOfActions,int value) 
{
	ArrayList<String> listOfKeys = null;

	//Check if Map contains the given value
	if(idToNumberOfActions.containsValue(value))
	{
		// Create an Empty List
		listOfKeys = new ArrayList<>();

		// Iterate over each entry of map using entrySet
		for (Map.Entry<String,Integer> entry : idToNumberOfActions.entrySet()) 
		{
			// Check if value matches with given value
			if (entry.getValue().equals(value))
			{
				// Store the key from entry to the list
				listOfKeys.add(entry.getKey());
			}
		}
	}
	// Return the list of keys whose value matches with given value.
	return listOfKeys;	
}

/**
 * @return number of actions to do join to f1 and f2 factors
 */
private int numberOfActions(Factor f1,Factor f2) {
	Set<String> difference= new HashSet<String>();
	int mul = 1;
	int numOfRows = 0;
	if (f1.getHeaderColumns().size() >f2.getHeaderColumns().size()) {
		difference = new HashSet<String>(f2.getHeaderColumns()); 
		difference.removeAll(f1.getHeaderColumns());
		numOfRows = f1.size();
	}
	else {
		difference = new HashSet<String>(f1.getHeaderColumns()); 
		difference.removeAll(f2.getHeaderColumns());
		numOfRows = f2.size();
	}
	for (Iterator<String> iterator = difference.iterator(); iterator.hasNext();) {
		String var = (String) iterator.next();
		mul *= this.variableToValues.get(var);
	}
	if (difference.size() != 0)
		return difference.size()*numOfRows*mul;
	return numOfRows*mul;
}
private Set<String> getInter(Factor factor1, Factor factor2) { //factor1 < factor2
	Set<String> intersection = new HashSet<String>(factor1.getHeaderColumns()); 
	intersection.retainAll(factor2.getHeaderColumns()); 
	return intersection;
}
/**
 * Join two factor by point wise product.
 * @param factor1
 * @param factor2
 * @returnA new factor will be generated form the two factors containing all variable involved.
 * Its probability will be the product of the two factors.
 */
private Factor join(Factor factor1, Factor factor2) {
	// To find union 
	Set<String> unionHeaderColumns = new HashSet<String>(factor1.getHeaderColumns()); 
	unionHeaderColumns.addAll(factor2.getHeaderColumns());
	Factor returnFactor = new Factor(unionHeaderColumns,this.idFactor++);
	Set<String> inter= getInter(factor1,factor2);
	int interArr[][]=new int[2][inter.size()];
	int i=0;
	for (Iterator<String> iterator = inter.iterator(); iterator.hasNext();i++)
	{
		String comVar= iterator.next();
		interArr[0][i]=factor1.getPositionByVariable(comVar);
		interArr[1][i]=factor2.getPositionByVariable(comVar);		
	}
	for(int rows1=0;rows1<factor1.getTable().size();rows1++)
	{
		for(int rows2=0;rows2<factor2.getTable().size();rows2++) 
		{
			for( i=0;i<inter.size();i++)
			{
				if(!factor2.iloc(rows2)[interArr[1][i]].equals(factor1.iloc(rows1)[interArr[0][i]]))
					break;	
				if(factor2.iloc(rows2)[interArr[1][i]].equals(factor1.iloc(rows1)[interArr[0][i]])&&(i==inter.size()-1))
				{
					if(factor1.getHeaderColumns().size()<factor2.getHeaderColumns().size())
					{
						String[] newRow= new String [factor2.getHeaderColumns().size()+1];
						for(int c2=0;c2<factor2.getHeaderColumns().size();c2++) 
							newRow[c2]=factor2.iloc(rows2)[c2];
						returnFactor.addRow(newRow);
						double newProb =factor2.RowProb(rows2)*factor1.RowProb(rows1);
						//	this.numOfMul++;
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
						newRow[rowCol]= ""+factor2.RowProb(rows2)*factor1.RowProb(rows1);
						//	this.numOfMul++;
						returnFactor.addRow(newRow);
					}
				}
			}
		}
	}
	if(this.printActions) System.out.println("join: "+factor1.getId()+" with "+factor2.getId()+":\n" + returnFactor.toString() + "\nnumOfAdd=" +this.numOfAdd + "\tnumOfMul= "+this.numOfMul +"\n______________________________________\n");
	return returnFactor;
}
private Factor eliminate(Factor factor, String variable) { 
	//maybe an error if the values of the variable to eliminate greater then 2
	factor = factor.removeColumn(variable);
	Factor returnFactor = new Factor(factor.getHeaderColumns(),factor.getId());
	int stop =factor.getTable().size();
	boolean flag = true;
	for(int rows1=0;rows1<stop;rows1++)
	{
		String[] newRow = new String [factor.getHeaderColumns().size()+1];
		double sumProb = factor.RowProb(factor.iloc(rows1));
		for(int nextRows=rows1+1;nextRows<factor.getTable().size();nextRows++) 
		{
			for( int c=0;c<factor.getHeaderColumns().size();c++)
			{
				if(!factor.iloc(rows1)[c].equals(factor.iloc(nextRows)[c])) break;
				int col_num=0;
				if(c==factor.getHeaderColumns().size()-1)
				{
					if(flag) {stop = nextRows; flag = false;}
					for(c=0;c<factor.getHeaderColumns().size();c++) { 
						newRow[col_num]=factor.iloc(rows1)[c];
						col_num++;
					}
					sumProb += factor.RowProb(nextRows);
					this.numOfAdd++;
				}

			}
		}
		returnFactor.addRow(newRow);
		returnFactor.setRowProb(rows1, sumProb);
	}
	//		this.numOfAdd += returnFactor.getRowsNumber();

	if(this.printActions) System.out.println("eliminate " +variable+" from "+returnFactor.getId()+"\n" + returnFactor.toString() + "\nnumOfAdd=" +this.numOfAdd + "\tnumOfMul= "+this.numOfMul +"\n______________________________________\n");
	return returnFactor;
}
private void normalize(Factor factor) {

	double sum = factor.RowProb(0);
	for (int i = 1; i < factor.getRowsNumber(); i++) {
		sum += factor.RowProb(i);			
	}
	if(sum != 1) {
		this.numOfAdd += factor.getRowsNumber()-1; //we initialized the sum to be the first row probability and start to sum the probabilities from the second row
		for(int i=0;i<factor.getTable().size();i++) {
			factor.setRowProb(i,factor.RowProb(i)/sum);
		}
		if(this.printActions) System.out.println("normalize for "+factor.getId()+"\n" + factor.toString()+ "\nnumOfAdd=" +this.numOfAdd + "\tnumOfMul= "+this.numOfMul +"\n______________________________________\n");
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
