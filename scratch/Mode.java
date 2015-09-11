
public enum Mode
{
	FORWARD("forwarding", 10),
	REVERSE("reverse", 20);
	
	private String strRepr;
	private int intRepr;
	
	public String getStrRepr()
	{
		return strRepr;
	}

	public int getIntRepr()
	{
		return intRepr;
	}

	private Mode(String strRepr, int intRepr)
	{
		this.strRepr = strRepr;
		this.intRepr = intRepr;
	}
	
	/**
	 * Static Factory Method
	 * @param modeStr
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static Mode fromString(String modeStr) throws IllegalArgumentException
	{
		return Enum.valueOf(Mode.class, modeStr);
	}
}
