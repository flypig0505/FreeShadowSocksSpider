package tianma.ss.spider.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log utility.
 * @author Tianma
 *
 */
public class TLog {

	private static Logger sLogger;
	
	private TLog() {
		
	}
	
	public static void init() {
		if (sLogger == null)
			initLogger();
	}
	
	private static void initLogger() {
		sLogger = LoggerFactory.getLogger("ShadowSocksSpiderLogger");
	}
	
	public static void v(String msg) {
		sLogger.trace(msg);
	}
	
	public static void d(String msg) {
		sLogger.debug(msg);
	}
	
	public static void i(String msg) {
		sLogger.info(msg);
	}
	
	public static void w(String msg) {
		sLogger.warn(msg);
	}
	
	public static void e(String msg) {
		sLogger.error(msg);
	}
	
	public static void e(Throwable throwable) {
		sLogger.error("", throwable);
	}
	
	public static void e(String msg, Throwable throwable) {
		sLogger.error(msg, throwable);
	}
	
}
