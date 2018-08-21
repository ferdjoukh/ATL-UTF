package ATLUtils;

public class ExecutionOutput {

	private String log;
	private String success;
	private String fail;
	private String summary;
	private int models;
	private int transformed;
	
	public ExecutionOutput(int nbModels, int nbTransformed) {
		this.models=nbModels;
		this.transformed=nbTransformed;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}
	
	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getFail() {
		return fail;
	}

	public void setFail(String fail) {
		this.fail = fail;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getModels() {
		return models;
	}

	public int getTransformed() {
		return transformed;
	}	
		
}
