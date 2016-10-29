package tianma.ss.spider.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log utility.
 * 
 * @author Tianma
 *
 */
public class TLog {

	private static Logger sLogger;

	private TLog() {

	}

	/**
	 * Init org.slf4j.Logger.
	 * <p>
	 * You must call it before calling other method.
	 */
	public static void init() {
		if (sLogger == null)
			initLogger();
	}

	/**
	 * Create the instance of org.slf4j.Logger.
	 */
	private static void initLogger() {
		sLogger = LoggerFactory.getLogger("ShadowSocksSpiderLogger");
	}

	public static void v(String msg) {
		sLogger.trace(msg);
	}

	/**
	 * Log the message in debug level.
	 * 
	 * @param msg
	 */
	public static void d(String msg) {
		sLogger.debug(msg);
	}

	/**
	 * Log the message in info level.
	 * 
	 * @param msg
	 */
	public static void i(String msg) {
		sLogger.info(msg);
	}

	/**
	 * Log the message in warn level.
	 * 
	 * @param msg
	 */
	public static void w(String msg) {
		sLogger.warn(msg);
	}

	/**
	 * Log the message in error level.
	 * 
	 * @param msg
	 */
	public static void e(String msg) {
		sLogger.error(msg);
	}

	/**
	 * Log the throwable in error level. It can print the full-stack message of
	 * the throwable object.
	 * 
	 * @param throwable
	 */
	public static void e(Throwable throwable) {
		sLogger.error("", throwable);
	}

	/**
	 * Log the throwable with specific message.
	 * @param msg
	 * @param throwable
	 */
	public static void e(String msg, Throwable throwable) {
		sLogger.error(msg, throwable);
	}

}
