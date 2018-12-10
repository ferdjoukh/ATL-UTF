package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.junit.jupiter.api.Test;

import Ecore.MetaModelReader;

public class MetaModelReaderTests {
	
	private int i=0;
	private Hashtable<String,Integer> candidates;
	
	Consumer<EClass> printCandidates = (a) -> {
		System.out.println(a);
	};

	@Test
	public void metricsMapsMM() {
		MetaModelReader reader = new MetaModelReader("meta-models/maps.ecore", "map");
		assertEquals(6, reader.getConcreteClasses().size());
		assertEquals(2, reader.getAbstractClasses().size());
		assertEquals(8, reader.getAllAttributesofMetamodel().size());
		assertEquals(4, reader.getAllTypesOfAttributes().size());
		assertEquals(5, reader.getAllReferencesOfMetamodel().size());
		
		assertEquals(1, reader.containmentTreeDepth());
		assertEquals(1, reader.getMetamodelMaxInheritanceDepth());
		
	}
	
	@Test
	public void metricsEcoreMM() {
		MetaModelReader reader = new MetaModelReader("meta-models/Ecore.ecore", "EPackage");
		
		//Composition tree depth
		assertEquals(4, reader.containmentTreeDepth());
		assertEquals(5, reader.getMetamodelMaxInheritanceDepth());
	}
	
	@Test
	public void containmentTest() {
		MetaModelReader reader = new MetaModelReader("meta-models/containTest.ecore", "A");
		
		//Composition tree depth
		assertEquals(3, reader.containmentTreeDepth());
		assertEquals(5, reader.getAllContainmentsOfMetamodel().size());
		assertEquals(0, reader.getMetamodelMaxInheritanceDepth());
	}
	
	@Test
	public void TargetingC() {
		MetaModelReader reader = new MetaModelReader("meta-models/containTest.ecore", "A");
		
		EClass A = reader.getClassByName("C");
		ArrayList<EReference> containers = reader.allTargetingContainment(A);
		containers.forEach((a)->System.out.println(" "+a.getName()));
		assertEquals(1, containers.size());				
	}
	
	@Test
	public void TargetingEPackage() {
		MetaModelReader reader = new MetaModelReader("meta-models/Ecore.ecore", "EPackage");
		
		EClass A = reader.getClassByName("EPackage");
		ArrayList<EReference> containers = reader.allTargetingContainment(A);
		containers.forEach((a)->System.out.println(" "+a.getName()));
		
		assertEquals(1, containers.size());				
	}
	
	@Test
	public void TargetingEClass() {
		MetaModelReader reader = new MetaModelReader("meta-models/Ecore.ecore", "EPackage");
		
		EClass A = reader.getClassByName("EClass");
		ArrayList<EReference> containers = reader.allTargetingContainment(A);
		containers.forEach((a)->System.out.println(" "+a.getName()));
		
		assertEquals(2, containers.size());				
	}
	
	@Test
	public void TargetingEParameter() {
		MetaModelReader reader = new MetaModelReader("meta-models/Ecore.ecore", "EPackage");
		
		EClass A = reader.getClassByName("EParameter");
		ArrayList<EReference> containers = reader.allTargetingContainment(A);
		containers.forEach((a)->System.out.println(" "+a.getName()));
		
		assertEquals(2, containers.size());				
	}
	
	@Test
	public void reverseTreeTestC() {
		MetaModelReader reader = new MetaModelReader("meta-models/containTest.ecore", "A");
		EClass A = reader.getClassByName("C");
		ArrayList<EClass> containers = reader.reverseContainingTree(A, new ArrayList<EReference>());
		
		containers.forEach((c)->System.out.println(c.getName()));
		
		assertEquals(2, containers.size());				
	}
	
	@Test
	public void reverseTreeTestD() {
		MetaModelReader reader = new MetaModelReader("meta-models/containTest.ecore", "A");
		EClass A = reader.getClassByName("D");
		ArrayList<EClass> containers = reader.reverseContainingTree(A, new ArrayList<EReference>());
		
		containers.forEach((c)->System.out.println(c.getName()));
		
		assertEquals(2, containers.size());				
	}
	
	@Test
	public void reverseTreeTestE() {
		MetaModelReader reader = new MetaModelReader("meta-models/containTest.ecore", "A");
		EClass A = reader.getClassByName("E");
		ArrayList<EClass> containers = reader.reverseContainingTree(A, new ArrayList<EReference>());
		
		containers.forEach((c)->System.out.println(c.getName()));
		
		assertEquals(3, containers.size());				
	}
	
	@Test
	public void reverseTreeTestB() {
		MetaModelReader reader = new MetaModelReader("meta-models/containTest.ecore", "A");
		EClass A = reader.getClassByName("B");
		ArrayList<EClass> containers = reader.reverseContainingTree(A, new ArrayList<EReference>());
		
		containers.forEach((c)->System.out.println(c.getName()));
		
		assertEquals(1, containers.size());				
	}
	
	@Test
	public void reverseTreeTestA() {
		MetaModelReader reader = new MetaModelReader("meta-models/containTest.ecore", "A");
		EClass A = reader.getClassByName("A");
		ArrayList<EClass> containers = reader.reverseContainingTree(A, new ArrayList<EReference>());
		
		containers.forEach((c)->System.out.println(c.getName()));
		
		assertEquals(0, containers.size());				
	}
	
	@Test
	public void reverseTreeEPackage() {
		MetaModelReader reader = new MetaModelReader("meta-models/Ecore.ecore", "EPackage");
		EClass A = reader.getClassByName("EPackage");
		ArrayList<EClass> containers = reader.reverseContainingTree(A,new ArrayList<EReference>());
		containers.forEach((c)->System.out.println(c.getName()));
		
		assertEquals(1, containers.size());				
	}
	
	@Test
	public void reverseTreeEClass() {
		MetaModelReader reader = new MetaModelReader("meta-models/Ecore.ecore", "EPackage");
		EClass A = reader.getClassByName("EClass");
		ArrayList<EClass> containers = reader.reverseContainingTree(A,new ArrayList<EReference>());
		containers.forEach((c)->System.out.println(c.getName()));
		
		assertEquals(2, containers.size());
	}
	
	@Test
	public void reverseTreeEAttribute() {
		MetaModelReader reader = new MetaModelReader("meta-models/Ecore.ecore", "EPackage");
		EClass A = reader.getClassByName("EAttribute");
		ArrayList<EClass> containers = reader.reverseContainingTree(A,new ArrayList<EReference>());
		containers.forEach((c)->System.out.println(c.getName()));
		
		assertEquals(3, containers.size());
	}

	@Test
	public void reverseTreeEOperation() {
		MetaModelReader reader = new MetaModelReader("meta-models/Ecore.ecore", "EPackage");
		EClass A = reader.getClassByName("EOperation");
		ArrayList<EClass> containers = reader.reverseContainingTree(A,new ArrayList<EReference>());
		candidates = reader.generateVecotrOfCandidates(A, containers);
		
		assertEquals(3, containers.size());
		assertEquals(4, candidates.size());
		
		i=0;
		containers.forEach(printCandidates);		
	}
	
	@Test
	public void reverseTreeEAnnotation() {
		MetaModelReader reader = new MetaModelReader("meta-models/Ecore.ecore", "EPackage");
		EClass A = reader.getClassByName("EAnnotation");
		ArrayList<EClass> containers = reader.reverseContainingTree(A,new ArrayList<EReference>());
		containers.forEach((c)->System.out.println(c.getName()));
		assertEquals(1, containers.size());
	}
	
	@Test
	public void reverseTreeEgenericType() {
		MetaModelReader reader = new MetaModelReader("meta-models/Ecore.ecore", "EPackage");
		EClass A = reader.getClassByName("EGenericType");
		ArrayList<EClass> containers = reader.reverseContainingTree(A,new ArrayList<EReference>());
		containers.forEach((c)->System.out.println(c.getName()));
		assertEquals(5, containers.size());
	}	
}
