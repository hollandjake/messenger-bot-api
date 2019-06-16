package com.hollandjake.messengerBotAPI.threads;

import com.hollandjake.messengerBotAPI.API;
import com.hollandjake.messengerBotAPI.message.Message;
import com.hollandjake.messengerBotAPI.util.WebController;

public class WaitForMessage extends Thread {
	private final API api;
	private final WebController webController;

	public WaitForMessage(API api, WebController webController) {
		this.api = api;
		this.webController = webController;
	}

	@Override
	public void run() {
		while (api.isRunning()) {
			Message message = webController.waitForMessage();
			if (Boolean.valueOf(api.getConfig().getProperty("echo"))) {
				webController.sendMessage(message);
			}
			api.newMessage(message);
		}
	}
}
