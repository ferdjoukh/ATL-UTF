package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ATLUtils.ExecutionOutput;
import ATLauncher.ATLauncher;

class ATLauncherTests {

	@Test
	void Execute() {
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
		
		System.out.println(exec.getSuccess());
		assertEquals(exec.getTransformed(), exec.getModels());
		assertEquals(4, exec.getTransformed());
	}
}
