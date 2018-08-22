package tests;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import ATLUtils.ModelTransformation;
import running.*;

class TransformationRunnerTests {

	@Test
	void nonExisitingTrafoDir() {
		TransformationRunner tool1= new TransformationRunner("izan");
		boolean exist= tool1.doesTrafoDirExist();
		assertEquals(false, exist, "Trafo folder does not exist");
	}
	
	@Test
	void exisitingTrafoDir() {
		TransformationRunner tool1= new TransformationRunner("trafosTest");
		boolean exist= tool1.doesTrafoDirExist();
		assertEquals(true, exist, "Trafo folder exist");
	}

	@Test
	void trafosTestContains2MLs() {
		TransformationRunner trRun= new TransformationRunner("trafosTest");
		ArrayList<ModelTransformation> MTsList= trRun.getModelTransformations();
		assertEquals(2, MTsList.size());
	}
	
	@Test
	void trafosTestContains3Tools() {
		TransformationRunner trRun= new TransformationRunner("trafosTest");
		ArrayList<String> toolsList= trRun.getToolsList();
		assertEquals(3, toolsList.size());
		//System.out.println(trRun.printAllMT());
	}
	
	@Test
	void runTheFolder() {
		TransformationRunner trRun= new TransformationRunner("trafosTest");
		boolean run=trRun.runAllTransformations();
		System.out.println(trRun.getVerbose());
		assertEquals(true, run);
	} 
	
	@Test
	void generateAFileName() {
		TransformationRunner trRun= new TransformationRunner("trafosTest");
	}
}

