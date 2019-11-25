
/**
 * Class to represent the Table Object consisting of probability rows
 * 
 * @author Marcel de Korte, Moira Berens, Djamari Oetringer, Abdullahi Ali, Leonieke van den Bulk
 */

import java.util.ArrayList;
import java.util.Collections;

public class cpt1 {
	
	private ArrayList<String> variables = new ArrayList<String>();
	private ArrayList<Integer> cardinalities = new ArrayList<Integer>();
	private ArrayList<Double> values = new ArrayList<Double>();
	
	
	private int stride(String variable) {
		int stride = 1;
		
		int varIndex = this.variables.indexOf(variable); 
		if (varIndex > 0) {
			for (int i = 0; i < varIndex; i++) {
				stride *= this.cardinalities.get(i);
			}
		} else if( varIndex < 0) {
			stride = 0;
		}
		
		return stride;
	}
	
	
	
	public cpt multiply(cpt other) {
		
		ArrayList<String> productVariables = new ArrayList<String>();
		ArrayList<Integer> productCardinalities = new ArrayList<Integer>();
		
		productVariables.addAll(this.variables);
		productCardinalities.addAll(this.cardinalities);
		
		for (int i = 0; i < other.variables.size(); i++) {
			if (!productVariables.contains(other.variables.get(i))) {
				productVariables.add(other.variables.get(i));
				productCardinalities.add(other.cardinalities.get(i));
			}
		}
		
		ArrayList<Integer> assignment = new ArrayList<Integer>(Collections.nCopies(productVariables.size(), 0));
		
		int productTotalRows = 1;
		for (int i = 0; i < productCardinalities.size(); i++) {
			productTotalRows *= productCardinalities.get(i) ;
		}
		
		int j = 0;
		int k = 0;
		ArrayList<Double> productValues = new ArrayList<Double>(productTotalRows);
		for (int i = 0; i < productTotalRows; i++) {
			// ### DEBUG
			try {
				productValues.add(this.values.get(j) * other.values.get(k));
			} catch (Exception e) {
				e.printStackTrace();
			}
			// --- DEBUG
			for (int l = 0; l < productVariables.size(); l++) {
				assignment.add(l, assignment.get(l) + 1);
				if (assignment.get(l) == productCardinalities.get(l)) {
					assignment.add(l, 0);
					j = j - (productCardinalities.get(l) - 1) * this.stride(productVariables.get(l));
					k = k - (productCardinalities.get(l) - 1) * other.stride(productVariables.get(l));
				} else {
					j = j + this.stride(productVariables.get(l));
					k = k + other.stride(productVariables.get(l));
					break;
				}
			}
		}
		
		cpt product = new cpt();
		product.setVariables(productVariables);
		product.setCardinalities(productCardinalities);
		product.setValues(productValues);
		
		return product;
	}
	
	
	public cpt marginalize(ArrayList<String> variablesToRemove) {
		
		ArrayList<String> marginalVariables = new ArrayList<String>();
		ArrayList<Integer> marginalCardinalities = new ArrayList<Integer>();
		
		for (int i = 0; i < this.variables.size(); i++) {
			if (!variablesToRemove.contains(this.variables.get(i))) {
				marginalVariables.add(this.variables.get(i));
				marginalCardinalities.add(this.cardinalities.get(i));
			}
		}
		
		ArrayList<Integer> assignment = new ArrayList<Integer>(Collections.nCopies(this.variables.size(), 0));
		
		int marginalTotalRows = 1;
		for (int i = 0; i < marginalCardinalities.size(); i++) {
			marginalTotalRows *= marginalCardinalities.get(i) ;
		}
		
		int thisTotalRows = 1;
		for (int i = 0; i < this.cardinalities.size(); i++) {
			thisTotalRows *= this.cardinalities.get(i) ;
		}
		
		cpt marginal = new cpt();
		marginal.setVariables(marginalVariables);
		marginal.setCardinalities(marginalCardinalities);
		
		int j = 0;
		int k = 0;
		ArrayList<Double> productValues = new ArrayList<Double>(Collections.nCopies(marginalTotalRows, 0.0));
		while (true) {
			productValues.set(k, this.values.get(j) + productValues.get(k));
			
			if (j == (thisTotalRows-1)) {
				break;
			}
			
			for (int l = 0; l < this.variables.size(); l++) {
				assignment.add(l, assignment.get(l) + 1);
				if (assignment.get(l) == this.cardinalities.get(l)) {
					assignment.add(l, 0);
					j = j - (this.cardinalities.get(l) - 1) * this.stride(this.variables.get(l));
					k = k - (this.cardinalities.get(l) - 1) * marginal.stride(this.variables.get(l));
				} else {
					j = j + this.stride(this.variables.get(l));
					k = k + marginal.stride(this.variables.get(l));
					break;
				}
			}
		}
		
		marginal.setValues(productValues);
		
		return marginal;
	}
	
	
	
	public boolean contains(String variable) {
		return this.variables.contains(variable);
	}



	public ArrayList<String> getVariables() {
		return variables;
	}



	public void setVariables(ArrayList<String> variables) {
		this.variables = variables;
	}



	public ArrayList<Integer> getCardinalities() {
		return cardinalities;
	}



	public void setCardinalities(ArrayList<Integer> cardinalities) {
		this.cardinalities = cardinalities;
	}



	public ArrayList<Double> getValues() {
		return values;
	}



	public void setValues(ArrayList<Double> values) {
		this.values = values;
	}
	
	
	
}