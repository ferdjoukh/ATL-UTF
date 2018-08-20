package ATLUtils;

public class MyRule {

	private String name;
	// kind IN [1,2,3,4]
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
