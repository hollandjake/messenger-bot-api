package com.hollandjake.messenger_bot_api.threads;

import com.hollandjake.messenger_bot_api.API;
import com.hollandjake.messenger_bot_api.util.Config;
import com.hollandjake.messenger_bot_api.util.WebController;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

public class PageReload extends Thread {
	private final API api;
	private final WebController webController;
	private LocalTime time = LocalTime.of(5, 0, 0);

	public PageReload(API api, WebController webController) {
		this.api = api;
		this.webController = webController;
		Thread.setDefaultUncaughtExceptionHandler((thread, e) -> api.errorHandler(e));

		Config config = api.getConfig();
		if (config.hasProperty("reload_time")) {
			String reloadTime = (String) api.getConfig().get("reload_time");
			try {
				this.time = LocalTime.parse(reloadTime);
			} catch (DateTimeParseException ignored) {
			}
		}

	}

	@Override
	public void run() {
		while (api.isRunning()) {
			LocalDateTime start = LocalDateTime.now();
			LocalDateTime fin = LocalDateTime.now().with(time);
			if (!fin.isAfter(start)) {
				fin = fin.plusDays(1);
			}
			long diff = fin.toEpochSecond(ZoneOffset.UTC) - start.toEpochSecond(ZoneOffset.UTC);
			try {
				Thread.sleep(diff * 1000);
				webController.reload();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
