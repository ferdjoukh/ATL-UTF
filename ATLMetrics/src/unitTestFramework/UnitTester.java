package unitTestFramework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import ATLUtils.ModelTransformation;
import ATLUtils.TransformationsReader;
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
	
	private boolean doesFolderExist(String folder) throws FileOrFolderNotFoundException {
		File file = new File(folder);
		
		if(file.isDirectory()) {
			return true;
		}else {
			throw new FileOrFolderNotFoundException("folder",folder);			
		}
	}
	
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
		
			//Get all the infos from MT.infos file
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

	public String getModelTransformationFolder() {
		return modelTransformationFolder;
	}

	public ModelTransformation getTransformation() {
		return transformation;
	}
}
