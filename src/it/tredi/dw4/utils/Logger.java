package it.tredi.dw4.utils;

import javax.faces.context.FacesContext;

public class Logger {
	public final static String ACL_LOGGER_NAME = "ACL";
	public final static String DOCWAY_LOGGER_NAME = "DOCWAY";
	
	private org.apache.log4j.Logger aclLogger;
	private org.apache.log4j.Logger docwayLogger;
	
	private static class SingletonHolder { 
        public static final Logger instance = new Logger();
	}

	public static Logger getInstance() {
	        return SingletonHolder.instance;
	}
	
	public static void debug(String message) {
		Logger.getInstance()._debug(message);
	}
	
	public static void info(String message) {
		Logger.getInstance()._info(message);
	}
	
	public static void warn(String message) {
		Logger.getInstance()._warn(message);
	}
	
	public static void error(String message) {
		Logger.getInstance()._error(message);
	}
	
	public static void fatal(String message) {
		Logger.getInstance()._fatal(message);
	}
	
	public static void error(String message, Throwable t) {
		Logger.getInstance()._error(message, t);
	}	
	
	public static void debug(String loggerName, String message) {
		Logger.getInstance()._debug(loggerName, message);
	}
	
	public static void info(String loggerName, String message) {
		Logger.getInstance()._info(loggerName, message);
	}
	
	public static void warn(String loggerName, String message) {
		Logger.getInstance()._warn(loggerName, message);
	}
	
	public static void error(String loggerName, String message) {
		Logger.getInstance()._error(loggerName, message);
	}
	
	public static void fatal(String loggerName, String message) {
		Logger.getInstance()._fatal(loggerName, message);
	}
	
	public static void error(String loggerName, String message, Throwable t) {
		Logger.getInstance()._error(loggerName, message, t);
	}	
	
	private Logger() {
		this.aclLogger = org.apache.log4j.Logger.getLogger(Logger.ACL_LOGGER_NAME);
		this.docwayLogger = org.apache.log4j.Logger.getLogger(Logger.DOCWAY_LOGGER_NAME);
	}
	
	private String guessLoggerName() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		if (ctx != null && ctx.getExternalContext() != null && ctx.getExternalContext().getRequestServletPath() != null) {
			String name = ctx.getExternalContext().getRequestServletPath();
			if (name.contains(Const.ACL_CONTEXT_NAME)) {
				return Logger.ACL_LOGGER_NAME;
			}
			else if (name.contains(Const.DOCWAY_CONTEXT_NAME)) {
				return Logger.DOCWAY_LOGGER_NAME;
			}
		}
		
		// in caso di problemi: 'DocWay'?
		return Logger.DOCWAY_LOGGER_NAME;
	}
	
	void _debug(String message) {
		String loggerName = guessLoggerName();
		this._debug(loggerName, message);
	}
	
	void _info(String message) {
		String loggerName = guessLoggerName();
		this._info(loggerName, message);
	}
	
	void _warn(String message) {
		String loggerName = guessLoggerName();
		this._warn(loggerName, message);
	}
	
	void _error(String message) {
		String loggerName = guessLoggerName();
		this._error(loggerName, message);
	}
	
	void _error(String message, Throwable t) {
		String loggerName = guessLoggerName();
		this._error(loggerName, message, t);
	}	
	
	void _fatal(String message) {
		String loggerName = guessLoggerName();
		this._fatal(loggerName, message);
	}
	
	void _debug(String loggerName, String message) {
		if (loggerName.equals(Logger.ACL_LOGGER_NAME) && this.aclLogger.isDebugEnabled())
			this.aclLogger.debug(message);
		else if (this.docwayLogger.isDebugEnabled())
			this.docwayLogger.debug(message);
	}
	
	void _info(String loggerName, String message) {
		if (loggerName.equals(Logger.ACL_LOGGER_NAME) && this.aclLogger.isInfoEnabled())
			this.aclLogger.info(message);
		else if (this.docwayLogger.isInfoEnabled())
			this.docwayLogger.info(message);
	}
	
	void _warn(String loggerName, String message) {
		if (loggerName.equals(Logger.ACL_LOGGER_NAME))
			this.aclLogger.warn(message);
		else
			this.docwayLogger.warn(message);
	}
	
	void _error(String loggerName, String message) {
		if (loggerName.equals(Logger.ACL_LOGGER_NAME))
			this.aclLogger.error(message);
		else
			this.docwayLogger.error(message);
	}
	
	void _error(String loggerName, String message, Throwable t) {
		if (loggerName.equals(Logger.ACL_LOGGER_NAME))
			this.aclLogger.error(message, t);
		else
			this.docwayLogger.error(message, t);
	}	
	
	void _fatal(String loggerName, String message) {
		if (loggerName.equals(Logger.ACL_LOGGER_NAME))
			this.aclLogger.fatal(message);
		else
			this.docwayLogger.fatal(message);
	}
	
}
