package ATLUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import exceptions.FileOrFolderNotFoundException;
import exceptions.MissingParameterException;

public class TransformationsReader {

	private String trafoDirPath;
	private File trafoDir;
	private String result = "results";
	private ArrayList<String> toolsList=new ArrayList<String>();
	private ArrayList<ModelTransformation> modelTransformations= new ArrayList<ModelTransformation>();
	
	/**
	 * TransformationsReader objects need a path for a directory that 
	 * contains all the information on Model Transformations  
	 * 
	 * @param trafoDirPath: folder where to find all the model transformations and the data for running
	 * @throws MissingParameterException 
	 */
	public TransformationsReader(String trafoDirPath) throws Exception {
		this.trafoDirPath= trafoDirPath;
		this.trafoDir= new File(trafoDirPath);
		
		if(trafoDir.exists()) {
			collectMTandTools();
		}
	}
	
	/**
	 * This method reads the data in folder trafoDirPath and collects all MTs 
	 * and their data (inMM, outMM, rules, infos, etc)
	 * @throws MissingParameterException 
	 */
	private void collectMTandTools() throws Exception{
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
				String[] infos;
				try {
					infos = readMTInfo(trafoDir+"/"+f.getName()+"/"+f.getName()+".infos");
					
					//Create the model transformation with all the information
					ModelTransformation MT= new ModelTransformation(trafoDirPath, f.getName(),
																	atlFilePath, infos[1],
																	infos[2], infos[3], infos[4],
																	infos[5], infos[6]);
					
					//Collect all the tools for that MT
					collectToolsForMTFolder(MT);
					//Add the MT to the list of MTs
					//At this step, we get it ready for running)
					modelTransformations.add(MT);
					
				} catch (Exception e) {
					System.out.println(e.getMessage());
					throw e;
				}			
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
	 * This method read the MT.infos file of a given Model Transformation name.
	 * The returned data is then used to create a ModelTransformation object
	 * 
	 *  MT.infos file is always located in trafoDir/MT/MT.infos
	 *  
	 * @param MTname: model transformation name
	 * @return list of information on this model transformation
	 *   [0=MTname, 1=module, 2=,3=,4=,5=]
	 * @throws MissingParameterException 
	 * @throws FileOrFolderNotFoundException 
	 * @throws IOException 
	 */
	public static String [] readMTInfo(String infoFile) throws MissingParameterException, FileOrFolderNotFoundException, IOException{
		
		File MTinfos= new File(infoFile);
		String [] infos= new String[7];
				
		if(MTinfos.exists()) {
			BufferedReader br = null;
	        String line = "";
	        String cvsSplitBy = "=";
	        int i=0;
	        
	        
	        br = new BufferedReader(new FileReader(MTinfos));
	            
	        while (i<7 && (line = br.readLine()) != null) {
	          	String[] data = line.split(cvsSplitBy);
	            infos[i]=data[1];
	            i++;
	        }	            
	        br.close();
	            
	        if(i < 7) {
	          	throw new MissingParameterException("info missing in "+infoFile+" file");
		    }	        
		}else {
			throw new FileOrFolderNotFoundException("file", infoFile);
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

	public String getResult() {
		return result;
	}
}
