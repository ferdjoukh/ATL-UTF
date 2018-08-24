package tests;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.atl.common.ATL.ActionBlock;
import org.eclipse.m2m.atl.common.ATL.InPattern;
import org.eclipse.m2m.atl.common.ATL.InPatternElement;
import org.eclipse.m2m.atl.common.ATL.MatchedRule;
import org.eclipse.m2m.atl.common.ATL.Module;
import org.eclipse.m2m.atl.common.ATL.ModuleElement;
import org.eclipse.m2m.atl.common.ATL.OutPattern;
import org.eclipse.m2m.atl.common.ATL.OutPatternElement;
import org.eclipse.m2m.atl.common.ATL.RuleVariableDeclaration;
import org.eclipse.m2m.atl.common.OCL.OclExpression;
import org.eclipse.m2m.atl.engine.parser.AtlParser;

public class readATL {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String atlFile="trafosTest/HSM2FSM/HSM2FSM.atl";
		
		try (InputStream in = new FileInputStream(atlFile)) {
		
		    EObject ast = AtlParser.getDefault().parse(in);
		    
		    findAllRules(ast);
		  }
	}		
	
	public static List<MatchedRule> findAllRules(EObject root) throws Exception{
	
		ArrayList<MatchedRule> rtn = new ArrayList<MatchedRule>();
		if(root instanceof Module){
			Module mod = (Module) root;
			for(ModuleElement elem : mod.getElements()){
				if(elem instanceof MatchedRule){
					MatchedRule r = (MatchedRule) elem;	
					System.out.println(r.getName());
					
					int filter=0;
										
					InPattern inp= r.getInPattern();
					List<InPatternElement> ipes= r.getInPattern().getElements();
					List<OutPatternElement> opes= r.getOutPattern().getElements();
					if(inp.getFilter()!=null)
						filter=1;
					
					OclExpression ocl= inp.getFilter();
					
					
//					System.out.println(" InPattern");
//					System.out.println("  filter="+inp.getFilter());
//					for(InPatternElement ipe: ipes) {
//						System.out.println("  "+ipe.getVarName());
//					}
//					
//				    System.out.println(" OutPattern");
//				    for(OutPatternElement ope: opes) {
//						System.out.println("  "+ope.getVarName());						
//					}
				    
					int score=1+filter+ipes.size()-1+opes.size()-1;
					
					System.out.println("  guard:"+filter);
				    System.out.println("  inVars:"+ipes.size());
				    System.out.println("  outVars:"+opes.size());
				    System.out.println("  SCORE="+score);
				    
				    
					
					rtn.add(r);						
				}
			}
		}

		return rtn;
	}
}
