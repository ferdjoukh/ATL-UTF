package unitTestFramework;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.m2m.atl.common.ATL.InPatternElement;
import org.eclipse.m2m.atl.common.ATL.MatchedRule;

import ATLUtils.ModelTransformation;

import ATLUtils.TransformationsReader;
import ATLUtils.Utils;
import Ecore.MetaModelReader;
import exceptions.FileOrFolderNotFoundException;
import exceptions.MissingParameterException;


/**
 * This class is used to generate Unit Test for a given model transformation
 * 
 * @author Adel Ferdjoukh
 *
 */
public class UnitTester {
	
	String modelTransformationFolder;
	String transformationName;
	ModelTransformation transformation;
	
	public UnitTester(String modelTransformationFolder) throws Exception {
		
		int lastSlash = modelTransformationFolder.lastIndexOf("/");
		
		String name = modelTransformationFolder.substring(modelTransformationFolder.lastIndexOf("/")+1);
		String folder = ".";
		
		if(lastSlash != -1) {
			folder = modelTransformationFolder.substring(0, modelTransformationFolder.lastIndexOf("/"));
		}
		
		try {
			if(doesFolderExist(modelTransformationFolder)) {
				this.modelTransformationFolder = folder;
				this.transformationName = name;
				this.transformation = createModelTransformation();				
			}
		} catch (FileOrFolderNotFoundException e) {
			System.out.println(e.getMessage());
			throw e;
		} catch (MissingParameterException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}
	
	/**
	 * This method checks that the input folder exists.
	 * If the folder does not exist, then an Exception is thrown
	 * 
	 * @param folder
	 * @return
	 * @throws FileOrFolderNotFoundException
	 */
	private boolean doesFolderExist(String folder) throws FileOrFolderNotFoundException {
		File file = new File(folder);
		
		if(file.isDirectory()) {
			return true;
		}else {
			throw new FileOrFolderNotFoundException("folder",folder);			
		}
	}
	
	/**
	 * This method create a ModelTransformation object from the given folder
	 * 
	 * @return
	 * @throws Exception
	 */
	private ModelTransformation createModelTransformation() throws Exception {
		
		File dir = new File(modelTransformationFolder+"/"+transformationName);
		
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
		
		String atlFilePath="";
		File [] subfiles= dir.listFiles(atlFilter);
		if(subfiles.length>=1) {
			atlFilePath=subfiles[0].getPath();
		
			//read transformation infos from .infos file
			String[] infos;
			try {
				infos = TransformationsReader.readMTInfo(modelTransformationFolder+"/"+transformationName+"/"+transformationName+".infos");
				
				//Create the model transformation with all the information
				ModelTransformation transformation= new ModelTransformation(modelTransformationFolder, dir.getName(),
																atlFilePath, infos[1],
																infos[2], infos[3], infos[4],
																infos[5], infos[6]);				
				return transformation;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				throw e;
			}	
		}else {
			throw new FileOrFolderNotFoundException("file", dir.getName()+".atl");
		}
	}
	
	
	public void generateGrimmConfigParamsFiles() {
		ArrayList<MatchedRule> matchedRules =  transformation.getMatchedRules();
		
		for(MatchedRule rule: matchedRules) {
			
			Hashtable<String,Integer> candidates = new Hashtable<String, Integer>();
			
			System.out.println(rule.getName());
			
			for (InPatternElement element : rule.getInPattern().getElements()) {
				candidates =  merge(candidates, generateFileForGivenClass(element.getType().getName()));
			}
			
			candidates2Files(candidates, rule.getName());
			
			//I have here my list of candidates
			System.out.println(candidates);
			System.out.println("");
			System.out.println("");
		}
	}
	
	private Hashtable<String,Integer> generateFileForGivenClass(String className) {
		
		MetaModelReader reader = transformation.getMetamodelReader();
		EClass classe = reader.getClassByName(className);
		ArrayList<EClass> containingTree = reader.reverseContainingTree(classe, new ArrayList<EReference>());
		Hashtable<String,Integer> candidates = reader.generateVecotrOfCandidates(classe, containingTree);
		
		System.out.println(candidates);
		
		return candidates;
	}
	
	private Hashtable<String,Integer> merge(Hashtable<String,Integer> table1, Hashtable<String,Integer> table2){
		Hashtable<String,Integer> result = new Hashtable<String, Integer>();
		
		for(Map.Entry<String, Integer> entry : table2.entrySet()){
		    if(table1.containsKey(entry.getKey())){
		    	result.put(entry.getKey(), table1.get(entry.getKey())+table2.get(entry.getKey()));		    	
		    }else{
		    	result.put(entry.getKey(), entry.getValue());
		    }
		}
		
		for(Map.Entry<String, Integer> entry : table1.entrySet()){
		    if(table2.containsKey(entry.getKey())){
		    	result.put(entry.getKey(), table1.get(entry.getKey())+table2.get(entry.getKey()));		    	
		    }else{
		    	result.put(entry.getKey(), entry.getValue());
		    }
		}
		
		return result;
	}
	
	private void candidates2Files(Hashtable<String, Integer> candidates, String rule) {
		
		String grimmFolder = transformation.getRootFolder()+"/"+transformationName+"/grimm";
		new File(grimmFolder).mkdir();
		
		String grimmFile = grimmFolder + "/" + rule + ".grimm";  
		Utils.createOutputFile(grimmFile,grimmFileContent(candidates));
		
		String paramsFile = grimmFolder + "/" + rule + ".params";  
		Utils.createOutputFile(paramsFile,paramsFileContent(grimmFile));	
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		executeGrimm(paramsFile);
	}
	
	private String grimmFileContent(Hashtable<String, Integer> candidates) {
		
		String content="";
		
		for(EClass classe : transformation.getMetamodelReader().getConcreteClasses()) {
			
			if(candidates.containsKey(classe.getName())) {
				content = content + classe.getName() +"="+ candidates.get(classe.getName()) +"\n";
			}else {
				content = content + classe.getName() +"=0\n";
			}
			
		}
		
		content = content + "RefsBound=3\n";
		
		return content;
	}
	
	private String paramsFileContent(String grimmFilePath) {
		
		String mm =   transformation.getRootFolder()+"/"+transformationName+"/metamodels/input/"+transformation.getInMMRelativePath();
		String root = transformation.getRootClass();
		
		return "+meta-model ="+mm+"\n" + 
		"+rootClass ="+root+"\n" + 
		"configuration file ="+grimmFilePath+"\n" + 
		"number of solutions =2\n" + 
		"output format =xmi\n" + 
		"CSP solver =abscon";
	} 
	
	private void executeGrimm(String paramsFilePath) {
		
		String cmd = "java -jar grimm.jar g " + paramsFilePath;

		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);	
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	////////////////////////////////////////////////
	//  Getters
	////////////////////////////////////////////////
	public String getModelTransformationFolder() {
		return modelTransformationFolder;
	}

	public ModelTransformation getTransformation() {
		return transformation;
	}

	public String getTransformationName() {
		return transformationName;
	}
}
