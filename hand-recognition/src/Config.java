import java.io.*;
import java.util.Properties;

public class Config {
	static Properties p;
	static{
		try{
			InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties");
			load(in);
			in.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	protected static void load (InputStream in){
		p = new Properties();
		try{
			p.load(in);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String getConfig(String key){
		return p.getProperty(key);
	}
}
