package decorator;

// base to be decorated with extras
public abstract class Car
{
	protected String description = "car";
	public String getDescription()
	{
		return description;
	}
	public abstract double cost();
}
