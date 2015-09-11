import java.io.File;
import java.io.IOException;


public class File2
{

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		File f = new File("temp.txt");
		if (f.exists())
		{
			//
		}
		else
		{
			// touch file
			f.createNewFile();
		}
	}

}
