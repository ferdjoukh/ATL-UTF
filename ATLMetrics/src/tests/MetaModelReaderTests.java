package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Ecore.MetaModelReader;

public class MetaModelReaderTests {

	@Test
	public void metricsMapsMM() {
		MetaModelReader reader = new MetaModelReader("meta-models/maps.ecore", "map");
		assertEquals(6, reader.getConcreteClasses().size());
		assertEquals(2, reader.getAbstractClasses().size());
		assertEquals(8, reader.getAllAttributesofMetamodel().size());
		assertEquals(4, reader.getAllTypesOfAttributes().size());
		
	}

}
