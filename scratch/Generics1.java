import java.util.ArrayList;
import java.util.List;

/**
 * Class demonstrating that generic types can contains subtypes of their parameterized types.
 * @author tom
 *
 */
public class Generics1
{
	private static List<Number> nums = new ArrayList<>();
	private static List<Integer> ints = new ArrayList<>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		nums.add(new Integer(1));
		nums.add(new Double(1.2d));
		nums.add(new Float(1.33f));
		// auto-boxing.
		nums.add(4);
		nums.add(89.4d);
		nums.add(14.88f);
		
		for (Number n : nums)
			System.out.printf("%d, ", n.intValue());
		System.out.println();
	}
}
