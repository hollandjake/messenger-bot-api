package com.hollandjake.messengerBotAPI.message;

import com.hollandjake.messengerBotAPI.util.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.datatransfer.StringSelection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.hollandjake.messengerBotAPI.util.CONSTANTS.CLIPBOT;
import static com.hollandjake.messengerBotAPI.util.XPATHS.MESSAGE_TEXT;
import static org.apache.commons.lang.StringEscapeUtils.unescapeHtml;


public class Text extends MessageComponent {
	private final String text;

	private Text(int id, String text) {
		super(id);
		this.text = text;
	}

	public static Text fromResultSet(Config config, ResultSet resultSet) {
		try {
			return new Text(resultSet.getInt("text_id"), resultSet.getString("text"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Text fromString(String text) {
		return new Text(0, text);
	}

	public static ArrayList<MessageComponent> extractFrom(Config config, WebElement messageElement) {
		ArrayList<MessageComponent> messageComponents = new ArrayList<>();
		List<WebElement> textComponents = messageElement.findElements(By.xpath(MESSAGE_TEXT));
		for (WebElement textComponent : textComponents) {
			messageComponents.add(new Text(0, textComponent.getAttribute("aria-label")));
		}
		return messageComponents;
	}

	@Override
	public String prettyPrint() {
		return "\"" + text + "\"";
	}

	@Override
	public void send(WebElement inputBox, WebDriverWait wait) {
		CLIPBOT.paste(new StringSelection(unescapeHtml(text)), inputBox);
	}

	public String getText() {
		return text;
	}
}
