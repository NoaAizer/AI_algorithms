import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class getInput {

	private String pathFile;
	private bayesianNet bn=null;
	private ArrayList<VariableElimination> veList=null;

	/**
	 * constructor
	 * @param dataFile- a path to the file
	 */
	public getInput(String dataFile) {
		this.pathFile = dataFile;
	}
	/**
	 * Queries set
	 * @return an array list of all the queries from the input file.
	 */
	public ArrayList<VariableElimination> SetQueries() {
		try {
			FileInputStream fstream = new FileInputStream(this.pathFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;
			while ((strLine = br.readLine()) != null)   {
				if(strLine.contains("Queries")) {
					this.veList = new ArrayList<VariableElimination>();
				}
				if(strLine.contains("P(")) {
					this.veList.add(new VariableElimination(strLine,this.bn));
				}
			}
			br.close();

		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		return this.veList;
	}
	/** 
	 *  creates a bayesian net with the data from the input file.
	 * @param dataFile is the given file
	 */
	public bayesianNet SetNet() {
		try {
			FileInputStream fstream = new FileInputStream(this.pathFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			int i = 0;
			String strLine;
			NBnode temp=null;
			while ((strLine = br.readLine()) != null)   {
				if (i==0) {
					bn = new bayesianNet(strLine);
				}
				if(i==1) {
					strLine = strLine.substring(11);
					String[] vars = strLine.split(",");
					for (int j = 0; j < vars.length; j++) {
						bn.getNodesList().add(new NBnode(vars[j]));
					}
				}
				if(strLine.contains("Var ")){
					strLine = strLine.replace("Var ","");
					temp = bn.getNodeByName(strLine);
				}
				if(strLine.contains("Values: ")){
					strLine = strLine.replace("Values: ","");
					String[] values = strLine.split(",");
					for (int j = 0; j < values.length; j++) {
						temp.getValues().add(values[j]);
					}
				}
				if(strLine.contains("Parents: ")){
					if (!strLine.contains("none")){
						strLine = strLine.replace("Parents: ","");
						String[] values = strLine.split(",");
						for (int j = 0; j < values.length; j++) {
							NBnode parent = bn.getNodeByName(values[j]);
							temp.getParents().add(parent);
							parent.getChilds().add(temp);
							temp.getTable().getHeaderColumns().add(parent.getName());
						}
					}
					temp.getTable().getHeaderColumns().add(temp.getName());
				}
				if(strLine.contains("=")){
					String[] row = strLine.split(",");
					temp.getTable().addRow(row);
				}
				i++;
				if(strLine.contains("Queries")) {break;}
			}
			br.close();

		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		return this.bn;
	}
}