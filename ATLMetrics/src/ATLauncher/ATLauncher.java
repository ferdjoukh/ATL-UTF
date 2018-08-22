package ATLauncher;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.EmftvmFactory;
import org.eclipse.m2m.atl.emftvm.ExecEnv;
import org.eclipse.m2m.atl.emftvm.Metamodel;
import org.eclipse.m2m.atl.emftvm.Model;
import org.eclipse.m2m.atl.emftvm.impl.resource.EMFTVMResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.trace.TraceLinkSet;
import org.eclipse.m2m.atl.emftvm.util.DefaultModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.ModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.TimingData;

import ATLUtils.ExecutionOutput;

public class ATLauncher {
	
	// The input and output metamodel nsURIs are resolved using lazy registration of metamodels, see below.
	private String inputMetamodelNsURI;
	private String outputMetamodelNsURI;
		
	//Main transformation launch method
	public ExecutionOutput launch(
			String globalDir,
			String TRname, String TRmodule, String toolName, 
			String inMetamodelFile, String inMetamodelName,
			String outMetamodelFile, String outMetamodelName){
		
		String log="";
		String success="";
		String fail="";
		String summary="";
		int nbSuccess=0;
		
		String transformationDir=globalDir+"/"+TRname+"/";
		String inMetamodelPath= transformationDir+"metamodels/input/"+ inMetamodelFile;
		String outMetamodelPath= transformationDir+"metamodels/output/"+ outMetamodelFile;
		String inModelDir=transformationDir+"models/input/"+toolName;
		String outModelDir=transformationDir+"models/output/"+toolName;
		String tracesDir=transformationDir+"models/traces/"+toolName;
		
		registerNamespaces();
		registerInputMetamodel(inMetamodelPath);
		registerOutputMetamodel(outMetamodelPath); 
		
		File inputMetamodel = new File(inMetamodelPath);
		File outputMetamodel = new File(outMetamodelPath);
		URI inputMetamodelUri = URI
				.createFileURI(inputMetamodel.getAbsolutePath());
		URI outputMetamodelUri = URI
				.createFileURI(outputMetamodel.getAbsolutePath());
		
		ExecEnv env = EmftvmFactory.eINSTANCE.createExecEnv();
		ResourceSet rs = new ResourceSetImpl();
		
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("emftvm", new EMFTVMResourceFactoryImpl());
		
		
		Metamodel inMetamodel = EmftvmFactory.eINSTANCE.createMetamodel();
		inMetamodel.setResource(rs.getResource(URI.createURI(inputMetamodelNsURI), true));
		env.registerMetaModel(inMetamodelName, inMetamodel);
		
		Metamodel outMetamodel = EmftvmFactory.eINSTANCE.createMetamodel();
		outMetamodel.setResource(rs.getResource(URI.createURI(outputMetamodelNsURI), true));
		env.registerMetaModel(outMetamodelName, outMetamodel);
		
		
		File inModelFolder = new File(inModelDir);
		File[] inputModelFiles = inModelFolder.listFiles();
		
		int totalRules= 0;
		final Set<String> totalExecutedRules = new HashSet<>();
				
		int nbInModels= inputModelFiles.length;
				
		for(int i=0; i<inputModelFiles.length;i++) {
			
			String inModelPath=inputModelFiles[i].getPath();
			String outModelPath=outModelDir+"out-"+inputModelFiles[i].getName();
			String traceModel = tracesDir + "trace-"+inputModelFiles[i].getName();
			
			//Load IN OUT and TRACE models
			final Model inModel = EmftvmFactory.eINSTANCE.createModel();
			final URI uri = URI.createFileURI(inModelPath);
			inModel.setResource(rs.getResource(uri, true));
			env.registerInputModel("IN", inModel);
			
			Model outModel = EmftvmFactory.eINSTANCE.createModel();
			outModel.setResource(rs.createResource(URI.createURI(outModelPath)));
			env.registerOutputModel("OUT", outModel);
			
			Model inoutModel = EmftvmFactory.eINSTANCE.createModel();
			inoutModel.setResource(rs.createResource(URI.createFileURI(traceModel)));
			env.registerInOutModel("trace", inoutModel);
						
			ModuleResolver mr = new DefaultModuleResolver(transformationDir, rs);
			TimingData td = new TimingData();
			env.loadModule(mr, TRmodule);
			
			td.finishLoading();
			
			try {
				env.run(td);
				td.finish();
								
				totalRules= (int) env.getRules().stream().count();
				float executionTime = td.getFinished() * 0.000001f;
				
				URI modelURI = URI.createFileURI(inModelPath);
				TraceLinkSet tls = (TraceLinkSet) inoutModel.getResource().getContents().get(0);
				Set<String> executedRulesNames = new HashSet<>(
						tls.getRules().stream().map(r -> r.getRule()).collect(Collectors.toList()));
				Set<String> executedRules = new HashSet<>(
						env.getRules().stream().filter(r -> executedRulesNames.contains(r.getName()))
								.map(r -> r.eResource().getURIFragment(r))
								.collect(Collectors.toList()));
				
				String res=  inputModelFiles[i].getName()+";"+toolName+";"+ executionTime+ ";"+ executedRules.size()+";"+ totalRules+";"+ executedRulesNames+"\n";
				success= success+res;
				log=log+ "SUCCESS "+res;
				
				//Ici il manque le calcul des totaux
				//Exemple: toutes les rules executes >> caclul du coverage
				
				nbSuccess++;
				
			} catch (Exception e) {
				String res=inputModelFiles[i].getName()+" "+toolName+"\n";
				fail=fail+res;
				log=log+ "FAILURE "+ res;
			}
			
			
			// Save models
			try {
				outModel.getResource().save(Collections.emptyMap());
				inoutModel.getResource().save(Collections.emptyMap());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		ExecutionOutput execOutput= new ExecutionOutput(nbInModels,nbSuccess);		
		execOutput.setSuccess(success);
		execOutput.setFail(fail);
		execOutput.setSummary(summary);
		execOutput.setLog(log);
		return execOutput;
		
	}
	
	/*
	 * I seriously hate relying on the eclipse facilities, and if you're not building an eclipse plugin
	 * you can't rely on eclipse's registry (let's say you're building a stand-alone tool that needs to run ATL
	 * transformation, you need to 'manually' register your metamodels) 
	 * This method does two things, it initializes an Ecore parser and then programmatically looks for
	 * the package definition on it, obtains the NsUri and registers it.
	 */
	private String lazyMetamodelRegistration(String metamodelPath){
		
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
   	
	    ResourceSet rs = new ResourceSetImpl();
	    // Enables extended meta-data, weird we have to do this but well...
	    final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(EPackage.Registry.INSTANCE);
	    rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
	
	    Resource r = rs.getResource(URI.createFileURI(metamodelPath), true);
	    EObject eObject = r.getContents().get(0);
	    // A meta-model might have multiple packages we assume the main package is the first one listed
	    if (eObject instanceof EPackage) {
	        EPackage p = (EPackage)eObject;
	        //System.out.println(p.getNsURI());
	        EPackage.Registry.INSTANCE.put(p.getNsURI(), p);
	        return p.getNsURI();
	    }
	    return null;
	}
	
	/*
	 * As shown above we need the inputMetamodelNsURI and the outputMetamodelNsURI to create the context of
	 * the transformation, so we simply use the return value of lazyMetamodelRegistration to store them.
	 * -- Notice that the lazyMetamodelRegistration(..) implementation may return null in case it doesn't 
	 * find a package in the given metamodel, so watch out for malformed metamodels.
	 * 
	 */
	public void registerInputMetamodel(String inputMetamodelPath){	
		inputMetamodelNsURI = lazyMetamodelRegistration(inputMetamodelPath);
	}

	public void registerOutputMetamodel(String outputMetamodelPath){
		outputMetamodelNsURI = lazyMetamodelRegistration(outputMetamodelPath);
	}
	
	private ExecEnv setupEnvironment(ResourceSet rs, URI inputMetamodelUri, URI outputMetamodelUri) {

		final ExecEnv env = EmftvmFactory.eINSTANCE.createExecEnv();

		final Metamodel emftvm = EmftvmFactory.eINSTANCE.createMetamodel();
		emftvm.setResource(rs.getResource(URI.createURI("http://www.eclipse.org/m2m/atl/2011/EMFTVM"), true));
		env.registerMetaModel("METAMODEL", emftvm);

		final Metamodel trace = EmftvmFactory.eINSTANCE.createMetamodel();
		trace.setResource(rs.getResource(URI.createURI("http://www.eclipse.org/m2m/atl/emftvm/2011/Trace"), true));
		env.registerMetaModel("TRACE", emftvm);

		final Metamodel IM = EmftvmFactory.eINSTANCE.createMetamodel();
		final Resource inputMetamodelResource = rs.getResource(inputMetamodelUri, true);
		IM.setResource(inputMetamodelResource);
		env.registerMetaModel(inputMetamodelUri.trimFileExtension().lastSegment(), IM);
		registerPackages(rs, inputMetamodelResource);

		final Metamodel Ecore = EmftvmFactory.eINSTANCE.createMetamodel();
		Ecore.setResource(rs.getResource(URI.createURI("http://www.eclipse.org/emf/2002/Ecore"), true));
		env.registerMetaModel("Ecore", Ecore);

		final Metamodel OM = EmftvmFactory.eINSTANCE.createMetamodel();
		OM.setResource(rs.getResource(outputMetamodelUri, true));
		env.registerMetaModel(outputMetamodelUri.trimFileExtension().lastSegment(), OM);
		registerPackages(rs, rs.getResource(outputMetamodelUri, true));

		return env;
	}
	
	private void registerNamespaces() {
		EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("emftvm", new EMFTVMResourceFactoryImpl());
	}

	private void registerPackages(ResourceSet resourceSet, Resource resource) {
		Iterator<EObject> eObjects = resource.getAllContents();
		while (eObjects.hasNext()) {
			final EObject eObject = eObjects.next();
			if (eObject instanceof EPackage) {
				EPackage p = (EPackage) eObject;
				resourceSet.getPackageRegistry().put(p.getNsURI(), p);
			}
		}
	}


}