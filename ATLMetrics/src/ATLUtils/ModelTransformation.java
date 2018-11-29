package ATLUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.atl.common.ATL.CalledRule;
import org.eclipse.m2m.atl.common.ATL.Helper;
import org.eclipse.m2m.atl.common.ATL.InPattern;
import org.eclipse.m2m.atl.common.ATL.InPatternElement;
import org.eclipse.m2m.atl.common.ATL.LazyMatchedRule;
import org.eclipse.m2m.atl.common.ATL.MatchedRule;
import org.eclipse.m2m.atl.common.ATL.Module;
import org.eclipse.m2m.atl.common.ATL.ModuleElement;
import org.eclipse.m2m.atl.common.ATL.OutPatternElement;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.engine.parser.AtlParser;

import Ecore.MetaModelReader;

/**
 * This class creates Model Transformations.
 *  
 * Its purpose is to gather all the information about on model transformation in one object.
 * It produces also metrics on: the complexity of rules, ATL and Ecore input meta-model
 * 
 *   
 * @author Adel Ferdjoukh <ferdjoukh@gmail.com>
 *
 */
public class ModelTransformation {
	private String rootFolder;
	private String name;
	private String moduleName;
	private String absoluteATLFilePath;
	private String inMM;
	private String inMMRelativePath;
	private String rootClass;
	private String outMM;
	private String outMMRelativePath;
	
	private ArrayList<String> tools; 
	private ArrayList<MyRule> rules;
	
	///////////////////////////
	// ATL Metrics
	//////////////////////////
	private Module module;
	private ArrayList<Helper> helpers = new ArrayList<Helper>();
	private ArrayList<CalledRule> calledRules = new ArrayList<CalledRule>();
	private ArrayList<MatchedRule> matchedRules = new ArrayList<MatchedRule>();
	private ArrayList<LazyMatchedRule> lazyMatchedRules = new ArrayList<LazyMatchedRule>();
	private int complexityScore;
	
	///////////////////////////
	// Ecore Metrics
	//////////////////////////
	private int concreteClasses=0;
	private int abstractClasses=0;
	private int references=0;
	private int attributes=0;
	private ArrayList<String> attributesTypes=new ArrayList<String>();
	private int containmentTreeDepth=0;
	private int inheritanceTreeDepth=0;
	
	public ModelTransformation( String rootFolder, String name, 
								String absoluteFilePath,String module, 
							    String inMM, String inMMRelativePath, String rootClass,
			                    String outMM, String outMMRelativePath) {
		
		this.rootFolder= rootFolder;
		this.name= name;
		this.moduleName= module;
		this.absoluteATLFilePath= absoluteFilePath;
		this.inMMRelativePath= inMMRelativePath;
		this.outMMRelativePath= outMMRelativePath;
		this.inMM= inMM;
		this.outMM= outMM;
		this.rootClass = rootClass;
		this.rules= new ArrayList<MyRule>();
		this.tools= new ArrayList<String>();
		this.complexityScore = 0;
		
		////////////////////////////////////////////////////////////////////
		//Create all the metrics: rules score, ATL metrics and Ecore Metrics
		////////////////////////////////////////////////////////////////////
		readModuleFromATLFile();
		createAllRulesScores();
		readATLMetrics();
		readMetaModelMetris();

		////////////////////////////////////////////////////////////////////
		//Create 3 metrics files
		////////////////////////////////////////////////////////////////////
		createMetricsFile("rules", printableRules());
		createMetricsFile("atlmetrics", atlMetricsTostring());
		createMetricsFile("ecoremetrics", metamodelMetricsToString());
	}
	
	/**
	 * This method parses an ATL file into a EMF model EObject.
	 * 
	 * @return the main module of the model transformation
	 */
	private void readModuleFromATLFile(){
		
		Module m = null;
		try (InputStream in = new FileInputStream(absoluteATLFilePath)) {
		
		    EObject ast = null;
			try {
				ast = AtlParser.getDefault().parse(in);
			} catch (ATLCoreException e) {
				System.out.println(e.getMessage());
			}
		    if(ast instanceof Module)
		    	m = (Module) ast;
		} catch (Exception e1) {
			System.out.println(e1.getMessage());
		} 
		this.module = m;
	}
		
	/**
	 * This method reads all the rules of a model transformation 
	 * and generates a complexity score for each of them
	 * Then its add them to this.rules
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ATLCoreException
	 */
	private void createAllRulesScores() {
		
		if(module instanceof Module){
			for(ModuleElement elem : module.getElements()){
				
				if(elem instanceof MatchedRule){
					MatchedRule r = (MatchedRule) elem;	
					int filter=0;
										
					InPattern inp= r.getInPattern();
					List<InPatternElement> ipes= r.getInPattern().getElements();
					List<OutPatternElement> opes= r.getOutPattern().getElements();
					if(inp.getFilter()!=null)
						filter=1;
					
					//OclExpression ocl= inp.getFilter();
					//int compOCL=expandOCL(ocl," ",0);
					
					int score=1+filter+ipes.size()-1+opes.size()-1;
					
					MyRule rule= new MyRule(r.getName(), score);
					addRule(rule);											
				}
			}
		}
	}
	
	private void readATLMetrics() {
		for(ModuleElement elem : module.getElements()){
			
			if(elem instanceof MatchedRule){
				this.matchedRules.add((MatchedRule) elem);
			}
			if(elem instanceof Helper) {
				this.helpers.add((Helper) elem);
			}
			if(elem instanceof CalledRule) {
				this.calledRules.add((CalledRule) elem);
			}
			if(elem instanceof LazyMatchedRule) {
				this.lazyMatchedRules.add((LazyMatchedRule) elem);
			}			
		}
	}
	
	private void readMetaModelMetris() {
		
		String inMMAbsolutePath = rootFolder+"/"+name+"/metamodels/input/"+inMMRelativePath;
		MetaModelReader reader = new MetaModelReader(inMMAbsolutePath, rootClass);
		
		concreteClasses = reader.getConcreteClasses().size();
		abstractClasses = reader.getAbstractClasses().size();
		attributes = reader.getAllAttributesofMetamodel().size();
		attributesTypes = (ArrayList<String>) reader.getAllTypesOfAttributes();
		references = reader.getAllReferencesOfMetamodel().size();
		containmentTreeDepth = reader.containmentTreeDepth();
		inheritanceTreeDepth = reader.getMetamodelMaxInheritanceDepth();
	}
	
//	private int expandOCL(OclExpression expr, String space, int depth) {
//		if(expr!=null) {
//			int score=0;
//			if(expr instanceof PropertyCallExp) {
//				PropertyCallExp call= (PropertyCallExp) expr;
//				score=score+1+expandOCL(call.getSource(), space+" ",depth++);
//			
//				if(call instanceof OperationCallExp) {
//					OperationCallExp operator= (OperationCallExp) call;
//					for(OclExpression o: operator.getArguments()) {
//						score=score+1+expandOCL(o, space+" ",depth++);
//					}
//				}
//				
//				if(call instanceof NavigationOrAttributeCallExp) {
//					NavigationOrAttributeCallExp ocl= (NavigationOrAttributeCallExp) call;
//				}
//			}
//			
//			if(expr instanceof VariableExp) {
//				VariableExp var= (VariableExp) expr;								
//			}			
//			return score;
//		}else{
//			return 0;
//		}
//	}
	
	private void computeMaxScore() {
		int maxScore=0;
		for(MyRule rule: rules) {
			maxScore=maxScore+rule.getScore();
		}
		this.complexityScore=maxScore;
	}
	
	///////////////////////////////////////////////////////////////////
	//  methods to print all types of metrics for ModelTransformation
	///////////////////////////////////////////////////////////////////
	/**
	 * This method creates a file named: <ModelTransformationName>.rules
	 * It contains all the rules of the model transformation and their complexity scores
	 * 
	 */
	private void createMetricsFile(String suffix, String content){
		int begin=0;
		int end= this.absoluteATLFilePath.lastIndexOf(".")+1;
		String filePath= this.absoluteATLFilePath.substring(begin, end)+suffix;
		Utils.createOutputFile(filePath,content);		
	}
	
	/**
	 * This method return a printable visualization for the rules of an MT
	 * 
	 * @return
	 */
	private String printableRules() {
		String res="";
		for(MyRule rule: this.rules) {
			res=res+rule.toString()+"\n";
		}
		return res;
	}
	
	
	public String prettyPrint() {
		String res="";
		
		res=res+this.name+" [ module:"+ this.moduleName+", "
				+ "maxScore:"+ this.complexityScore+", "
				+ "meta-models: "+ this.inMM +"("+this.inMMRelativePath+") > "
				+ this.outMM+"("+this.outMMRelativePath+") ]\n";
		
		res=res+"\t Rules: ";
		for(MyRule rule: this.rules) {
			res=res+" "+rule.getName()+"("+rule.getScore()+")";
		}
		res=res+"\n\n";
		return res;
	}
	
	public String atlMetricsTostring() {
		String res= "Transformation,Helper,CalledRule,MatchedRule,LazyMatchedRule,ComplexityScore\n";
		res = res 	+ moduleName + "," 
					+ helpers.size() + "," 
					+ calledRules.size() + "," 
					+ matchedRules.size() + "," 
					+ lazyMatchedRules.size()+ ","
					+ complexityScore + "\n";
		
		return res;		
	}
	
	public String metamodelMetricsToString() {
		String res = "moduleName,"
					+ "Metamodel,"
					+ "concreteClasses,"
					+ "abstractClasses,"
					+ "containTreeDepth,"
					+ "references,"
					+ "inherTreeDepth,"
					+ "attributes,"
					+ "attributeTypes\n";
		
		res = res 	+ moduleName + "," 
					+ inMM + "," 
					+ concreteClasses + "," 
					+ abstractClasses + "," 
					+ containmentTreeDepth + ","
					+ references + "," 
					+ inheritanceTreeDepth + ","
					+ attributes + "," 
					+ attributesTypes + "\n";
		return res;
	}
	
	///////////////////////////////////////////////////////////////////////
	//  Getters and Setters
	///////////////////////////////////////////////////////////////////////
	public String getAbsoluteATLFilePath() {
		return absoluteATLFilePath;
	}

	public void setAbsoluteATLFilePath(String absoluteATLFilePath) {
		this.absoluteATLFilePath = absoluteATLFilePath;
	}
	
	public void addTool(String tool) {
		this.tools.add(tool);
	}
		
	public void addRule(MyRule rule) {
		this.rules.add(rule);
		this.complexityScore=this.complexityScore+rule.getScore();
	}

	public String getName() {
		return name;
	}

	public String getModule() {
		return moduleName;
	}

	public String getInMMRelativePath() {
		return inMMRelativePath;
	}

	public String getInMM() {
		return inMM;
	}

	public String getOutMMRelativePath() {
		return outMMRelativePath;
	}

	public String getOutMM() {
		return outMM;
	}

	public ArrayList<MyRule> getRules() {
		return rules;
	}

	public void setRules(ArrayList<MyRule> rules) {
		this.rules = rules;
		computeMaxScore();
	}
	
	public int getMaxScore() {
		return complexityScore;
	}

	public ArrayList<String> getTools() {
		return tools;
	}

	public void setTools(ArrayList<String> tools) {
		this.tools = tools;
	}
		
	public String getModuleName() {
		return moduleName;
	}

	public ArrayList<Helper> getHelpers() {
		return helpers;
	}

	public ArrayList<CalledRule> getCalledRules() {
		return calledRules;
	}

	public ArrayList<MatchedRule> getMatchedRules() {
		return matchedRules;
	}

	public ArrayList<LazyMatchedRule> getLazyMatchedRules() {
		return lazyMatchedRules;
	}
}
