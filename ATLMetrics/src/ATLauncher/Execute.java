package ATLauncher;

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
	 *              
	 */
	
	// Some constants for quick initialization and testing.
	public static String TRDir = "transformations/HSM2FSM/";
	public static String TRModule= "HSM2FSM";
		
	public static String inMMPath = TRDir + "metamodels/input/HSM.ecore";
	public static String inMM = "HSM";
	public static String outMMPath = TRDir + "metamodels/output/FSM.ecore";
	public static String outMM = "FSM";
	
	public static String inMDir = TRDir+ "models/input/";
	public static String outMDir = TRDir + "models/output/";
	public static String tracesDir= TRDir + "models/traces/";
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			ATLauncher l = new ATLauncher();
			l.registerInputMetamodel(inMMPath);
			l.registerOutputMetamodel(outMMPath);
			l.launch(inMMPath, inMM, inMDir, outMMPath, outMM, outMDir, TRDir, TRModule, tracesDir);
	}

}
