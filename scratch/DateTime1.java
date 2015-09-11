import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateTime1
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
//		Date now = new Date();
//		SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
//		System.out.println("Current time: " + formatter.format(now));
//		
//		SimpleDateFormat sf = new SimpleDateFormat("ss.SSS");
//		long start = System.currentTimeMillis();
//		Thread.sleep(1000 * 1); // 10s
//		long end = System.currentTimeMillis();
//		long diff = end - start;
//		//Date diffDate = new Date(diff);
//		System.out.println("Elapsed time: " + sf.format(diff));
		
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("E dd-MM-yy hh:mm:ss.SSS");
		System.out.println("The time now is: " + formatter.format(now));
		
		Calendar cal = Calendar.getInstance();
		
		// 10 minutes time
		cal.add(cal.MINUTE,  10);
		System.out.println("In 10 mins the time will be: " + formatter.format(cal.getTime()));
		
		formatter = new SimpleDateFormat("E dd-MM-yyyy");
		cal.add(cal.DAY_OF_MONTH, 1);
		System.out.println("Tomorrow the date is: " + formatter.format(cal.getTime()));
		
		cal.set(2015, 7, 10, 9, 16);
		formatter = new SimpleDateFormat("dd MM yyyy");
		System.out.println("Yesterday was: " + formatter.format(cal.getTime()));
	}
}
