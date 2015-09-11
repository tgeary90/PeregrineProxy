package factorymethod;

public class Description {
	private String description;

	public Description(String description) {
		super();
		this.description = description;
	}
	
	@Override
	public String toString() {
		return description;
	}
	
}
