package decorator;

public class CarTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Car mini = new Mini();
		mini = new BodyKit(mini);
		mini = new SatNav(mini);
		mini = new Stereo(mini);
		System.out.println("car: " + mini.getDescription() + " costs: " + mini.cost());
	}

}
