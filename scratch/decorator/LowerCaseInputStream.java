package decorator;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LowerCaseInputStream extends FilterInputStream
{

	public LowerCaseInputStream(InputStream in)
	{
		super(in);
	}

	@Override
	public int read() throws IOException
	{
		int c = in.read();
		// Character.toLowerCase returns a char but due to 'Implicit widening' (coercing)
		// an int is returned.
		return (c == -1 ? c : Character.toLowerCase( (char) c));
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		int readBytes = in.read(b, off, len);
		for (int i = off; i < readBytes + off; i++)
		{
			b[i] = (byte) Character.toLowerCase(b[i]);
		}
		return readBytes;
	}

	@Override
	public int read(final byte[] b) throws IOException
	{
		int readBytes = in.read(b);
		for (int i = 0; i < readBytes; i++)
		{
			b[i] = (byte) Character.toLowerCase((char) b[i]);
		}
		return readBytes;
	}

}
