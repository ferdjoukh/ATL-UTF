package ATLUtils;

/**
 * This class is used when reading the rules of a model transformation from .rules files.
 * It allows the creation of Rule object in order to store their kind (complexity score)
 * 
 * @author Adel Ferdjoukh
 *
 */
public class MyRule {

	private String name;
	// kind in [1,2,3,4]
	private int kind;
	
	public MyRule(String name, int kind) {
		this.name=name;
		this.kind=kind;
	}

	public String getName() {
		return name;
	}

	public int getKind() {
		return kind;
	}
}
