package decorator;

public class SatNav extends CarDecorator
{
	public SatNav(Car car)
	{
		super();
		this.car = car;
	}

	@Override
	public String getDescription()
	{
		return car.getDescription() + ",satnat";
	}


	@Override
	public double cost()
	{
		return car.cost() + 200.00d;
	}

}
