package running;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ATLUtils.ModelTransformation;
import ATLUtils.MyRule;

public class TransformationRunner {

	private String trafoDirPath;
	private File trafoDir;
	private ArrayList<String> toolsList=new ArrayList<String>();
	private ArrayList<ModelTransformation> modelTransformations= new ArrayList<ModelTransformation>();
	private String verbose;
	
	/**
	 * Constructor need only the folder path where MT are located.
	 * All other data is always structured in the same way (see. docs/transformations-folder.md for details) 
	 * 
	 * @param trafoDirPath: folder where to find all the model transformations and the data for running
	 * 
	 */
	public TransformationRunner(String trafoDirPath) {
		this.trafoDirPath= trafoDirPath;
		this.trafoDir= new File(trafoDirPath);
		this.verbose="";
		if(trafoDir.exists()) {
			collectMTandTools();
		}
	}
	
	/**
	 * This method reads the data in folder trafoDirPath and collects all MTs 
	 * and their data (inMM, outMM, rules, infos, etc)
	 */
	private void collectMTandTools(){
		File[] trDirContent= trafoDir.listFiles();
		
		for(File f: trDirContent) {
			if(f.isDirectory()) {
				
				//Get all the infos from MT.infos file
				String [] infos=readMTInfo(f.getName());
				//Create the model transformation with all the information
				ModelTransformation MT= new ModelTransformation(f.getName(), infos[1],infos[2], infos[3],
						infos[4], infos[5]);
				//Collect all the tools for that MT
				collectToolsForMTFolder(MT);
				//Read data about MT rules
				readMTRules(MT);
				//Add the MT to the list of MTs
				//At this step, we get it ready for running)
				modelTransformations.add(MT);
			}
		}
	}
	
	/**
	 * This method collects all the different input folders for a given MT
	 * 
	 * trafoDir/MT/models/input/Tool1
	 * trafoDir/MT/models/input/Tool2
	 * 
	 * @param MT: a model transformation object
	 */
	private void collectToolsForMTFolder(ModelTransformation MT){
		String folderPath= trafoDirPath+"/"+MT.getName()+"/models/input/";
		File folder= new File(folderPath);
		File[] toolCandidates= folder.listFiles();
		
		for(File toolName: toolCandidates) {
			if(toolName.isDirectory()) {
				if(!toolsList.contains(toolName.getName())) {
					toolsList.add(toolName.getName());
				}
				MT.addTool(toolName.getName());
			}
		}
	}
	
	/**
	 * This method reads the MT.rules file of a given transformation
	 * 
	 * this file must be located in: trafoDir/MT/MT.rules.
	 * It contains the complexity weights of all rules
	 * 
	 * @param MT: a ModelTransformation object
	 */
	private void readMTRules(ModelTransformation MT) {
		File MTRules= new File(trafoDir+"/"+MT.getName()+"/"+MT.getName()+".rules");
		//System.out.println(MTRules.getName());
		if(MTRules.exists()) {
			
			BufferedReader br = null;
	        String line = "";
	        String cvsSplitBy = ",";
	        
	        try {
	            br = new BufferedReader(new FileReader(MTRules));
	            while ((line = br.readLine()) != null) {
	                String[] data = line.split(cvsSplitBy);
	                MyRule rule = new MyRule(data[0], Integer.parseInt(data[1]));
	                MT.addRule(rule);
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
			
		}
	}
	
	/**
	 * This method read the MT.infos file of a given Model Transformation name.
	 * The returned data is then used to create a ModelTransformation object
	 * 
	 *  MT.infos file is always located in trafoDir/MT/MT.infos
	 *  
	 * @param MTname: model transformation name
	 * @return list of information on this model transformation
	 *   [0=MTname, 1=module, 2=,3=,4=,5=]
	 */
	private String [] readMTInfo(String MTname){
		
		File MTinfos= new File(trafoDir+"/"+MTname+"/"+MTname+".infos");
		String [] infos= new String[6];
				
		if(MTinfos.exists()) {
			BufferedReader br = null;
	        String line = "";
	        String cvsSplitBy = "=";
	        
	        try {
	            br = new BufferedReader(new FileReader(MTinfos));
	            int i=0;
	            while (i<6 && (line = br.readLine()) != null) {
	            	String[] data = line.split(cvsSplitBy);
	                infos[i]=data[1];
	                i++;
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
		}
				
		return infos;
	}
	
	/**
	 * Checks that a given folder containing model transformations exists
	 * 
	 * @return true if this.trafoDir exists
	 */
	public boolean isTrafoDirExist() {
		
		if(trafoDir.exists()) {
			return true;
		}
		else {
			return false;
		}
		
	}
			
	/**
	 * This method runs all the transformations that are in folder: trafoDirPath.
	 * You can find in <docs/transformations-folder.md> files how the folder of transformations should be formed.
	 * 
	 * 
	 * @return true if running was performed with success
	 * 			
	 * This method produces a csv file: trafoDirPath/results/executions-toolName.csv
	 */
	public boolean runAllTransformations() {
		
		verbose= verbose+ this.printInfo();
		verbose= verbose+ this.printAllMT();
		verbose= verbose+ "RUNNING\n\n";
		
		for(ModelTransformation mt: this.modelTransformations) {
			verbose= verbose+ "running ["+ mt +"] model transformation...\n";
			
			for(String tool: mt.getTools()) {
				verbose= verbose+"\tprocessing models generated by ["+tool+"] tool...\n";
				
				//Run the Model Transformation
				// Get back the log output, execution time, and the results
				// After that, generate result files
				// execution-heureDatename.log
				// execution.result
			}
			
			verbose= verbose+"\n";
		}		
		
		return true;		
	}
	/**
	 * This method prints a detailed overview of all the MTs found in trafoDir
	 *  
	 * @return pretty print of Model Transformations data
	 */
	public String printAllMT() {
		String res="MODEL TRANSFORMATIONS\n";
		for(ModelTransformation mt: this.modelTransformations) {
			res= res+"\t"+mt.prettyPrint();
		}
		
		return res;
	}
	
	/**
	 * This method prints a quick overview of the trafoDir folder (#MTs, #Tools)
	 * 
	 * @return quick info print
	 */
	public String printInfo() {
		
		String result="";
		result=result+"INFORMATION\n";
		result=result+"\tFolder: "+ trafoDirPath +"\n";
		result=result+"\tModel transformations: "+  modelTransformations.size()+ " "+ modelTransformations +"\n";
		result=result+"\tModel finding tools: "+  toolsList.size()+ " "+ toolsList +"\n\n";
		return result;
	}
	
	public ArrayList<ModelTransformation> getModelTransformations() {
		return modelTransformations;
	}

	public ArrayList<String> getToolsList() {
		return toolsList;
	}

	public String getVerbose() {
		return verbose;
	}
	
	
		
}
