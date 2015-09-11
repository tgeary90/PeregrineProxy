package decorator;

public class Stereo extends CarDecorator
{
	public Stereo(Car car)
	{
		super();
		this.car = car;
	}

	@Override
	public String getDescription()
	{
		return car.getDescription() + ",stereo";
	}

	@Override
	public double cost()
	{
		return car.cost() + 100.00d;
	}

}
