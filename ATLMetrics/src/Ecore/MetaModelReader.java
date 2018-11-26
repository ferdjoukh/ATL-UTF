package Ecore;

import java.lang.reflect.Array;
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
	
	public EPackage getModelPackage(){
		return this.BasePackage;
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
	//
	//////////////////////////////////////////////////////
	public List<EClass> getAllSubtypes(EClass c)
	{
		ArrayList<EClass> allClasses= new ArrayList<EClass>();
		for(EClass subClass: getConcreteClasses()){
			
			if(subClass.getEAllSuperTypes().contains(c))
			allClasses.add(subClass);
		}
		return allClasses;
	}
	
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
	
	public List<EClass> getConcreteSubTypes(EClass c) 
	{

			ArrayList<EClass> cls= new ArrayList<EClass>();
			for(EClass cc: getConcreteClasses())
			{
				if (!cc.isAbstract())
				{
				if(cc.getEAllSuperTypes().contains(c))	
				cls.add(cc);
				}
			}
			return cls;
	}

	public EPackage getBasePackage() {
		return BasePackage;
	}		
}
