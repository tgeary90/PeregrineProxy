import java.io.File;


public class File1
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// . represents root of classpath
		File dir = new File(".");
		if (dir.isDirectory())
		{
			String[] contents = dir.list();
			for (String s : contents)
			{
				System.out.println(s);
			}
		}
	}

}
