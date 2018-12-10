package unitTestFramework;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.m2m.atl.common.ATL.MatchedRule;

import ATLUtils.ModelTransformation;

import ATLUtils.TransformationsReader;
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
		
			System.out.println(rule.getName());
			
			rule.getInPattern().getElements().forEach((a)->generateFileForGivenClass(a.getType().getName()));
						
			System.out.println("");
		}
	}
	
	private void generateFileForGivenClass(String className) {
		
		MetaModelReader reader = transformation.getMetamodelReader();
		EClass classe = reader.getClassByName(className);
		
		ArrayList<EClass> containingTree = reader.reverseContainingTree(classe, new ArrayList<EReference>());
		
		ArrayList<String> candidates = reader.generateVecotrOfCandidates(classe, containingTree);
		
		
		System.out.println(candidates);
		
//		System.out.print(className+" << [ ");
//		containingTree.forEach((a)-> System.out.print(a.getName()+" "));
//		System.out.println("]");
		
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
