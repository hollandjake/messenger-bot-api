package bot.threads;

import bot.MBotAPI;
import bot.utils.WebController;
import bot.utils.message.Message;

public class WaitForMessage extends Thread {
	private final MBotAPI api;
	private final WebController webController;

	public WaitForMessage(MBotAPI api, WebController webController) {
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
