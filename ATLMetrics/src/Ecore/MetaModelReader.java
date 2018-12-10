package Ecore;

import java.util.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.*;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * This class is used to read information of Ecore Metamodels
 * and compute some metrics 
 * 
 * @author Adel Ferdjoukh
 *
 */
public class MetaModelReader {
	
	private String metamodelFilePath;
	private String rootClassName;
	private Resource resource;
	private EPackage BasePackage;
	
	public void loadRootPackage(String metamodel,String rootClass) {
		Resource.Factory.Registry reg=Resource.Factory.Registry.INSTANCE;
		Map<String,Object> m = reg.getExtensionToFactoryMap();
		m.put("ecore",new XMIResourceFactoryImpl());
		ResourceSet resourceSet=new ResourceSetImpl();
		URI fileURI=URI.createFileURI(metamodel);
		Resource resource=resourceSet.getResource(fileURI,true);
		EPackage rootpackage= (EPackage)  resource.getContents().get(0);
		
		this.metamodelFilePath = metamodel;
		this.rootClassName=rootClass;
		this.resource=resource;
		this.BasePackage= rootpackage;		
	}
	
	public MetaModelReader(String metamodel,String rootClass){
		loadRootPackage(metamodel, rootClass);
	}
	
	///////////////////////////////////////////////////
	//   Getters
	///////////////////////////////////////////////////
	public String getMetamodel(){
		return metamodelFilePath;
	}

	public String getRootClass() {
		return rootClassName;
	}
	
	public Resource getModelResource(){
		return this.resource;
	}
		
	public EPackage getBasePackage() {
		return BasePackage;
	}
	
	///////////////////////////////////////////////////////
	//   Classes
	///////////////////////////////////////////////////////
	public List<EClass> getConcreteClasses(){
		
		ArrayList<EClass> cls= new ArrayList<EClass>();
		for( EClassifier cf :BasePackage.getEClassifiers()){
			
			if (cf instanceof EClass){		
				if (!((EClass) cf).isAbstract())
					cls.add((EClass) cf);						
			}
		}
		return cls;
	}
	
	public List<EClass> getAbstractClasses(){
		
		ArrayList<EClass> cls= new ArrayList<EClass>();
		for( EClassifier cf :BasePackage.getEClassifiers()){
			
			if (cf instanceof EClass){
					if (((EClass) cf).isAbstract())
						cls.add((EClass) cf);										
			}
		}
		return cls;
	}
	
	public ArrayList<EClass> getAllClasses() {
		ArrayList<EClass> allClasses = (ArrayList<EClass>) getConcreteClasses();
		ArrayList<EClass> abstratClasses = (ArrayList<EClass>) getAbstractClasses();
		allClasses.addAll(abstratClasses);
		
		return allClasses;
	}
	
	public EClass getClassByName(String className) {
		
		for(EClass eclass: getAllClasses()) {
			if (eclass.getName().equals(className)) {
				return eclass;
			}
		}
		return null;
	}
	
	///////////////////////////////////////////////////////
	//     Attributes
	///////////////////////////////////////////////////////
	public List<EAttribute> getAllAttributesFromClass(EClass c)
	{
		ArrayList<EAttribute> attributes= new ArrayList<EAttribute>();
		
		for(EAttribute a : c.getEAllAttributes()) {
			if(a.isChangeable()) {
				attributes.add(a);
			}
		}
		return attributes;
	}
	
	public List<EAttribute> getAllAttributesofMetamodel(){
		
		ArrayList<EAttribute> attributes= new ArrayList<EAttribute>();
		
		for(EClass eclass: getConcreteClasses()) {
			attributes.addAll(eclass.getEAttributes());
		}
		
		for(EClass eclass: getAbstractClasses()) {
			attributes.addAll(eclass.getEAttributes());
		}
		
		return attributes;
	}
	
	public List<String> getAllTypesOfAttributes(){
		
		ArrayList<String> types = new ArrayList<String>();
		
		ArrayList<EAttribute> attributes = (ArrayList<EAttribute>) getAllAttributesofMetamodel();
		
		for(EAttribute attr: attributes) {
			
			String typeName;
			if(attr.getEType().eClass().getName().equals("EEnum")) {
				typeName = "EEnum";
			}else if (attr.getEType().eClass().getName().equals("EEnum")) {
				typeName = attr.getEType().getName(); 
			}else {
				typeName = attr.getEType().getName();
			}
			
			if(!types.contains(typeName)) {
				types.add(typeName);
			}
		}
		
		return types;
	}
	
	//////////////////////////////////////////////////////
	// Inheritance
	//////////////////////////////////////////////////////	
	public List<EClass> getAllSubtypes(EClass c){
		
		ArrayList<EClass> allClasses= new ArrayList<EClass>();
		for(EClass subClass: getConcreteClasses()){
			
			if(subClass.getEAllSuperTypes().contains(c))
			allClasses.add(subClass);
		}
		return allClasses;
	}
	
	public List<EClass> getConcreteSubTypes(EClass c) {

		ArrayList<EClass> cls= new ArrayList<EClass>();
		for(EClass cc: getConcreteClasses()){
			if (!cc.isAbstract()){
				if(cc.getEAllSuperTypes().contains(c))	
					cls.add(cc);
			}
		}
		return cls;
	}
	
	public int getMetamodelMaxInheritanceDepth() {
		
		int [] allDepth = new int[getAllClasses().size()];
		int i = 0;
		
		for(EClass eclass: getAllClasses()) {
			allDepth[i] = eclass.getEAllSuperTypes().size(); 
			i++;
		}
		
		return dichotomicMax(allDepth, 0, allDepth.length-1);
		
	}

	
	//////////////////////////////////////////////////////
	// References
	//////////////////////////////////////////////////////
	/**
	 * This method collects all the references of a given class.
	 * It includes also the references of superClasses of c.
	 * 
	 * Unchangeable and containments are not considered
	 * 
	 * @param c
	 * @return
	 */
	public List<EReference> getAllReferencesFromClass(EClass c){
		ArrayList<EReference> references= new ArrayList<EReference>();
		for (EReference r: c.getEAllReferences()){
			if (r.isChangeable() && !r.isContainment()){
				references.add(r);
			}
		}
		return references;
    }
	
	/**
	 * This method collects the containment relation of a given class
	 * 
	 * unchangeable references are not considered
	 * 
	 * @param c
	 * @return
	 */
	public List<EReference> getAllContainmentFromClass(EClass c){
		ArrayList<EReference> containments= new ArrayList<EReference>();
		for (EReference r: c.getEAllReferences()){
			if (r.isChangeable() && r.isContainment()){
				containments.add(r);
			}
		}
		return containments;
	}

	public List<EReference> getContainmentFromClass(EClass c){
		ArrayList<EReference> containments= new ArrayList<EReference>();
		for (EReference r: c.getEReferences()){
			if (r.isChangeable() && r.isContainment()){
				containments.add(r);
			}
		}
		return containments;
	}
	
	public ArrayList<EReference> allTargetingContainment(EClass eclass){
		ArrayList<EReference> containments= new ArrayList<EReference>();
		
		for(EReference containment: getAllContainmentsOfMetamodel()) {
			EClass target = containment.getEReferenceType();
			EClass container = containment.getEContainingClass();
			
			if(!eclass.equals(container)) {
				if(target.equals(eclass) || eclass.getEAllSuperTypes().contains(target)) {
					containments.add(containment);
				}	
			}
		}
		return containments;
	}
	
	public List<EReference> getAllContainmentsOfMetamodel() {
		ArrayList<EReference> containments= new ArrayList<EReference>();
		for(EClass eclass: getAllClasses()) {
			ArrayList<EReference> currentContainments = (ArrayList<EReference>) getContainmentFromClass(eclass);
			for(EReference ref: currentContainments) {
				if(!containments.contains(ref)) {
					containments.add(ref);
				}
			}
		}
		return containments;
	}
	
	/**
	 * This method is collecting the references of a class.
	 * If a reference has not an EOpposite then it is added.
	 * If it has then the reference with the smallest bound is added
	 * 
	 * @param c
	 * @return
	 */
	public List<EReference> getAllReferencesFromClasswithOpposite(EClass c)
	{
		ArrayList<EReference> references= new ArrayList<EReference>();
		for (EReference ref: getAllReferencesFromClass(c)){
			
			if(ref.getEOpposite()==null){
				references.add(ref);
			}
			else{
				
				int bound=ref.getUpperBound();
				int oppositeBound = ref.getEOpposite().getUpperBound();
				
				if (bound == -1) {
					bound=100;			
				}
				
				if (oppositeBound == -1) { 
					oppositeBound=100;
				}
				
				if(bound < oppositeBound){
					references.add(ref);
				}
				else if(oppositeBound==bound)
				{
					if(ref.getName().codePointCount(0, ref.getName().length()) < ref.getEOpposite().getName().codePointCount(0, ref.getEOpposite().getName().length()))
					{
						references.add(ref);
					}
				}
			}
		}
		return references;
    }
	
	public List<EReference> getAllReferencesOfMetamodel(){
		ArrayList<EReference> references = new ArrayList<EReference>();
		ArrayList<EClass> allClasses = getAllClasses();
		
		for(EClass eclass: allClasses) {
			for(EReference ref: getAllReferencesFromClass(eclass)) {
				if(!references.contains(ref)) {
					references.add(ref);
				}
			}
			for(EReference ref: getAllContainmentFromClass(eclass)) {
				if(!references.contains(ref)) {
					references.add(ref);
				}
			}
		}
		
		return references;
	}	
	
	////////////////////////////////////////////
	// Composition tree depth
	///////////////////////////////////////////
	private int containmentTreeOfClass(EClass eclass) {
		
		ArrayList<EReference> containments = (ArrayList<EReference>) getAllContainmentFromClass(eclass);
		
		if(containments.size() == 0) {
			return 0;
		}else {
			int [] trees = new int[containments.size()];
			int i=0;
			for(EReference containment: containments) {
				if(containment.getEType().getName().equals(eclass.getName())) {
					trees[i] = 0;
				}else {
					trees[i] = containmentTreeOfClass((EClass)containment.getEType());
				}
				i++;
			}
			return 1 + dichotomicMax(trees,0, trees.length-1);
		}		
	}
	
	private int dichotomicMax(int[] trees, int begin, int end) {
		
		if(begin == end) {
			return trees[begin];
		}else {
			return max(dichotomicMax(trees, begin, (begin+end)/2),dichotomicMax(trees, (begin+end)/2 + 1, end));
		}			
	}
	
	private int max(int a, int b) {
		if (a>b) {return a;}
		else return b;
	}

	public int containmentTreeDepth() {
		ArrayList<EClass> allClasses = (ArrayList<EClass>) getConcreteClasses();
		int [] alldepths = new int[allClasses.size()];
		int i = 0;
		
		for(EClass eclass : allClasses) {
			alldepths[i] = containmentTreeOfClass(eclass);
			i++;
		}
		
		return dichotomicMax(alldepths, 0, alldepths.length-1);		
	}
	
	/**
	 * This method returns the reverse containing tree for a given class.
	 * For the first call given empty ArrayList<EReference>
	 *
	 * @param eclass
	 * @param visitedContainments
	 * @return
	 */
	public ArrayList<EClass> reverseContainingTree(EClass eclass, ArrayList<EReference> visitedContainments){
		ArrayList<EClass> containers = new ArrayList<EClass>();
		
		for(EReference targeting: allTargetingContainment(eclass)) {
			
			if(!visitedContainments.contains(targeting)) {
				EClass container = (EClass) targeting.eContainer();
				if(!container.isAbstract() && !containers.contains(container)) {
					containers.add(container);
				}
				visitedContainments.add(targeting);
				
				for (EClass container1: reverseContainingTree(container, visitedContainments)) {
					if(!containers.contains(container1)) {
						containers.add(container1);
					}
				}							
			}						
		}
		return containers;
	}	
	
	public static int randomInt(int min, int max) {
		Random rand= new Random();
		return rand.nextInt(max-min) + min ;
	}
	
//	public int [] generateVecotrOfCandidates(EClass eclass, ArrayList<EClass> containmentTree){
//		int [] results= new int[1+containmentTree.size()];
//		results[0] = randomInt(1, 3);
//		
//		for(int i=0;i<containmentTree.size();i++) {
//			results[i+1] = randomInt(1, 3); 
//		}
//		
//		return results;
//	}
	
	public Hashtable<String,Integer> generateVecotrOfCandidates(EClass eclass, ArrayList<EClass> containmentTree){
		Hashtable<String,Integer> results= new Hashtable<String,Integer>();
		
		//Add a number of instances for current class
		if(!eclass.isAbstract()) {
			if(eclass.getName().equals(rootClassName)) {
				results.put(eclass.getName(),1);
			}else {
				results.put(eclass.getName(),randomInt(1, 3));
			}
		}else {
			
			for(EClass c: getConcreteSubTypes(eclass)) {
				results.put(c.getName(),randomInt(1, 3));
			}
		}
		
		//Add a number of instances for all the classes of the containing tree
		for(EClass c : containmentTree) {
			
			if(!c.isAbstract()) {
				if(c.getName().equals(rootClassName)) {
					results.put(c.getName(),1);
				}else {
					results.put(c.getName(),randomInt(1, 3));
				}
			}else {
				for(EClass subclass: getConcreteSubTypes(c)) {
					results.put(subclass.getName(),randomInt(1, 3));
				}	
			}
		}
		
		return results;
	}

}
