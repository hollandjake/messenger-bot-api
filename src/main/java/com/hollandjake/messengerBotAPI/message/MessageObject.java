package com.hollandjake.messengerBotAPI.message;

import com.google.errorprone.annotations.ForOverride;
import com.hollandjake.messengerBotAPI.util.Config;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class MessageObject {

	public abstract String prettyPrint();

	public abstract void send(WebElement inputBox, WebDriverWait wait, MessageComponent nextComponent);

	@ForOverride
	static Object extractFrom(Config config, WebElement messageElement) {
		throw new IllegalArgumentException("Subclass did not declare an overridden extractFrom() method.");
	}
}
