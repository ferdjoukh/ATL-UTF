package ATLUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.m2m.atl.core.ATLCoreException;

public class TransformationsReader {

	private String trafoDirPath;
	private File trafoDir;
	private ArrayList<String> toolsList=new ArrayList<String>();
	private ArrayList<ModelTransformation> modelTransformations= new ArrayList<ModelTransformation>();
	
	/**
	 * TransformationsReader objects need a path for a directory that 
	 * contains all the information on Model Transformations  
	 * 
	 * @param trafoDirPath: folder where to find all the model transformations and the data for running
	 */
	public TransformationsReader(String trafoDirPath) {
		this.trafoDirPath= trafoDirPath;
		this.trafoDir= new File(trafoDirPath);
		
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
	
	/**
	 * This method prints a detailed overview of all the MTs found in trafoDir
	 *  
	 * @return pretty print of Model Transformations data
	 */
	public String printAllMT() {
		String res="MODEL TRANSFORMATIONS\n";
		
		for(ModelTransformation mt: modelTransformations) {
			res= res+"\t"+mt.prettyPrint();
		}		
		return res;
	}

	public ArrayList<ModelTransformation> getModelTransformations() {
		return modelTransformations;
	}

	public ArrayList<String> getToolsList() {
		return toolsList;
	}

	public String getTrafoDirPath() {
		return trafoDirPath;
	}

	public File getTrafoDir() {
		return trafoDir;
	}
	
	
	
}