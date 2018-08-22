

import ATLUtils.ExecutionOutput;
import ATLauncher.ATLauncher;

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
	 */
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String globalDir = "trafosTest";
		String TRname= "HSM2FSM";
		String TRmodule= "HSM2FSM";
			
		String inMMPath = "HSM.ecore";
		String inMM = "HSM";
		String outMMPath = "FSM.ecore";
		String outMM = "FSM";
		
		String toolName= "GRIMM";
		
		ATLauncher l = new ATLauncher();
		ExecutionOutput exec= l.launch(globalDir, TRname, TRmodule, toolName, inMMPath, inMM, outMMPath, outMM);
		
	}

}
