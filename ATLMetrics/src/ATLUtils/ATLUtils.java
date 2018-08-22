package ATLUtils;

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
}
