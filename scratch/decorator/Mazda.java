package decorator;

public class Mazda extends Car
{
	
	public Mazda()
	{
		super();
		description = "Mazda";
	}

	@Override
	public double cost()
	{
		return 2000.00d;
	}
}
