package bot;

import bot.threads.WaitForMessage;
import bot.utils.Config;
import bot.utils.WebController;
import bot.utils.message.Message;

import java.lang.reflect.MalformedParametersException;
import java.time.Duration;

public abstract class API extends Thread {
	private final boolean debugging;
	protected Config config;
	private WebController webController;
	private boolean running = false;
	private long refreshRate;
	private Duration messageTimeout;

	public API(Config config) {
		this.config = config;
		this.debugging = Boolean.valueOf(config.getProperty("debug"));
		messageTimeout = Duration.ofMillis(Long.valueOf(config.getProperty("message_timeout")));
		refreshRate = Long.valueOf(config.getProperty("refresh_rate"));

		webController = new WebController(config, this);

		//Login to the thread
		if (config.hasProperty("email") && config.hasProperty("password") && config.hasProperty("thread_id")) {
			webController.openThread(config.getProperty("email"), config.getProperty("password"), config.getProperty("thread_id"));
		} else {
			throw new MalformedParametersException("'email', 'password', 'thread_id' properties are required");
		}
		//Load this system

		if (debugging) {
			System.out.println("System is running");
		}

		if (Boolean.valueOf(config.getProperty("startup_message"))) {
//			webController.sendMessage();
		}


		running = true;
		//Create threads

		//waiting for messages
		new WaitForMessage(this, webController).start();
	}

	public abstract void newMessage(Message message);

	public void sendMessage(Message message) {
		webController.sendMessage(message);
	}

	public Config getConfig() {
		return config;
	}

	public Duration getMessageTimeout() {
		return messageTimeout;
	}

	public long getRefreshRate() {
		return refreshRate;
	}

	public boolean isRunning() {
		return running;
	}
}
