package com.hollandjake.messengerBotAPI;

import com.hollandjake.messengerBotAPI.message.Message;
import com.hollandjake.messengerBotAPI.message.MessageThread;
import com.hollandjake.messengerBotAPI.threads.WaitForMessage;
import com.hollandjake.messengerBotAPI.util.Config;
import com.hollandjake.messengerBotAPI.util.WebController;

import java.time.Duration;

public abstract class API extends Thread {
	private final boolean debugging;
	private final MessageThread thread;
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

		//Login to the thread
		webController = new WebController(config, this);
		this.thread = webController.getThread();
		//Load this system

		if (debugging) {
			System.out.println("System is running");
		}

		if (Boolean.valueOf(config.getProperty("startup_message"))) {
//			webController.sendMessage();
		}


		running = true;

		//Create threads
		Runtime.getRuntime().addShutdownHook(new Thread(() -> webController.quit()));

		Thread.setDefaultUncaughtExceptionHandler((thread, e) -> errorHandler(e));

		//waiting for messages
		new WaitForMessage(this, webController).start();
	}

	public void errorHandler(Throwable e) {
		e.printStackTrace();
		System.exit(1);
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

	public MessageThread getThread() {
		return thread;
	}

	public boolean isRunning() {
		return running;
	}
}
