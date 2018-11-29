package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import ATLUtils.ModelTransformation;

class ModelTransformationTests {

	@Test
	void HSM2FSMmetrics() throws Exception {
		ModelTransformation mt= new ModelTransformation("Experiment","HSM2FSM", 
				"Experiment/HSM2FSM/HSM2FSM.atl","HSM2FSM", 
				"HSM", "HSM.ecore", "StateMachine",
				"outMM", "outMMRelativePath");
		
		assertEquals(7, mt.getMatchedRules().size());
		assertEquals(0, mt.getCalledRules().size());
		assertEquals(0, mt.getLazyMatchedRules().size());
		assertEquals(0, mt.getHelpers().size());
		
		System.out.println(mt.atlMetricsTostring());
		System.out.println(mt.metamodelMetricsToString());
	}
	
}
