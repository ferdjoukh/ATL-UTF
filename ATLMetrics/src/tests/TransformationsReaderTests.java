package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import exceptions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.eclipse.m2m.atl.core.ATLCoreException;

import ATLUtils.TransformationsReader;

public class TransformationsReaderTests {

	@Test
	public void nonExisitingTrafoDir() throws Exception{
		TransformationsReader tool1= new TransformationsReader("izan");
		boolean exist= tool1.doesTrafoDirExist();
		assertEquals(false, exist, "Trafo folder does not exist");
	}
	
	@Test(expected = MissingParameterException.class)
	public void missingInfos() throws Exception{
		TransformationsReader tool1= new TransformationsReader("Experiment");
		boolean exist= tool1.doesTrafoDirExist();
		assertEquals(true, exist, "Trafo folder does not exist");
	}

}
