package tom.peregrine.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/** 
 * class to manage the process of loading plugins at runtime
 * @author tom
 *
 */
public class PluginLoader
{
	//private static final String PLUGIN_JAR = "/CacheBypassPlugins-1.0-SNAPSHOT.jar";
	private static final String PLUGIN_JAR = "/home/tom/.m2/repository/tom/msc/peregrine/CacheBypassPlugins/1.0-SNAPSHOT/CacheBypassPlugins-1.0-SNAPSHOT.jar";
	private static final String PLUGIN_DESC_FILE = "/plugin_descriptors";
	
	private static Logger s_logger = Logger.getLogger(PluginLoader.class); 
	
	public List<Plugin> loadPlugins() 
	{
		s_logger.debug("plugin jar: " + PLUGIN_JAR);
		s_logger.debug("descriptor file: " + PLUGIN_DESC_FILE);
		List<Plugin> plugins = new ArrayList<Plugin>();
		InputStream is = null;
		BufferedReader buf = null;
		try
		{
			//is = new FileInputStream(PLUGIN_DESC_FILE);
			is = getClass().getResourceAsStream(PLUGIN_DESC_FILE);
			buf = new BufferedReader(new InputStreamReader(is));
			
			String descriptor;
			while ((descriptor = buf.readLine()) != null)
			{
				try
				{
					plugins.add(loadPlugin(descriptor));
				}
				catch (ClassNotFoundException cnfe)
				{
					s_logger.fatal("couldnt load raw class files from plugins jar");
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
			s_logger.fatal("Could not read plugin descriptors");
		}
		finally
		{
			try
			{
				buf.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return plugins;
	}

	private Plugin loadPlugin(String descriptor) throws ClassNotFoundException, MalformedURLException, IllegalAccessException, InstantiationException
	{
		File file = new File(PLUGIN_JAR);
		URL url = file.toURI().toURL();
		URL[] urls = new URL[] {url};
		
		ClassLoader loader = new URLClassLoader(urls);
		Class cls = loader.loadClass(descriptor);
		Plugin plugin = (Plugin) cls.newInstance();
		plugin.setName(descriptor);
		s_logger.info("loaded " + plugin.getName());
		return plugin;
	}
}
