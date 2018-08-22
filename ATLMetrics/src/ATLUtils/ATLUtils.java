package ATLUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public abstract class ATLUtils {

	public static String generateFileNamePostfix(String prefix, String filetype) {
		String res=prefix+"_";
		
		DateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
		Date date = new Date();
		
		res=res+format.format(date);
		
		res=res+"."+filetype;
		return res;
	}
	
	public static void createOutputFile(String filePath, String content) {
		try {
			PrintWriter out = new PrintWriter(filePath);
			out.write(content);
			out.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Given a List of rules and a rule R, it checks if R is contained in the list
	 * @param execRule
	 * @param allRules
	 * @return 0 if not contained and kind=[1,2,3,4] otherwise
	 */
	public static int isRuleContained(String execRule, ArrayList<MyRule> allRules) {
		
		int kind=0;
		int i=0;
		
		while(kind==0 && i<allRules.size()) {
			
			MyRule currentrule=allRules.get(i);  
			if(currentrule.getName().equals(execRule)) {
				kind=currentrule.getKind();
			}
			i++;
		}
		
		
		return kind;
	}
}
