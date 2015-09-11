package decorator;

public class BodyKit extends CarDecorator
{
	public BodyKit(Car car)
	{
		super();
		this.car = car;
	}

	@Override
	public String getDescription()
	{
		return new String(car.getDescription() + ",body kit");
	}

	@Override
	public double cost()
	{
		return 500.00d + car.cost();
	}
}
