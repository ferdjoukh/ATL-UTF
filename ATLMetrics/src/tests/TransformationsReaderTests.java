package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;

import ATLUtils.TransformationsReader;

public class TransformationsReaderTests {

	@Test
	public void nonExisitingTrafoDir() throws Exception{
		TransformationsReader tool1= new TransformationsReader("izan");
		boolean exist= tool1.doesTrafoDirExist();
		assertEquals(false, exist, "Trafo folder does not exist");
	}
	
	@Test
	public void missingInfos() throws Exception{
		TransformationsReader tool1= new TransformationsReader("Experiment");
		boolean exist= tool1.doesTrafoDirExist();
		assertEquals(true, exist, "Trafo folder exists");
	}

}
