import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * utility class to demonstrate that non generic classes can contain methods
 * that have generic parameters without themselves being generic.
 * @author tom
 *
 */
public class GenericUtils
{
	private GenericUtils() {}
	
	/**
	 * Generic print for Lists. unbounded wildcard allows any list to be passed in.
	 * Cant use List<Object> because then only that type can be passed in.
	 * Useful for Object operations though.
	 * @param list
	 */
	public static void printList(List<?> list)
	{
		for (Object e : list)
			System.out.printf("%s, ", e.toString());
	}
	
	/**
	 * Generic Number summation
	 * @param nums
	 * @return Number the integer sum
	 */
	public static Number integerSum(Collection<? extends Number> nums)
	{
		Integer sum = new Integer(0);
		for (Number n : nums)
		{
			sum += n.intValue();
		}
		return sum;
	}
	
	/**
	 * Return a list of Doubles for a given number collection
	 * @param nums
	 */
	public static void printNumsAsDoubles(Collection<? extends Number> nums)
	{
		for (Number n : nums)
			System.out.printf("%.1f, ", n.doubleValue());
	}
	
	/**
	 * Return an int array of hashcodes for any typed collection passed in
	 * @param c
	 * @return
	 */
	public static int[] hashCodes(Collection<?> c)
	{
		int[] hashes = new int[c.size()];
		Iterator<?> iter = c.iterator();
		for (int i = 0; i < c.size(); i++)
		{
			if (iter.hasNext())
			{
				hashes[i] = iter.next().hashCode();
			}
		}
		return hashes;
	}
	
	/**
	 * DOESNT WORK YET!
	 * @param t
	 * @param cls
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> T create(T t, Class<T> cls) throws InstantiationException, IllegalAccessException
	{
		T obj = (T) cls.newInstance();
		Field[] fields = cls.getFields();
		for (Field f : fields)
			System.out.println(f);
		return obj;
	}
	
	/**
	 * Returns a typed list of the primes within a given collection.
	 * prime: whole numbers with no divisors over 1
	 * @param c
	 * @return list of primes
	 */
	public static <T extends Number> List<T> primesInCollection(Collection<T> c)
	{
		List<T> primes = new ArrayList<T>();
		for (T n : c)
		{
			boolean prime = true;
			for (int i = 2; i < n.intValue(); i++)
			{
				if (n.intValue() % i == 0)
					prime = false;
			}
			if (prime)
				primes.add(n);
		}
		return primes;
	}
	
	/**
	 * Switch the position of two elements in an array
	 * @param array
	 * @param pos
	 * @return
	 */
	public static <E> E[] switchInArray(E[] array, int pos)
	{
		E tmp = array[pos + 1];
		array[pos + 1] = array[pos];
		array[pos] = tmp;
		return array;
	}
	
	public static <T extends Comparable<T>> T max(List<T> list, Class<T> cls) 
	 throws InstantiationException, IllegalAccessException
	{
		T max = list.get(0);
		for (int i = 1; i < list.size(); i++)
		{
			if (list.get(i).compareTo(max) > 0)
				max = list.get(i);
		}
		return max;
	}
}
