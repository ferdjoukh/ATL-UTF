package ATLauncher;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.EmftvmFactory;
import org.eclipse.m2m.atl.emftvm.ExecEnv;
import org.eclipse.m2m.atl.emftvm.Metamodel;
import org.eclipse.m2m.atl.emftvm.Model;
import org.eclipse.m2m.atl.emftvm.Rule;
import org.eclipse.m2m.atl.emftvm.trace.*;
import org.eclipse.m2m.atl.emftvm.impl.resource.EMFTVMResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.trace.TraceLinkSet;
import org.eclipse.m2m.atl.emftvm.util.DefaultModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.ModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.TimingData;

import ATLUtils.ExecutionOutput;

/**
 * This class launches an ATL transformation (.atl, .emftvm and .ecore meta-models) using a set of .xmi models
 * 
 * @author Adel Ferdjoukh
 *
 */
public class ATLauncher {
		
	//Main transformation launch method
	/**
	 * 
	 * @param globalDir
	 * @param TRname
	 * @param TRmodule
	 * @param toolName
	 * @param inMetamodelFile
	 * @param inMetamodelName
	 * @param outMetamodelFile
	 * @param outMetamodelName
	 * @return an Object of type ExecutionOutput that gathers all the information on the execution:
	 * 			log, failed models, transformed models and covered rules
	 */
	@SuppressWarnings("finally")
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
		int nbInModels=0;
		int totalRules= 0;
		final Set<String> totalExecutedRules = new HashSet<>();
		
		try {
			
			String transformationDir=globalDir+"/"+TRname+"/";
			String inMetamodelPath= transformationDir+"metamodels/input/"+ inMetamodelFile;
			String outMetamodelPath= transformationDir+"metamodels/output/"+ outMetamodelFile;
			String inModelDir=transformationDir+"models/input/"+toolName;
			String outModelDir=transformationDir+"models/output/"+toolName;
			String tracesDir=transformationDir+"models/traces/"+toolName;
			
			ResourceSet rs = new ResourceSetImpl();
			
			registerNamespaces();
			
			File inputMetamodel = new File(inMetamodelPath);
			File outputMetamodel = new File(outMetamodelPath);
			
			URI inputMetamodelUri = URI
					.createFileURI(inputMetamodel.getAbsolutePath());
			URI outputMetamodelUri = URI
					.createFileURI(outputMetamodel.getAbsolutePath());

			ExecEnv env = EmftvmFactory.eINSTANCE.createExecEnv();
			env = setupEnvironment(rs, inputMetamodelUri, outputMetamodelUri);
			
			File inModelFolder = new File(inModelDir);
			File[] inputModelFiles = inModelFolder.listFiles();
					
			nbInModels= inputModelFiles.length;
				
			for(int i=0; i<inputModelFiles.length;i++) {
				
				String inModelPath=inputModelFiles[i].getPath();
				String outModelPath=outModelDir+"/out-"+inputModelFiles[i].getName();
				String traceModel = tracesDir + "/trace-"+inputModelFiles[i].getName();
				
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
					}catch(Exception e) {
						String res=inputModelFiles[i].getName()+" "+toolName+"\n";
						fail=fail+res;
						fail=fail+"  "+e.getMessage()+"\n";
						log=log+ "EXCEPTION "+ res;						
					}
					finally {
						td.finish();
						
						totalRules= (int) env.getRules().size();
						float executionTime = td.getFinished() * 0.000001f;
						
						// with repetitions
						final List<String> executedRulesNames = inoutModel.getResource().getContents()
								.stream().findFirst().map(o -> new ArrayList<TracedRule>(((TraceLinkSet) o).getRules()))
								.map(l -> l.stream().map(r -> r.getRule()).collect(Collectors.toList()))
								.orElse(new ArrayList<String>());
						
						// without repetitions
						final Set<Rule> executedRules = env.getRules().stream()
								.filter(r -> executedRulesNames.contains(r.getName()))
								.collect(Collectors.toSet());

						String res=  inputModelFiles[i].getName()+";"+executionTime+ ";"+ executedRules.size()+";"+ totalRules+";"+ executedRulesNames+"\n";
						success= success+res;
						log=log+ "SUCCESS "+res;
						
						//Ici il manque le calcul des totaux
						//Exemple: toutes les rules executes >> caclul du coverage
						for(String rule: executedRulesNames) {
							if(!totalExecutedRules.contains(rule)) {
								totalExecutedRules.add(rule);
							}
						}						
						nbSuccess++;
					}
					
				// Save models
//				try {
//					outModel.getResource().save(Collections.emptyMap());
//					inoutModel.getResource().save(Collections.emptyMap());
//				} catch (IOException e) {
//					
//				}
			}
			
		} catch (Exception e) {
			//If there is a global problem with the Model Transformation
			fail=fail+e.getMessage();
			log=log+ "MT FAILURE "+ e.getMessage();				
		} finally {
			
			//Add totalExecutedRules to summary
			summary=toolName+";"+TRname+";"+totalExecutedRules.size()+";"+totalRules+";"+totalExecutedRules+";"+nbInModels;
			log=log+" SUMMARY "+summary;
			
			ExecutionOutput execOutput= new ExecutionOutput(nbInModels,nbSuccess);		
			execOutput.setExecutedRules(totalExecutedRules);
			execOutput.setSuccess(success);
			execOutput.setFail(fail);
			execOutput.setSummary(summary);
			execOutput.setLog(log);
			
			return execOutput;		
		}				
	}
	
	/*
	 * I seriously hate relying on the eclipse facilities, and if you're not building an eclipse plugin
	 * you can't rely on eclipse's registry (let's say you're building a stand-alone tool that needs to run ATL
	 * transformation, you need to 'manually' register your metamodels) 
	 * This method does two things, it initializes an Ecore parser and then programmatically looks for
	 * the package definition on it, obtains the NsUri and registers it.
	 */

	
	/*
	 * As shown above we need the inputMetamodelNsURI and the outputMetamodelNsURI to create the context of
	 * the transformation, so we simply use the return value of lazyMetamodelRegistration to store them.
	 * -- Notice that the lazyMetamodelRegistration(..) implementation may return null in case it doesn't 
	 * find a package in the given metamodel, so watch out for malformed metamodels.
	 * 
	 */

	private ExecEnv setupEnvironment(ResourceSet rs, URI inputMetamodelUri, URI outputMetamodelUri) {

		final ExecEnv env = EmftvmFactory.eINSTANCE.createExecEnv();

		final Metamodel emftvm = EmftvmFactory.eINSTANCE.createMetamodel();
		emftvm.setResource(rs.getResource(URI.createURI("http://www.eclipse.org/m2m/atl/2011/EMFTVM"), true));
		env.registerMetaModel("METAMODEL", emftvm);

		final Metamodel trace = EmftvmFactory.eINSTANCE.createMetamodel();
		trace.setResource(rs.getResource(URI.createURI("http://www.eclipse.org/m2m/atl/emftvm/2011/Trace"), true));
		env.registerMetaModel("TRACE", emftvm);

		if (inputMetamodelUri.toString().endsWith("Ecore.ecore")) {
			final Metamodel Ecore = EmftvmFactory.eINSTANCE.createMetamodel();
			Ecore.setResource(rs.getResource(URI.createURI("http://www.eclipse.org/emf/2002/Ecore"), true));
			env.registerMetaModel("Ecore", Ecore);
		} else {
			final Metamodel IM = EmftvmFactory.eINSTANCE.createMetamodel();
			final Resource inputMetamodelResource = rs.getResource(inputMetamodelUri, true);
			IM.setResource(inputMetamodelResource);
			env.registerMetaModel(inputMetamodelUri.trimFileExtension().lastSegment(), IM);
			registerPackages(rs, inputMetamodelResource);

			final Metamodel Ecore = EmftvmFactory.eINSTANCE.createMetamodel();
			Ecore.setResource(rs.getResource(URI.createURI("http://www.eclipse.org/emf/2002/Ecore"), true));
			env.registerMetaModel("Ecore", Ecore);
		}

		final Metamodel OM = EmftvmFactory.eINSTANCE.createMetamodel();
		final Resource outputMetamodelResource = rs.getResource(outputMetamodelUri, true);
		OM.setResource(outputMetamodelResource);
		env.registerMetaModel(outputMetamodelUri.trimFileExtension().lastSegment(), OM);
		registerPackages(rs, outputMetamodelResource);

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