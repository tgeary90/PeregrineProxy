package decorator;

public abstract class CarDecorator extends Car
{
	protected Car car;

	@Override
	public abstract String getDescription();
}
