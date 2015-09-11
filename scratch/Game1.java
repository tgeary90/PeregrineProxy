import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class Game1 implements Serializable
{
//	private enum PlayerType
//	{
//		FIGHTER,
//		WIZARD;
//	}
//	
//	private class Player
//	{
//		private PlayerType type;
//		private String name;
//		private int id;
//		private Player(PlayerType type, String name, int id)
//		{
//			super();
//			this.type = type;
//			this.name = name;
//			this.id = id;
//		}
//		
//	}
	private int a;
	private String b;
	
	
	public int getA()
	{
		return a;
	}


	public String getB()
	{
		return b;
	}


	private Game1(int a, String b)
	{
		super();
		this.a = a;
		this.b = b;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Game1 game = new Game1(1, "game1");
		
		try
		{
			FileOutputStream fs = new FileOutputStream("Game1.ser");
			ObjectOutputStream os = new ObjectOutputStream(fs);
			os.writeObject(game);
			os.close();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		game = null;
		
		try
		{
			ObjectInputStream is = new ObjectInputStream(new FileInputStream("Game1.ser"));
			game = (Game1) is.readObject();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println("a: " + game.getA());
		System.out.println("b: " + game.getB());
		
		File f = new File("Game1.ser");
		f.delete();
	}
}
