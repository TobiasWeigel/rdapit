package rdapit.pidsystem;

public class PID {

	private String identifierName;
	
	public PID(String identifierName) {
		this.identifierName = identifierName;
	}
	
	public String getIdentifierName() {
		return identifierName;
	}
	
	@Override
	public String toString() {
		return identifierName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PID) return identifierName.equals(((PID) obj).identifierName);
		else return super.equals(obj);
	}
	
}
