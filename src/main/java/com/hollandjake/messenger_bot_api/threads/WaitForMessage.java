package com.hollandjake.messenger_bot_api.threads;

import com.hollandjake.messenger_bot_api.API;
import com.hollandjake.messenger_bot_api.message.Message;
import com.hollandjake.messenger_bot_api.util.LOG_LEVEL;
import com.hollandjake.messenger_bot_api.util.WebController;

import java.util.List;

public class WaitForMessage extends Thread {
	private final API api;
	private final WebController webController;
	private final Boolean echo;

	public WaitForMessage(API api, WebController webController) {
		this.api = api;
		this.webController = webController;
		Thread.setDefaultUncaughtExceptionHandler((thread, e) -> api.errorHandler(e));
		echo = (Boolean) api.getConfig().get("echo");
	}

	@Override
	public void run() {
		while (api.isRunning()) {
			List<Message> messages = webController.waitForMessage();
			for (Message message : messages) {
				if (api.getLogLevel().greaterThanEqTo(LOG_LEVEL.DEBUG_MESSAGES)) {
					message.print();
				}
				if (echo) {
					webController.sendMessage(message);
				}
				api.newMessage(message);
			}
		}
	}
}
