

import java.io.File;

import ATLUtils.ExecutionOutput;
import ATLUtils.TransformationsReader;
import ATLauncher.ATLauncher;
import ATLauncher.TransformationsRunner;
import exceptions.FileOrFolderNotFoundException;
import exceptions.MissingParameterException;
import exceptions.UnknownParameterException;

public class Execute {

	/**
	 *  This class executes a model Transformation (module: TRModule, directory: TRDir).
	 *  
	 *  The directory containing input data must be organized as follows:
	 *  
	 *   TRDir
	 *   -----metamodels
	 *   ---------------input
	 *   --------------------inMM.ecore
	 *   ---------------output
	 *   --------------------outMM.ecore
	 *   -----models
	 * 	 ---------------input
	 *   --------------------inM1.xmi
	 *   --------------------inM2.xmi
	 *   ---------------output
	 *   --------------------empty	
	 *   ---------------traces
	 *   -----TRModule.atl
	 *   -----TRModule.emftvm
	 *   -----TRModule.asm
	 *   
	 *   arguments: TRDir TRModule inMMPath inMM outMMPath outMM
	 *   example:   transformations/R2ML2RDM R2ML2RDM R2ML.ecore R2ML RDM.ecore RDM
	 *   
	 *   constant: inMDir: models/input
	 *             outMDir: models/output 
	 * @throws Exception 
	 * @throws UnknownParameterException 
	 */
		
	public static void main(String[] args) throws Exception{
		
		if(args.length == 0) {
			Help.printGeneralHelp();
		}else {
			switch (args[0]) {
				case "help":
				case "h": {
					
					if(args.length == 1) {					
						Help.printGeneralHelp();
					}else {
						switch (args[1]) {
							case "ut":{
								Help.printUTHelp();
							}
							break;
							
							case "cc":{
								Help.printCoverageCalculatorHelp();
							}
							break;
							
							default:{
								try {
									incorrectParameter(args[1]);
								}
								catch (UnknownParameterException e) {
									System.out.println(e.getMessage());
								}
							}							
						}
					}
				}
				break;
				
				case "ut": {
					unitTest(args);
				}
				break;
				
				case "cc": {
					coverageCalculator(args);
				}
				break;
				
				default:{
					try {
						incorrectParameter(args[0]);
					}
					catch (UnknownParameterException e) {
						System.out.println(e.getMessage());
					}
				}
			}
		}
	}
	/**
	 * This method is used to Unit Test one given model transformation.
	 * 
	 * The folder containing the model transformation is the needed parameter.
	 * 
	 * The way how this folder has to be structured is very important. It is described here:
	 * 
	 * https://github.com/ferdjoukh/ATLrunner/blob/master/documentation/tranformations-folder.md
	 * @throws Exception 
	 * 
	 */
	private static void unitTest(String[] args) throws Exception {
		
		if(args.length < 2) {
			try {
				missingParameter("Folder containing 1 Model Transformation");
			} catch (MissingParameterException e) {
				System.out.println(e.getMessage());
			}
		}else {
			try {
				if(isFolderExisiting(args[1])) {
					System.out.println("Unit Test mode is under developement");
					
				}
			} catch (FileOrFolderNotFoundException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	/**
	 * This method is used to launch the Coverage Calculation mode.
	 * 
	 * For that, we need the name of a folder containing many model transformations.
	 * 
	 * The way how this folder has to be structured is very important. It is described here:
	 * 
	 * https://github.com/ferdjoukh/ATLrunner/blob/master/documentation/tranformations-folder.md
	 * @throws Exception 
	 * 
	 */
	private static void coverageCalculator(String[] args) throws Exception {
		
		if(args.length < 2) {
			try {
				missingParameter("Folder containing Model Transformations");
			} catch (MissingParameterException e) {
				System.out.println(e.getMessage());
			}
		}else {
			try {
				if(isFolderExisiting(args[1])) {
					System.out.println("ATL Unit Test Framework");
					System.out.println("");
					System.out.println("Coverage Calculator Mode");
					System.out.println("");
					System.out.println(" Collecting Model Transformations..");
					
					TransformationsReader reader= new TransformationsReader(args[1]);

					if(reader.getModelTransformations().size()!=0) {
						System.out.println(reader.printAllMT());
						
						TransformationsRunner trRun= new TransformationsRunner(reader);
						trRun.runAllTransformations();
						
						System.out.println(trRun.getVerbose());
					}else {
						System.out.println("No model transformation was found ! Please check error messages !");
					}
				}
			} catch (FileOrFolderNotFoundException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	private static boolean isFolderExisiting(String filePath) throws FileOrFolderNotFoundException {
		File folder= new File(filePath);
		if(!folder.exists()) {
			throw new FileOrFolderNotFoundException("Folder",filePath);
		}else if (!folder.isDirectory()) {
			throw new FileOrFolderNotFoundException("Folder",filePath);
		}else{
			return true;
		}
	}
	
	private static void incorrectParameter(String arg) throws UnknownParameterException {
		UnknownParameterException e= new UnknownParameterException(arg);
		throw e;
	}
	
	private static void missingParameter(String arg) throws MissingParameterException {
		MissingParameterException e= new MissingParameterException(arg);
		throw e;
	}

}
