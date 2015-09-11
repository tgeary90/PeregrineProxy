
public class Regex1
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String sample = "I\nlove\n\tsho es\t\n\ndo you like them? too?";
		String[] toks = sample.split("\\s+");
		System.out.println(sample);
		for (String s : toks)
		{
			System.out.println(s);
		}
		
		String s2 = "I do like the sound of typing. Do you like it?";
		String[] toks2 = s2.split("\\W+");
		for (String s : toks2)
		{
			//System.out.println(s);
		}
	}
}
