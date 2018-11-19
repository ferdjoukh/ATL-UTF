package ATLUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.atl.common.ATL.InPattern;
import org.eclipse.m2m.atl.common.ATL.InPatternElement;
import org.eclipse.m2m.atl.common.ATL.MatchedRule;
import org.eclipse.m2m.atl.common.ATL.Module;
import org.eclipse.m2m.atl.common.ATL.ModuleElement;
import org.eclipse.m2m.atl.common.ATL.OutPatternElement;
import org.eclipse.m2m.atl.common.OCL.LoopExp;
import org.eclipse.m2m.atl.common.OCL.NavigationOrAttributeCallExp;
import org.eclipse.m2m.atl.common.OCL.OclExpression;
import org.eclipse.m2m.atl.common.OCL.OclUndefinedExp;
import org.eclipse.m2m.atl.common.OCL.OperationCallExp;
import org.eclipse.m2m.atl.common.OCL.OperatorCallExp;
import org.eclipse.m2m.atl.common.OCL.PropertyCallExp;
import org.eclipse.m2m.atl.common.OCL.VariableExp;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.emftvm.Rule;
import org.eclipse.m2m.atl.engine.parser.AtlParser;

/**
 * This class creates an object of type Model Transformation. 
 * Its purpose is to gather all the information about on model transformation in one object.
 * 
 *   
 * @author Adel Ferdjoukh
 *
 */
public class ModelTransformation {
	private String name;
	private String module;
	private String absoluteATLFilePath;
	private String inMM;
	private String inMMRelativePath;
	private String outMM;
	private String outMMRelativePath;
	private ArrayList<String> tools; 
	private ArrayList<MyRule> rules;
	private int maxScore;
	
	public ModelTransformation(String name, String absoluteFilePath,String module, 
							   String inMM, String inMMRelativePath,
			                   String outMM, String outMMRelativePath) {
		this.name= name;
		this.module= module;
		this.absoluteATLFilePath= absoluteFilePath;
		this.inMMRelativePath= inMMRelativePath;
		this.outMMRelativePath= outMMRelativePath;
		this.inMM= inMM;
		this.outMM= outMM;
		rules= new ArrayList<MyRule>();
		tools= new ArrayList<String>();
		maxScore=0;
	}
	
	/**
	 * This method parses an .atl file into a EMF model Eobject.
	 * 
	 * @return the main module of the model transformation
	 */
	private Module readATLFile(){
		
		//String atlFile="trafosTest/HSM2FSM/HSM2FSM.atl";
		Module m = null;
		
		try (InputStream in = new FileInputStream(absoluteATLFilePath)) {
		
		    EObject ast = null;
			try {
				ast = AtlParser.getDefault().parse(in);
			} catch (ATLCoreException e) {
				e.printStackTrace();
			}
		    if(ast instanceof Module)
		    	m=(Module) ast;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return m;
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
	public void createAllRulesScores() {
		
		Module root= readATLFile();
		
		if(root instanceof Module){
			Module mod = (Module) root;
			for(ModuleElement elem : mod.getElements()){
				if(elem instanceof MatchedRule){
					MatchedRule r = (MatchedRule) elem;	
					int filter=0;
										
					InPattern inp= r.getInPattern();
					List<InPatternElement> ipes= r.getInPattern().getElements();
					List<OutPatternElement> opes= r.getOutPattern().getElements();
					if(inp.getFilter()!=null)
						filter=1;
					
					OclExpression ocl= inp.getFilter();
					
					int compOCL=expandOCL(ocl," ",0);
					
					//System.out.println(r.getName());
					//System.out.println("score="+compOCL);
					//System.out.println("");
					
					
//					if(ocl!=null) {
//						System.out.println(r.getName());
//						System.out.println("  "+ocl);
//						
//						if(ocl instanceof OperationCallExp) {
//							OperationCallExp oce= (OperationCallExp) ocl;
//							
//							System.out.println(oce.getArguments());
//							System.out.println(oce.getSource());
//							System.out.println(oce.getSource().getClass());
//							
//						}
//					}
					
					//System.out.println(ocl.getOwningAttribute());
					
					int score=1+filter+ipes.size()-1+opes.size()-1;
					
//					System.out.println(r.getName());
//					System.out.println("  guard:"+filter);
//				    System.out.println("  inVars:"+ipes.size());
//				    System.out.println("  outVars:"+opes.size());
//				    System.out.println("  SCORE="+score);
				    
					MyRule rule= new MyRule(r.getName(), score);
					addRule(rule);											
				}
			}
		}
	}
	
	/**
	 * This method creates a file named: <ModelTransformationName>.rules
	 * It contains all the rules of the model transformation and their complexity scroes
	 * 
	 */
	public void createRulesFile(){
		int begin=0;
		int end= this.absoluteATLFilePath.lastIndexOf(".")+1;
		String filePath= this.absoluteATLFilePath.substring(begin, end)+"rules";
		Utils.createOutputFile(filePath,printableRules());		
	}
	
	/**
	 * This method return a printable visualisation for the rules of an MT
	 * 
	 * @return
	 */
	public String printableRules() {
		String res="";
		for(MyRule rule: this.rules) {
			res=res+rule.toString()+"\n";
		}
		return res;
	}
	
	public int expandOCL(OclExpression expr, String space, int depth) {
		if(expr!=null) {
			
			String label="";
			int score=0;
			
			if(expr instanceof PropertyCallExp) {
				PropertyCallExp call= (PropertyCallExp) expr;
				score=score+1+expandOCL(call.getSource(), space+" ",depth++);
			
			
				if(call instanceof OperationCallExp) {
					OperationCallExp operator= (OperationCallExp) call;
					label=operator.getOperationName();
					for(OclExpression o: operator.getArguments()) {
						score=score+1+expandOCL(o, space+" ",depth++);
					}
				}
				
				if(call instanceof NavigationOrAttributeCallExp) {
					NavigationOrAttributeCallExp ocl= (NavigationOrAttributeCallExp) call;
					label=ocl.getName();
				}
			}
			
			if(expr instanceof VariableExp) {
				VariableExp var= (VariableExp) expr;
				label=var.getReferredVariable().getVarName();				
			}
			
			
			//System.out.println(space+" "+depth+" "+label+" "+expr);
			return score;
		}else
		{
			return 0;
		}
	}
	
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
		this.maxScore=this.maxScore+rule.getScore();
	}

	public String getName() {
		return name;
	}

	public String getModule() {
		return module;
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
	
	private void computeMaxScore() {
		
		int sum=0;
		for(MyRule rule: rules) {
			sum=sum+rule.getScore();
		}
		System.out.println("maxScore"+sum);
		this.maxScore=sum;
	}
	
	public int getMaxScore() {
		return maxScore;
	}

	public ArrayList<String> getTools() {
		return tools;
	}

	public void setTools(ArrayList<String> tools) {
		this.tools = tools;
	}
	
	public String toString() {
		return this.getName();
	}
	
	public String prettyPrint() {
		String res="";
		
		res=res+this.name+" [ module:"+ this.module+", "
				+ "maxScore:"+ this.maxScore+", "
				+ "meta-models: "+ this.inMM +"("+this.inMMRelativePath+") > "
				+ this.outMM+"("+this.outMMRelativePath+") ]\n";
		
		res=res+"\t Rules: ";
		for(MyRule rule: this.rules) {
			res=res+" "+rule.getName()+"("+rule.getScore()+")";
		}
		res=res+"\n\n";
		return res;
	}
}
