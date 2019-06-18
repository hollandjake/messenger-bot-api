package com.hollandjake.messengerBotAPI.threads;

import com.hollandjake.messengerBotAPI.API;
import com.hollandjake.messengerBotAPI.message.Message;
import com.hollandjake.messengerBotAPI.util.WebController;

import java.util.List;

public class WaitForMessage extends Thread {
	private final API api;
	private final WebController webController;

	public WaitForMessage(API api, WebController webController) {
		this.api = api;
		this.webController = webController;
		Thread.setDefaultUncaughtExceptionHandler((thread, e) -> api.errorHandler(e));
	}

	@Override
	public void run() {
		while (api.isRunning()) {
			List<Message> messages = webController.waitForMessage();
			for (Message message : messages) {
				if (Boolean.valueOf(api.getConfig().getProperty("echo"))) {
					webController.sendMessage(message);
				}
				api.newMessage(message);
			}
		}
	}
}
