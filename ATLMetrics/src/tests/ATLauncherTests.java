package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ATLUtils.ExecutionOutput;
import ATLauncher.ATLauncher;

class ATLauncherTests {

	@Test
	void Execute_HSM2FSM_tests() {
		String globalDir = "trafosTest";
		String TRname= "HSM2FSM";
		String TRmodule= "HSM2FSM";
		String toolName= "GRIMM";
		String inMMPath = "HSM.ecore";
		String inMM = "HSM";
		String outMMPath = "FSM.ecore";
		String outMM = "FSM";
		
		ATLauncher l = new ATLauncher();
		ExecutionOutput exec= l.launch(globalDir, TRname, TRmodule, toolName, inMMPath, inMM, outMMPath, outMM);
		
		System.out.println(exec.getLog());
		assertEquals(exec.getTransformed(), exec.getModels());
		assertEquals(4, exec.getTransformed());
	}
	
	@Test
	void ExecuteRDMLTests() {
		String globalDir = "trafosTest";
		String TRname= "R2ML2RDM";
		String TRmodule= "R2ML2RDM";
		String toolName= "PRAMANA";
		String inMMPath = "R2ML.ecore";
		String inMM = "R2ML";
		String outMMPath = "RDM.ecore";
		String outMM = "RDM";
		
		ATLauncher l = new ATLauncher();
		ExecutionOutput exec= l.launch(globalDir, TRname, TRmodule, toolName, inMMPath, inMM, outMMPath, outMM);
		
		System.out.println(exec.getLog());
		assertEquals(2, exec.getModels());
		assertEquals(0, exec.getTransformed());
	}
}
