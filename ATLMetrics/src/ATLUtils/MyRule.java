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
	private int score;
	
	public MyRule(String name, int score) {
		this.name=name;
		this.score=score;
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}
	
	public String toString() {
		return name+","+score;
	}
}
