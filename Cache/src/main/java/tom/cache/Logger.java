package tom.cache;

public interface Logger 
{
	public void	start();
	public void stop();
	public void log(String entry);
}