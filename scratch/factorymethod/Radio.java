package factorymethod;

public abstract class Radio {
	Id id;
	Description description;
	
	public Radio(Id id, Description description) {
		super();
		this.id = id;
		this.description = description;
	}

	public void prep() {
		description = new Description(description.toString() + 
				" and is prepared");
	}
	
	public String status() {
		return description.toString();
	}
}
