package tests;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.m2m.atl.core.ATLCoreException;
import org.junit.jupiter.api.Test;

import ATLUtils.ModelTransformation;
import ATLUtils.TransformationsReader;
import ATLauncher.TransformationsRunner;

class TransformationRunnerTests {

	@Test
	void nonExisitingTrafoDir() throws FileNotFoundException, IOException, ATLCoreException {
		TransformationsReader tool1= new TransformationsReader("izan");
		boolean exist= tool1.doesTrafoDirExist();
		assertEquals(false, exist, "Trafo folder does not exist");
	}
	
	@Test
	void exisitingTrafoDir() throws FileNotFoundException, IOException, ATLCoreException {
		TransformationsReader tool1= new TransformationsReader("trafosTest");
		boolean exist= tool1.doesTrafoDirExist();
		assertEquals(true, exist, "Trafo folder exist");
	}

	@Test
	void trafosTestContains2MLs() throws FileNotFoundException, IOException, ATLCoreException {
		TransformationsReader trRun= new TransformationsReader("trafosTest");
		ArrayList<ModelTransformation> MTsList= trRun.getModelTransformations();
		assertEquals(2, MTsList.size());
	}
	
	@Test
	void trafosTestContains3Tools() throws FileNotFoundException, IOException, ATLCoreException {
		TransformationsReader trRun= new TransformationsReader("trafosTest");
		ArrayList<String> toolsList= trRun.getToolsList();
		assertEquals(3, toolsList.size());
		//System.out.println(trRun.printAllMT());
	}
	
	@Test
	void runTheFolder() throws FileNotFoundException, IOException, ATLCoreException {
		TransformationsReader reader= new TransformationsReader("trafosTest");
		
		System.out.println(reader.printAllMT());
		TransformationsRunner trRun= new TransformationsRunner(reader);
		boolean run=trRun.runAllTransformations();
		System.out.println(trRun.getVerbose());
		assertEquals(true, run);		
	} 
	
	@Test
	void runTheFolder2() throws FileNotFoundException, IOException, ATLCoreException {
		TransformationsReader reader= new TransformationsReader("trafosTest2");
		System.out.println(reader.printAllMT());
		
		TransformationsRunner trRun= new TransformationsRunner(reader);
		boolean run=trRun.runAllTransformations();
		
		System.out.println(trRun.getVerbose());
		assertEquals(true, run);		
	} 
	
	@Test
	void runTheFolder3() throws FileNotFoundException, IOException, ATLCoreException {
		TransformationsReader reader= new TransformationsReader("newMTS");
		System.out.println(reader.printAllMT());
		
		TransformationsRunner trRun= new TransformationsRunner(reader);
		boolean run=trRun.runAllTransformations();
		
		System.out.println(trRun.getVerbose());
		assertEquals(true, run);		
	} 	
	
	@Test
	void runExperimentFolderTest() throws FileNotFoundException, IOException, ATLCoreException {
		TransformationsReader reader= new TransformationsReader("Experiment");
		System.out.println(reader.printAllMT());
		
		TransformationsRunner trRun= new TransformationsRunner(reader);
		boolean run=trRun.runAllTransformations();
		
		System.out.println(trRun.getVerbose());
		assertEquals(true, run);		
	} 	
}

