package com.lukasdietrich.logr;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Logger {

	private static Level globalLevel = Level.INFO;
	private static Map<Class<?>, Logger> instance = new HashMap<>();
	
	/**
	 * Sets the global log level to be displayed.
	 * 
	 * @param level
	 */
	public static void setLogLevel(Level level) {
		globalLevel = level;
	}
	
	/**
	 * Returns the anonymous {@link Logger} instance
	 * 
	 * @return a {@link Logger} instance
	 */
	public static Logger get() {
		return get(null);
	}
	
	/**
	 * Returns a {@link Logger} in the context of a given object.<br>
	 * <em>This is only a shorthand for {@link #get(Class)} with <code>context.getClass()</code></em>
	 * 
	 * @param context
	 * @return a {@link Logger} instance
	 */
	public static Logger get(Object context) {
		return get(context.getClass());
	}
	
	/**
	 * Returns a {@link Logger} in the context of a given class.
	 * 
	 * @param context
	 * @return a {@link Logger} instance
	 */
	public synchronized static Logger get(Class<?> context) {
		return instance.computeIfAbsent(context, c -> new Logger(c));
	}
	
	private final DateTimeFormatter FORMAT;
	private final String CONTEXT;
	
	private Logger(Class<?> context) {
		FORMAT = DateTimeFormatter.ofPattern("dd-MM-YYYY HH:mm:ss.SSS");
		CONTEXT = context == null ? "-" : context.getSimpleName();
	}
	
	/**
	 * Logs a formatted message on given level.
	 * 
	 * @param level
	 * @param format
	 * @param args
	 */
	public void log(Level level, String format, Object... args) {
		if (level.isWithin(globalLevel))
			level.printer.printf("[%s][%s][%s] %s%s", 
				level.toString(),
				LocalDateTime.now().format(FORMAT), 
				CONTEXT,
				String.format(format, args),
				System.lineSeparator()
			);
	}
	
	/**
	 * Shorthand for {@link Logger#log(Level.INFO, String, Object...)}
	 * 
	 * @param format
	 * @param args
	 */
	public void info(String format, Object... args) {
		log(Level.INFO, format, args);
	}
	
	/**
	 * Shorthand for {@link Logger#log(Level.WARN, String, Object...)}
	 * 
	 * @param format
	 * @param args
	 */
	public void warn(String format, Object... args) {
		log(Level.WARN, format, args);
	}
	
	/**
	 * Shorthand for {@link Logger#log(Level.ERROR, String, Object...)}
	 * 
	 * @param format
	 * @param args
	 */
	public void err(String format, Object... args) {
		log(Level.ERROR, format, args);
	}
	
	/**
	 * Shorthand for {@link #err(String, Object...)}
	 * 
	 * @param e
	 */
	public void err(Exception e) {
		err(e.getLocalizedMessage());
	}
	
	/**
	 * Shorthand for {@link #err(String, Object...)}
	 * 
	 * @param e
	 */
	public void errTrace(Exception e) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(buffer));
		err("%s: %s", e.getClass().getSimpleName(), buffer.toString());
	}
	
	public static enum Level {
		INFO(System.out), 
		WARN(System.out), 
		ERROR(System.err);
		
		private PrintStream printer;
		
		private Level(PrintStream printer) {
			this.printer = printer;
		}
		
		public boolean isWithin(Level level) {
			return ordinal() >= level.ordinal();
		}
		
	}
	
}
