import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Settings {

	private static Properties config;

	static {
		config = new Properties();
		try {
			FileInputStream fis = new FileInputStream("config.properties");
			config.load(fis);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}

	public final static int nodeSize = Integer.parseInt(config.getProperty("nodeSize"));

	public final static int  initialWidth = Integer.parseInt(config.getProperty("initialWidth"));
	public final static int initialHeight = Integer.parseInt(config.getProperty("initialHeight"));

	public final static int timerDelay = Integer.parseInt(config.getProperty("timerDelay"));
	public final static boolean immediateRun = Boolean.parseBoolean(config.getProperty("immediateRun"));

	public final static boolean diagonalAllowed = Boolean.parseBoolean(config.getProperty("diagonalAllowed"));
	public final static double heuristicWeighting = Double.parseDouble(config.getProperty("heuristicWeighting"));
	public final static double diagonalCost = Math.sqrt(2);
}
