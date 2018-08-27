package ATLauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.eclipse.m2m.atl.core.ATLCoreException;

import ATLUtils.Utils;
import ATLUtils.ExecutionOutput;
import ATLUtils.ModelTransformation;
import ATLUtils.MyRule;

/**
 * This class reads, collects, runs and gathers results of execution for model transformations.
 * 
 * All the model transformations are contained in a given folder (the key field of any TransformationRunner object).
 * 
 * Calling the methods if this Object produces many output files: log, csv ...
 * 
 * @author Adel Ferdjoukh
 *
 */
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
	 * @throws ATLCoreException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * 
	 */
	public TransformationRunner(String trafoDirPath) throws FileNotFoundException, IOException, ATLCoreException {
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
	 * @throws ATLCoreException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void collectMTandTools() throws FileNotFoundException, IOException, ATLCoreException{
		File[] trDirContent= trafoDir.listFiles();
		
		for(File f: trDirContent) {
			if(f.isDirectory()) {
				
				//Get ATL file path
				String atlFilePath="";
				FilenameFilter atlFilter = new FilenameFilter() {
					public boolean accept(File dir, String name) {
						String lowercaseName = name.toLowerCase();
						if (lowercaseName.endsWith(".atl")) {
							return true;
						} else {
							return false;
						}
					}
				};
				
				File [] subfiles= f.listFiles(atlFilter);
				if(subfiles.length>=1) {
					atlFilePath=subfiles[0].getPath();
				}
				//Get all the infos from MT.infos file
				String [] infos=readMTInfo(f.getName());
				//Create the model transformation with all the information
				ModelTransformation MT= new ModelTransformation(f.getName(),atlFilePath,infos[1],
																infos[2], infos[3],
																infos[4], infos[5]);
				//CreateRulesScores
				MT.createAllRulesScores();
				//Collect all the tools for that MT
				collectToolsForMTFolder(MT);
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
	public boolean doesTrafoDirExist() {
		
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
		String logFile= Utils.generateFileNamePostfix("execution", "log");
		String resFile= Utils.generateFileNamePostfix("execution-results", "csv");
		String globalResults="";
		String failure;
		
		for(ModelTransformation mt: this.modelTransformations) {
			verbose= verbose+ "RUNNING ["+ mt +"] model transformation...\n\n";
		
			//Create a failure file for each Model Transformation
			String failFile= Utils.generateFileNamePostfix("failed-models-"+mt.getName(), "log");
			failure="";
			
			for(String tool: mt.getTools()) {
				verbose= verbose+"processing models generated by ["+tool+"] tool...\n";
				
				//Run the Model Transformation
				ATLauncher l = new ATLauncher();
				ExecutionOutput exec= l.launch(trafoDirPath, mt.getName(), mt.getModule(), tool, 
						mt.getInMMRelativePath(), mt.getInMM(), mt.getOutMMRelativePath(), mt.getOutMM());
				
				verbose= verbose+ exec.getLog();
				verbose= verbose+"\n";
								
				int coverageScore= weightedRuleCoverage(mt.getRules(), exec.getExecutedRules());
				
				globalResults= globalResults+ exec.getSummary()+";"+coverageScore+";"+mt.getMaxScore()+"\n";
				
				verbose= verbose+ " coverageScore: "+coverageScore+ "\n";
				
				failure=failure+exec.getFail();
								
				//Create the specific result per tool
				if(!exec.getSuccess().equals("")) {
					String toolLog=Utils.generateFileNamePostfix(tool+"-"+mt.getName(), "csv");
					Utils.createOutputFile(trafoDirPath+"/"+toolLog, exec.getSuccess());
					verbose= verbose+ " creation of file: "+ toolLog+"\n";
				}
				verbose= verbose+"\n";
			}
									
			//Create the failure
			if(!failure.equals("")) {
				Utils.createOutputFile(trafoDirPath+"/"+failFile, failure);
				verbose= verbose+ "creation of file: "+ failFile+"\n";
			}
			
			verbose= verbose+"\n";
		}	
		
		//Create the global results file
		Utils.createOutputFile(trafoDirPath+"/"+resFile, globalResults);
		verbose= verbose+ "creation of file: "+ resFile+"\n";
		
		//Create the global log file
		Utils.createOutputFile(trafoDirPath+"/"+logFile, verbose);
		verbose= verbose+ "creation of file: "+ logFile+"\n";
				
		return true;		
	}
	
	/**
	 * This function computes the weighted rule coverage
	 * 
	 * @param allRules
	 * @param executedRules
	 * @return
	 */
	public int weightedRuleCoverage(ArrayList<MyRule> allRules, Set<String> executedRules) {
		int score=0;
		
		for(String execRule: executedRules) {
			int kind=Utils.isRuleContained(execRule,allRules);
			if(kind==0) {
				score=score+1;
			}else {
				score=score+kind;
			}
		}		
		return score;
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
