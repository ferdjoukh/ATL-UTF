package exceptions;

public class MissingParameterException extends Exception {
	public MissingParameterException(String msg) {
		super("Missing parameter: "+msg);
	}
}
