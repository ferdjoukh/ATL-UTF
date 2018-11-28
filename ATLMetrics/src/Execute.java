

import ATLUtils.ExecutionOutput;
import ATLauncher.ATLauncher;
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
	 * @throws UnknownParameterException 
	 */
		
	public static void main(String[] args){
		
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
					unitTest();
				}
				break;
				
				case "cc": {
					coverageCalculator();
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
		
		
//		String globalDir = "trafosTest";
//		String TRname= "HSM2FSM";
//		String TRmodule= "HSM2FSM";
//			
//		String inMMPath = "HSM.ecore";
//		String inMM = "HSM";
//		String outMMPath = "FSM.ecore";
//		String outMM = "FSM";
//		
//		String toolName= "GRIMM";
//		
//		ATLauncher l = new ATLauncher();
//		ExecutionOutput exec= l.launch(globalDir, TRname, TRmodule, toolName, inMMPath, inMM, outMMPath, outMM);
//		
	}
	
	private static void unitTest() {
		System.out.println("UT");
	}
	
	private static void coverageCalculator() {
		System.out.println("CC");
	}
	
	private static void incorrectParameter(String arg) throws UnknownParameterException {
		UnknownParameterException e= new UnknownParameterException(arg);
		throw e;
	}

}
