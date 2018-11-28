
public class Help {

	public static void printHelp() {
		
		System.out.println("ATL-UTF");
		System.out.println("  a Unit Testing Framework for ATL model transformation language");
		System.out.println("");		
	}
	
	public static void printGeneralHelp() {
		
		printHelp();
		
		System.out.println("USAGE");
		System.out.println("  java -jar atlutf.jar [COMMAND] [FOLDER]");
		System.out.println("");		
		
		System.out.println("COMMAND");
		System.out.println("  ut (Unit Testing mode)");		
		System.out.println("     In this configuration, the user wants to unit test one model trnasformation");
		System.out.println("     The tool then generates unit tests that covers the MT");
		System.out.println("     Many metrics are produced on the MT, meta-model, covered and uncovered rules");
		System.out.println("");
		
		System.out.println("  cc (Coverage Calculator mode)");
		System.out.println("     In this configuration, the user already owns unit tests from different origins.");
		System.out.println("     He wants to compare several model sets for seveal model transformations");
		System.out.println("     The tool produces metrics on the coverage of model transformations by the given models");
		System.out.println("");
		
		System.out.println("FOLDER");
		System.out.println("  ut");
		System.out.println("     a folder that contains one model transformation");
		System.out.println("");
		
		System.out.println("  cc");
		System.out.println("     a folder that contains several (>=1) model transformation folders");
		System.out.println("");
		
		System.out.println("HELP");
		System.out.println("  Main help");
		System.out.println("    java -jar atlutf.jar help");
		System.out.println("");
		System.out.println("  Unit Tester help");
		System.out.println("    java -jar atlutf.jar help ut");
		System.out.println("");
		System.out.println("  Coverage Calculator help");
		System.out.println("    java -jar atlutf.jar help cc");
		System.out.println("");
		
		System.out.println("CREDITS");
		System.out.println("  version:  ATL-UTF 1.0 (December 2018)");
		System.out.println("  authors:  Adel Ferdjoukh <adel.ferdjoukh@tuwien.ac.at> (main contributor)");
		System.out.println("            Manuel Wimmer <manuel.wimmer@tuwien.ac.at> (Team leader)");
	}
	
	public static void printUTHelp() {
		
		printHelp();
		
		System.out.println("Unit Tester (help)");
		System.out.println("  java -jar atlutf.jar ut modelTransformationFolder");
		System.out.println("");
		System.out.println("  The mandatory structure of input folder is described here:");
		System.out.println("     https://github.com/ferdjoukh/ATLrunner/blob/master/documentation/tranformations-folder.md");
	}
	
	public static void printCoverageCalculatorHelp() {
		
		printHelp();
		
		System.out.println("Coverage Calculator (help)");
		System.out.println("  java -jar atlutf.jar cc modelTransformationsFolder");
		System.out.println("");		
		System.out.println("  The mandatory structure of input folder is described here:");
		System.out.println("     https://github.com/ferdjoukh/ATLrunner/blob/master/documentation/tranformations-folder.md");
		
	}
}
