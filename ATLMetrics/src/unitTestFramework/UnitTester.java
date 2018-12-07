package unitTestFramework;

import java.io.File;
import java.io.FileNotFoundException;

import exceptions.FileOrFolderNotFoundException;


/**
 * This class is used to generate Unit Test for a given model transformation
 * 
 * @author Adel Ferdjoukh
 *
 */
public class UnitTester {
	
	String modelTransformationFolder;
	
	public UnitTester(String modelTransformationFolder) throws FileOrFolderNotFoundException {
		
		try {
			if(doesFolderExist(modelTransformationFolder)) {
				this.modelTransformationFolder = modelTransformationFolder;
			}
		} catch (FileOrFolderNotFoundException e) {
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

}
