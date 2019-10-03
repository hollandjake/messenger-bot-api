package com.hollandjake.messenger_bot_api;

import com.google.errorprone.annotations.ForOverride;
import com.hollandjake.messenger_bot_api.message.Human;
import com.hollandjake.messenger_bot_api.message.Message;
import com.hollandjake.messenger_bot_api.message.MessageThread;
import com.hollandjake.messenger_bot_api.threads.PageReload;
import com.hollandjake.messenger_bot_api.threads.WaitForMessage;
import com.hollandjake.messenger_bot_api.util.Config;
import com.hollandjake.messenger_bot_api.util.DatabaseController;
import com.hollandjake.messenger_bot_api.util.LOG_LEVEL;
import com.hollandjake.messenger_bot_api.util.WebController;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;

public abstract class API extends Thread {
	protected final Config config;
	protected final LOG_LEVEL logLevel;
	protected final Human me;
	protected final WebController webController;
	protected final DatabaseController db;
	private boolean running;
	private int refreshRate;
	private Duration messageTimeout;

	public API(Config config) throws SQLException {
		this.config = config;
		messageTimeout = Duration.ofMillis(Long.parseLong(config.getProperty("message_timeout")));
		refreshRate = (int) config.get("refresh_rate");
		logLevel = (LOG_LEVEL) config.get("log_level");

		//Login to the thread
		db = new DatabaseController(config, this);
		webController = new WebController(config, this);

		me = webController.getMe();
		//Load this system

		if (LOG_LEVEL.DEBUG.lessThanEqTo(logLevel)) {
			System.out.println("System is running");
		}
		running = true;

		//Create threads
		Thread.setDefaultUncaughtExceptionHandler((thread, e) -> errorHandler(this, e));

		//waiting for messages
		new WaitForMessage(this, webController).start();
		new PageReload(this, webController).start();
		loaded(db.getConnection());
	}

	public void errorHandler(Object sender, Throwable e) {
		e.printStackTrace();
		if (sender instanceof WaitForMessage) {
			System.out.println("Restarting WaitForMessage Thread");
			new WaitForMessage(this, webController).start();
		} else if (sender instanceof PageReload) {
			System.out.println("Restarting PageReload Thread");
			new PageReload(this, webController).start();
		} else {
			quit();
		}
	}

	public void quit() {
		if (webController != null) {
			webController.quit(true);
		}
		System.exit(0);
	}

	public void checkDbConnection() {
		db.checkConnection();
	}

	@ForOverride
	public void newMessage(Message message) {
	}

	@ForOverride
	public void databaseReload(Connection connection) throws SQLException {
	}

	@ForOverride
	public void loaded(Connection connection) throws SQLException {
	}

	public void sendMessage(Message message) {
		webController.sendMessage(message);
	}

	public void sendMessage(String message) {
		webController.sendMessage(Message.fromString(db.getThread(), me, message));
	}

	public boolean debugging() {
		return logLevel.greaterThanEqTo(LOG_LEVEL.DEBUG) || (java.lang.management.ManagementFactory.getRuntimeMXBean().
				getInputArguments().toString().indexOf("-agentlib:jdwp") > 0);
	}

	public Config getConfig() {
		return config;
	}

	public DatabaseController getDb() {
		return db;
	}

	public LOG_LEVEL getLogLevel() {
		return logLevel;
	}

	public Human getMe() {
		return me;
	}

	public Duration getMessageTimeout() {
		return messageTimeout;
	}

	public int getRefreshRate() {
		return refreshRate;
	}

	public MessageThread getThread() {
		return db.getThread();
	}

	@ForOverride
	public String getVersion() {
		try {
			return new MavenXpp3Reader().read(
					new FileReader(new File("pom.xml"))
			).getVersion();
		} catch (XmlPullParserException | IOException ignore) {
			return "";
		}
	}

	public boolean isRunning() {
		return running;
	}
}
