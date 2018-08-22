package ATLUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
}
