package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.m2m.atl.core.ATLCoreException;
import org.junit.jupiter.api.Test;

import ATLUtils.ModelTransformation;

class ModelTransformationTests {

	@Test
	void test() throws FileNotFoundException, IOException, ATLCoreException {
		ModelTransformation mt= new ModelTransformation("HSM2FSM", "trafosTest/HSM2FSM/HSM2FSM.atl","HSM2FSM", "inMM", 
				"inMMRelativePath", "outMM", "outMMRelativePath");
		
		mt.createAllRulesScores();
		System.out.println(mt.prettyPrint());
	}
	
	@Test
	void testR2ML2RDM() throws FileNotFoundException, IOException, ATLCoreException {
		ModelTransformation mt= new ModelTransformation("R2ML2RDM", "trafosTest/R2ML2RDM/R2ML2RDM.atl",
				"R2ML2RDM", "inMM", "inMMRelativePath", "outMM", "outMMRelativePath");
		
		mt.createAllRulesScores();
		System.out.println(mt.prettyPrint());
	}
	
	@Test
	void creationOfRulesFile() throws FileNotFoundException, IOException, ATLCoreException {
		ModelTransformation mt= new ModelTransformation("HSM2FSM", "trafosTest/HSM2FSM/HSM2FSM.atl","HSM2FSM", "inMM", 
				"inMMRelativePath", "outMM", "outMMRelativePath");
		
		mt.createAllRulesScores();
		mt.createRulesFile();
	}
	
	@Test
	void creationOfRulesFile2() throws FileNotFoundException, IOException, ATLCoreException {
		ModelTransformation mt= new ModelTransformation("R2ML2RDM", "trafosTest/R2ML2RDM/R2ML2RDM.atl",
				"R2ML2RDM", "inMM", "inMMRelativePath", "outMM", "outMMRelativePath");
		
		mt.createAllRulesScores();
		mt.createRulesFile();
	}

}
