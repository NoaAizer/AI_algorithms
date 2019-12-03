import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;


/**
 * implement VariableElimination algorithm.
 * - each query has his one VariableElimination object.
 * @author ido shapira & noa aizer
 *
 */
public class VariableElimination {
	private String Q;// the variable of the query
	private ArrayList<String> E;// a list of the evidences
	private PriorityQueue<String> JoinOrder;// the order of the joining
	ArrayList<Factor> Factors;
	private bayesianNet bn;
	/**
	 * Constructor
	 * @param request represents a query.
	 * @param bn represents the bayesian net of a given query.
	 */
	public VariableElimination(String request,bayesianNet bn)
	{
		this.bn = bn;
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
		for (int i = 0; i < evidences.length; i++) {
			this.E.add(evidences[i]);
		}
		//creating factors
		this.Factors = new ArrayList<Factor>();
		for(Iterator<NBnode> iterator = this.bn.getNodesList().iterator(); iterator.hasNext();) {
			NBnode n = (NBnode) iterator.next(); //new NBnode ((NBnode) iterator.next(),this.E);
			if(this.Q.contains(n.getName()) || isEvidence(n.getName()) || isAncestor(n)) {
				//return true or false if we need to build for this node a factor
				Factors.add(new Factor(n));
				this.lastFactor().restrictFactor(this.E);
				if(this.lastFactor().size() <= 1) { //if the factor is just evidence
					Factors.remove(this.lastFactor());
				}
			}
		}
		for (Factor factor : Factors) {
			int id=factor.getId();
			Factor remFac=removeSameValuesColumn(factor);
			if(remFac!=null) {
				factor=new Factor(remFac.getHeaderColumns());
				factor.setTable(remFac.getTable());
			}
		}
	}
	private Factor removeSameValuesColumn(Factor f)
	{
		Factor returnFactor=null;
		for(int col=0;col<f.getHeaderColumns().size();col++) 
		{
			boolean flag=true;
			String value=f.iloc(0)[col];
			for(int rows=1;rows<f.getTable().size();rows++) {
				if(!f.iloc(rows)[col].equals(value)) {
					flag=false;
					break;
				}
			}
			if(flag)
			{
				Set<String> newHeaderColumns= new HashSet <String>();
				for (Iterator<String> iterator = f.getHeaderColumns().iterator(); iterator.hasNext();) {
					String tempVar= iterator.next();
					if(tempVar.equals(f.getVariableByPosition(col)))
						newHeaderColumns.add(tempVar);
				}
				returnFactor= new Factor (newHeaderColumns);
				for(int r=0;r<f.getTable().size();r++) {
					String[] newRow= new String [f.getHeaderColumns().size()];
					int rowCol=0;
					for(int c=0;c<f.getHeaderColumns().size();c++) 
						if(c!=col) {
							newRow[rowCol]=f.iloc(r)[c];
							rowCol++;
						}
					newRow[rowCol]=""+f.RowProb(r);
					returnFactor.addRow(newRow);

				}
			}
		}
		return returnFactor;
	}

	private boolean isEvidence(String e) { //return true is the node is evidence and false otherwise
		for (Iterator<String> iterator = this.E.iterator(); iterator.hasNext();) {
			String evidence = (String) iterator.next();
			if (evidence.contains(e)) return true;
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
	public void start() {
		for (Iterator<String> iterator = this.JoinOrder.iterator(); iterator.hasNext();) {
			String hiddenVar = (String) iterator.next();
			ArrayList<Factor> hiddenFactorsList = factorsListByHiddenViarable(hiddenVar);
			joinAll(hiddenFactorsList);
			this.Factors.add(eliminate(hiddenFactorsList.get(0),hiddenVar));
		}
		Factor answer = normalize(this.Factors.get(0));
		System.out.println(answer.toString());
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
	public void joinAll(ArrayList<Factor> factorsList) {
		while(factorsList.size() > 1) {
			Factor[] toJoin = minimumActions(factorsList);
			factorsList.add(join(toJoin[0],toJoin[1]));
		}

		//	       // To find the symmetric difference 
		//        Set<Integer> difference = new HashSet<Integer>(a); 
		//        difference.removeAll(b); 
		//        System.out.print("Difference of the two Set"); 
		//        System.out.println(difference); 
		//
	}
	/**
	 * get a factor list and return two factors that the join will be happen with a minimum actions
	 * if there are two options the function will return the two factor with the smallest ascii code
	 * @param factorsList
	 * @return array with to factors
	 */
	private Factor[] minimumActions(ArrayList<Factor> factorsList) {
		HashMap<Integer,Factor[]> numberOfAction = new HashMap<Integer,Factor[]>();
		for (Iterator<Factor> iterator = factorsList.iterator(); iterator.hasNext();) {
			Factor factor1 = (Factor) iterator.next();
			for (Iterator<Factor> iterator2 = factorsList.iterator(); iterator2.hasNext();) {
				Factor factor2 = (Factor) iterator2.next();
				if(!factor1.equals(factor2)) { //implemnt equals for Factor
					Factor[] p = {factor1,factor2};
					numberOfAction.put(numberOfActions(factor1,factor2),p);
				}
			}
		}
		int minAction = 0;//do key minimum function
		return numberOfAction.get(minAction);
	}
	/**
	 * @return number of actions to do join to f1 and f2 factors
	 */
	private int numberOfActions(Factor f1,Factor f2) {
		int answer = 0;
		return answer;
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
	public Factor join(Factor factor1, Factor factor2) {
		// To find union 
		Set<String> unionHeaderColumns = new HashSet<String>(factor1.getHeaderColumns()); 
		unionHeaderColumns.addAll(factor2.getHeaderColumns());
		Factor returnFactor = new Factor(unionHeaderColumns);
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
							returnFactor.addRow(newRow);	
						}
					}
				}
			}
		}
		//		if(inter.size() ==1) {
		//			returnFactor.setTable(factor2.getTable());
		//			for (Iterator<String> iterator = inter.iterator(); iterator.hasNext();)
		//			{
		//				String temp_var= iterator.next();
		//				int indexCol1=factor1.getPositionByVariable(temp_var);
		//				int indexCol2=factor2.getPositionByVariable(temp_var);
		//
		//				for(int i=0;i<factor2.getTable().size();i++)
		//				{
		//					for(int j=0;j<factor1.getTable().size();j++) {
		//
		//						if(factor2.iloc(i)[indexCol2].equals(factor1.iloc(j)[indexCol1]))
		//						{
		//							double newProb =factor2.RowProb(i)*factor1.RowProb(j);
		//							returnFactor.setRowProb(i,newProb);
		//							break;
		//						}
		//
		//					}
		//				}
		//			}
		//
		//		}
		return returnFactor;
	}
	public static Factor eliminate(Factor factor, String variable) {
		int position = factor.getPositionByVariable(variable);
		HashMap<String, Double> groupedMap = new HashMap<String, Double>();
		for (String[] row : factor.getTable()) {
			String key = "";
			for (int i = 0; i < row.length; i++) {
				if (i != position) {
					key += row[i];
				}
			}
			Double probability = groupedMap.get(key);
			if (probability != null) {
				probability += factor.RowProb(row);
			} else {
				probability = factor.RowProb(row);
			}
			groupedMap.put(key, probability);
		}

		ArrayList<String> variables = new ArrayList<String>();
		variables.addAll(factor.getHeaderColumns());
		Set<String> newVariables = new HashSet<String>(variables.size() - 1);
		for (String v : variables) {
			if (!v.equals(variable)) {
				newVariables.add(v);
			}
		}
		Factor returnFactor = new Factor(newVariables);
		for (String key : groupedMap.keySet()) {
			Double val = groupedMap.get(key);
			String[] newRow = {key,""+val};
			returnFactor.addRow(newRow);
		}

		return returnFactor;
	}
	private static Factor normalize(Factor factor) {
		Factor returnFactor = new Factor(factor.getHeaderColumns());
		double sum = 0;
		for (String[] row : factor.getTable()) {
			sum += factor.RowProb(row);
		}
		for (String[] row : factor.getTable()) {
			String[] newRow = new String[row.length+1];
			for (int i = 0; i < row.length; i++) {
				newRow[i] = row[i];
			}
			newRow[row.length] = "" +factor.RowProb(row)/sum;
			returnFactor.addRow(newRow);
		}
		return returnFactor;
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
