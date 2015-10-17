package de.galan.commons.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.util.ReflectionUtil;


/**
 * Facade for the logging-framework (currently Log4j2). Uses the PayloadMessage for parameterization of messages.
 *
 * @author galan
 */
public class Say {

	// Using ReflectionUtil directly, "2" is taken from log4j2 LogManager.getLogger(), adding 1 for determineLogger() and adding 1 for log(..)
	private static final int THREAD_TYPE_DEEP = 2 + 1 + 1;

	private static boolean includeIdentifier = false;


	// potential improvements:
	// - Integrate Rethrow into Say
	// - Say returns generated message

	/**
	 * Determines the class and the appropiate logger of the calling class.
	 *
	 * @param extraDepth TODO
	 *
	 * @return The logger for the caller
	 */
	static Logger determineLogger() { // (String callerClassName)
		return LogManager.getLogger(ReflectionUtil.getCallerClass(THREAD_TYPE_DEEP), PayloadMessageFactory.INSTANCE);
	}


	protected static PayloadMessage payload(final Object message, final Object[] arguments, Throwable throwable) {
		return new PayloadMessage(message == null ? null : message.toString(), arguments, includeIdentifier, throwable);
	}

	/** Using fluent interface to construct ThreadContext informations (formerly known as MDC /NDC) */
	public static class LogBuilder {

		public LogBuilder f(String key, Object value) {
			return field(key, value);
		}


		public LogBuilder field(String key, Object value) {
			if (value != null) {
				ThreadContext.put(key, value.toString());
			}
			return this;
		}


		public void info(Object message) {
			log(Level.INFO, message, null, (Object[])null);
			ThreadContext.clearMap();
		}

	}

	private static LogBuilder builder = new LogBuilder();


	public static LogBuilder f(String key, Object value) {
		return builder.f(key, value);
	}


	public static LogBuilder field(String key, Object value) {
		return builder.f(key, value);
	}


	protected static void log(Level level, Object message, Throwable throwable, Object... args) {
		PayloadMessage payload = payload(message, args, throwable);
		determineLogger().log(level, payload, payload.getThrowable());
	}


	// -------------------------------- TRACE --------------------------------

	public static void trace(Object message) {
		log(Level.TRACE, message, null, (Object[])null);
	}


	public static void trace(Object message, Object... args) {
		log(Level.TRACE, message, null, args);
	}


	public static void trace(Object message, Throwable throwable) {
		log(Level.TRACE, message, throwable, (Object[])null);
	}


	public static void trace(Object message, Throwable throwable, Object... args) {
		log(Level.TRACE, message, throwable, args);
	}

	/* TODO rethrow
	public static <T extends Throwable> T traceThrows(T throwable) throws T {
		//PayloadMessage payload = payload(message, null, throwable);
		throw determineLogger(0).throwing(Level.ERROR, throwable);
	}
	*/


	// -------------------------------- DEBUG --------------------------------

	public static void debug(Object message) {
		log(Level.DEBUG, message, null, (Object[])null);
	}


	public static void debug(Object message, Object... args) {
		log(Level.DEBUG, message, null, args);
	}


	public static void debug(Object message, Throwable throwable) {
		log(Level.DEBUG, message, throwable, (Object[])null);
	}


	public static void debug(Object message, Throwable throwable, Object... args) {
		log(Level.DEBUG, message, throwable, args);
	}


	// -------------------------------- INFO --------------------------------

	public static void info(Object message) {
		log(Level.INFO, message, null, (Object[])null);
	}


	public static void info(Object message, Object... args) {
		log(Level.INFO, message, null, args);
	}


	public static void info(Object message, Throwable throwable) {
		log(Level.INFO, message, throwable, (Object[])null);
	}


	public static void info(Object message, Throwable throwable, Object... args) {
		log(Level.INFO, message, throwable, args);
	}


	// -------------------------------- WARN --------------------------------

	public static void warn(Object message) {
		log(Level.WARN, message, null, (Object[])null);
	}


	public static void warn(Object message, Object... args) {
		log(Level.WARN, message, null, args);
	}


	public static void warn(Object message, Throwable throwable) {
		log(Level.WARN, message, throwable, (Object[])null);
	}


	public static void warn(Object message, Throwable throwable, Object... args) {
		log(Level.WARN, message, throwable, args);
	}


	// -------------------------------- ERROR --------------------------------

	public static void error(Object message) {
		log(Level.ERROR, message, null, (Object[])null);
	}


	public static void error(Object message, Object... args) {
		log(Level.ERROR, message, null, args);
	}


	public static void error(Object message, Throwable throwable) {
		log(Level.ERROR, message, throwable, (Object[])null);
	}


	public static void error(Object message, Throwable throwable, Object... args) {
		log(Level.ERROR, message, throwable, args);
	}


	// -------------------------------- FATAL --------------------------------

	public static void fatal(Object message) {
		log(Level.FATAL, message, null, (Object[])null);
	}


	public static void fatal(Object message, Object... args) {
		log(Level.FATAL, message, null, args);
	}


	public static void fatal(Object message, Throwable throwable) {
		log(Level.FATAL, message, throwable, (Object[])null);
	}


	public static void fatal(Object message, Throwable throwable, Object... args) {
		log(Level.FATAL, message, throwable, args);
	}

	// -------------------------------- MISC --------------------------------

	/*
	public static Logger/checkstyle/ getLogger() {
		return determineLogger();
	}
	*/


	public static void please() {
		log(Level.INFO,
			"\u0059\u006F\u0075\u0027\u0072\u0065\u0020\u0077\u0065\u006C\u0063\u006F\u006D\u0065\u0020" + System.getProperty("user.name") + "\u0021", null,
			(Object[])null);
	}

	// Old stuff

	/* A custom security manager that exposes the getClassContext() information */
	/*
	static class CallerSecurityManager extends SecurityManager {
	
		public String getCallerClassName(int callStackDepth) {
			return getClassContext()[callStackDepth].getName();
		}
	}
	 */

	/* Cache of the dynamically created loggers. Multithreading considerations: a simple HashMap is sufficient. */
	//private static Map<String, Logger> logger = new HashMap<String, Logger>();

	/*
	 * Using a Custom SecurityManager to get the caller classname. Using the old reflection approach had some drawbacks:<br/>
	 * 1. From Java7u10 to Javau11 the THREAD_TYPE_DEEP differs (+1) due to internal Java changes.<br/>
	 * 2. From Java7u40 onwards the method is only supported if "-Djdk.reflect.allowGetCallerClass" is set on start<br/>
	 * 3. From Java8 on the method is removed<br/>
	 * <br/>
	 * See also http://www.infoq.com/news/2013/07/Oracle-Removes-getCallerClass
	 */
	//private static final CallerSecurityManager CSM = new CallerSecurityManager();

	/*
	 * Determines the calling class (and method/linenumber too)
	 *
	 * @return A StrackTraceElement, which contains the Classname of the caller, and (if allwissend is integrated) the
	 *         method and linenumber, too.
	 */
	/*
	static String determineCaller() {
		return CSM.getCallerClassName(THREAD_TYPE_DEEP);
		//@SuppressWarnings("restriction")
		//String className = sun.reflect.Reflection.getCallerClass(THREAD_TYPE_DEEP).getName();
		//return new StackTraceElement(className, "", null, 0);
	}
	 */

	/*
	 * Determines the class and the appropiate logger of the calling class.
	 *
	 * @return The logger for the caller
	 */
	/*
	//static Logger determineLogger(StackTraceElement caller) {
	//static Logger determineLogger() { // (String callerClassName)
		//String className = caller.getClassName();
		String className = callerClassName;
		Logger result = logger.get(className);
		if (result == null) {
			logger.put(className, LoggerFactory.getLogger(className));
			result = logger.get(className);
			// plain log4j: logger.put(className, Logger.getLogger(stackTraceElements[THREAD_TYPE_DEEP].getClassName());
		}
		return result;
	}
	 */

}
