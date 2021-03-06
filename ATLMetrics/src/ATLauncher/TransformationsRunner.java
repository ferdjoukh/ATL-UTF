package ATLauncher;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import ATLUtils.Utils;
import ATLUtils.ExecutionOutput;
import ATLUtils.ModelTransformation;
import ATLUtils.MyRule;
import ATLUtils.TransformationsReader;

/**
 * This class reads, collects, runs and gathers results of execution for model transformations.
 * All the model transformations are contained in a given folder (the key field of any TransformationRunner object).
 * Calling the methods if this Object produces many output files: log, csv ...
 * 
 * @author Adel Ferdjoukh <ferdjoukh@gmail.com>
 *
 */
public class TransformationsRunner {

	private TransformationsReader reader;
	private String verbose;
	
	/**
	 * Constructor need only the folder path where MT are located.
	 * All other data is always structured in the same way (see. docs/transformations-folder.md for details) 
	 * 
	 * 
	 * 
	 */
	public TransformationsRunner(TransformationsReader reader) {
		this.reader=reader;
		this.verbose="";
	}
			
	/**
	 * This method runs all the transformations that are in folder: trafoDirPath.
	 * You can find in <docs/transformations-folder.md> files how the folder of transformations should be formed.
	 * 
	 * 
	 * @return true if running was performed with success
	 * 			
	 * This method produces a csv file: trafoDirPath/results/executions-toolName.csv
	 */
	public boolean runAllTransformations() {
		
		String result = reader.getResult();
		String trDir = reader.getTrafoDirPath();
		File resulttDir = new File(trDir+"/"+result);
		resulttDir.mkdir();
		
		verbose= verbose+ reader.printInfo();
		verbose= verbose+ reader.printAllMT();
		verbose= verbose+ "RUNNING\n\n";
		
		String logFile= Utils.generateFileNamePostfix(result+"/execution", "log");
		String resFile= Utils.generateFileNamePostfix(result+"/execution-results", "csv");
		
		String transformationMetricsFile= Utils.generateFileNamePostfix(result+"/transformations-metrics", "csv");
		
		String transformationMetrics = "Transformation,Helper,CalledRule,MatchedRule,LazyMatchedRule,ComplexityScore\n";
		
		String globalResults="";
		String failure;
		
		for(ModelTransformation mt: reader.getModelTransformations()) {
			verbose= verbose+ "RUNNING ["+ mt +"] model transformation...\n\n";
			
			transformationMetrics = transformationMetrics + mt.atlMetricsTostring()+ "\n";
		
			//Create a failure file for each Model Transformation
			String failFile= Utils.generateFileNamePostfix(result+"/failed-models-"+mt.getName(), "log");
			failure="";
			
			for(String tool: mt.getTools()) {
				verbose= verbose+"processing models generated by ["+tool+"] tool...\n";
				
				//Run the Model Transformation
				ATLauncher l = new ATLauncher();
				ExecutionOutput exec= l.launch(reader.getTrafoDirPath(), mt.getName(), mt.getModule(), tool, 
						mt.getInMMRelativePath(), mt.getInMM(), mt.getOutMMRelativePath(), mt.getOutMM());
				
				verbose= verbose+ exec.getLog();
				verbose= verbose+"\n";
								
				int coverageScore= weightedRuleCoverage(mt.getRules(), exec.getExecutedRules());
				
				globalResults= globalResults+ exec.getSummary()+";"+coverageScore+";"+mt.getMaxScore()+"\n";
				
				verbose= verbose+ " coverageScore: "+coverageScore+ "\n";
				
				failure=failure+exec.getFail();
								
				//Create the specific result per tool
				if(!exec.getSuccess().equals("")) {
					String toolLog=Utils.generateFileNamePostfix(result+"/"+tool+"-"+mt.getName(), "csv");
					Utils.createOutputFile(reader.getTrafoDirPath()+"/"+toolLog, exec.getSuccess());
					verbose= verbose+ " creation of file: "+ toolLog+"\n";
				}
				verbose= verbose+"\n";
			}
									
			//Create the failure
			if(!failure.equals("")) {
				Utils.createOutputFile(reader.getTrafoDirPath()+"/"+failFile, failure);
				verbose= verbose+ "creation of file: "+ failFile+"\n";
			}
			
			verbose= verbose+"\n";
		}	
		
		//Create the transformations metrics file
		Utils.createOutputFile(reader.getTrafoDirPath()+"/"+transformationMetricsFile, transformationMetrics);
		verbose= verbose+ "creation of file: "+ transformationMetricsFile+"\n";
		
		//Create the global results file
		Utils.createOutputFile(reader.getTrafoDirPath()+"/"+resFile, globalResults);
		verbose= verbose+ "creation of file: "+ resFile+"\n";
		
		//Create the global log file
		Utils.createOutputFile(reader.getTrafoDirPath()+"/"+logFile, verbose);
		verbose= verbose+ "creation of file: "+ logFile+"\n";
				
		return true;		
	}
	
	/**
	 * This function computes the weighted rule coverage
	 * 
	 * @param allRules
	 * @param executedRules
	 * @return
	 */
	public int weightedRuleCoverage(ArrayList<MyRule> allRules, Set<String> executedRules) {
		int score=0;
		
		for(String execRule: executedRules) {
			int kind=Utils.isRuleContained(execRule,allRules);
			if(kind==0) {
				score=score+1;
			}else {
				score=score+kind;
			}
		}		
		return score;
	}
		
	public String getVerbose() {
		return verbose;
	}
}
