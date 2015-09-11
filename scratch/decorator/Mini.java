package decorator;

public class Mini extends Car
{
	
	public Mini()
	{
		super();
		description = "Mini";
	}

	@Override
	public double cost()
	{
		return 3000.00d;
	}

}
