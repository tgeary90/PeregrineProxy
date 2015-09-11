import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * Class to test drive the utility class.
 * @author tom
 *
 */
public class Generics2
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		List<Double> doubles = Arrays.asList(1.1d, 2.7d, 3.8d);
		GenericUtils.printNumsAsDoubles(doubles);
		System.out.println("sum: " + GenericUtils.integerSum(doubles));
		GenericUtils.printList(doubles);
		
		System.out.println("type: " + GenericUtils.create("Hello, World!", String.class));
		String s = "hello";
		
		List<Integer> pp = new ArrayList<>(Arrays.asList(1, 17, 3, 14, 5)); // primes: 17, 3, 5
		List<Integer> primes = GenericUtils.primesInCollection(pp);
		System.out.println(primes);
		
		List<Double> pp2 = new ArrayList<>(Arrays.asList(2.3d, 1.1d, 7.3d, 3.5d, 15d));
		Collection<?> primes2 = GenericUtils.primesInCollection(pp2);
		System.out.println(primes2);
		
		System.out.println("max: " + GenericUtils.max(Arrays.asList(1, 2, 3), Integer.class));
		System.out.println("max: " + GenericUtils.max(Arrays.asList(1, 17, 3), Integer.class));
		System.out.println("max: " + GenericUtils.max(Arrays.asList(1, 2, 3, 22, 7, 88), Integer.class));
	}
}
