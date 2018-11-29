package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import ATLUtils.ModelTransformation;
import ATLUtils.TransformationsReader;
import ATLauncher.TransformationsRunner;

class TransformationRunnerTests {

	@Test
	void nonExisitingTrafoDir() throws Exception {
		TransformationsReader tool1= new TransformationsReader("izan");
		boolean exist= tool1.doesTrafoDirExist();
		assertEquals(false, exist, "Trafo folder does not exist");
	}
	
	@Test
	void exisitingTrafoDir() throws Exception {
		TransformationsReader tool1= new TransformationsReader("trafosTest");
		boolean exist= tool1.doesTrafoDirExist();
		assertEquals(true, exist, "Trafo folder exist");
	}

	@Test
	void trafosTestContains2MLs() throws Exception {
		TransformationsReader trRun= new TransformationsReader("trafosTest");
		ArrayList<ModelTransformation> MTsList= trRun.getModelTransformations();
		assertEquals(2, MTsList.size());
	}
	
	@Test
	void trafosTestContains3Tools() throws Exception {
		TransformationsReader trRun= new TransformationsReader("trafosTest");
		ArrayList<String> toolsList= trRun.getToolsList();
		assertEquals(3, toolsList.size());
		//System.out.println(trRun.printAllMT());
	}
	
	@Test
	void runTheFolder() throws Exception {
		TransformationsReader reader= new TransformationsReader("trafosTest");
		
		System.out.println(reader.printAllMT());
		TransformationsRunner trRun= new TransformationsRunner(reader);
		boolean run=trRun.runAllTransformations();
		System.out.println(trRun.getVerbose());
		assertEquals(true, run);		
	} 
	
	@Test
	void runTheFolder2() throws Exception {
		TransformationsReader reader= new TransformationsReader("trafosTest2");
		System.out.println(reader.printAllMT());
		
		TransformationsRunner trRun= new TransformationsRunner(reader);
		boolean run=trRun.runAllTransformations();
		
		System.out.println(trRun.getVerbose());
		assertEquals(true, run);		
	} 
	
	@Test
	void runTheFolder3() throws Exception {
		TransformationsReader reader= new TransformationsReader("newMTS");
		System.out.println(reader.printAllMT());
		
		TransformationsRunner trRun= new TransformationsRunner(reader);
		boolean run=trRun.runAllTransformations();
		
		System.out.println(trRun.getVerbose());
		assertEquals(true, run);		
	} 	
	
	@Test
	void runExperimentFolderTest() throws Exception {
		TransformationsReader reader= new TransformationsReader("Experiment");
		System.out.println(reader.printAllMT());
		
		TransformationsRunner trRun= new TransformationsRunner(reader);
		boolean run=trRun.runAllTransformations();
		
		System.out.println(trRun.getVerbose());
		assertEquals(true, run);		
	} 	
}

