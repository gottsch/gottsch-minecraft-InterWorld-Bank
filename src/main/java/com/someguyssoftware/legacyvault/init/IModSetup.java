package com.someguyssoftware.legacyvault.init;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * TODO move to  GottschCore
 * @author Mark Gottschling on Apr 28, 2021
 *
 */
public interface IModSetup {
	/**
	 * TODO need the ILoggerConfig or just IConfig
	 * @param modName
	 * @param object
	 */
	public static void addRollingFileAppender(String modName, Object object) {

		String appenderName = modName + "Appender";
		String loggerFolder = "logs/legacyvault/";
		if (!loggerFolder.endsWith("/")) {
			loggerFolder += "/";
		}

		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();

		// create a sized-based trigger policy, using config setting for size.
		SizeBasedTriggeringPolicy policy = SizeBasedTriggeringPolicy.createPolicy(/*modConfig.getLoggerSize()*/"1000K");
		// create the pattern for log statements
		PatternLayout layout = PatternLayout.newBuilder().withPattern("%d [%t] %p %c | %F:%L | %m%n")
				.withAlwaysWriteExceptions(true).build();

		// create a rolling file appender
		Appender appender = RollingFileAppender.newBuilder()
				.withFileName(loggerFolder + /*modConfig.getLoggerFilename()*/"legacyvault" + ".log")
				.withFilePattern(loggerFolder + /*modConfig.getLoggerFilename()*/"legacyvault" + "-%d{yyyy-MM-dd-HH_mm_ss}.log")
				.withAppend(true).setName(appenderName).withBufferedIo(true).withImmediateFlush(true)
				.withPolicy(policy)
				.setLayout(layout)
				.setIgnoreExceptions(true).withAdvertise(false).setConfiguration(config).build();

		appender.start();
		config.addAppender(appender);
		
		// create a appender reference
		AppenderRef ref = AppenderRef.createAppenderRef("File", null, null);
		AppenderRef[] refs = new AppenderRef[] {ref};
		
		LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.DEBUG, modName, "true", refs, null, config, null );
		loggerConfig.addAppender(appender, null, null);
		config.addLogger(modName, loggerConfig);
		
		// update logger with new appenders
		ctx.updateLoggers();
	}

}