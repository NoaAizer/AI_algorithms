import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class SetNet {
	private String pathFile;//"C:\\Users\\idsha\\eclipse-workspace\\Algoritmim_in_AI\\input.txt";
	private bayesianNet bn=null;
	/** 
	 * Gets a file and creats a bayesian net with the data.
	 * @param dataFile the given file
	 */
	public SetNet(String dataFile) {
		this.pathFile = dataFile;

		try {
			FileInputStream fstream = new FileInputStream(this.pathFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			int i = 0;
			String strLine;
			NBnode temp=null;
			while ((strLine = br.readLine()) != null)   {
				//			System.out.println (strLine);

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
					//				int j=0;
					//				for (;j < row.length; j++) {
					//					if(row[j].contains("=true"))
					//						break;
					//				}
				}

				//			System.out.println("line:"+i);
				i++;
				if(strLine.contains("Queries")) {break;}
				//				br.close();
			}

		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	/**
	 * @return  the baysen net of a node
	 */
	public bayesianNet getNet() {
		return this.bn;
	}
}