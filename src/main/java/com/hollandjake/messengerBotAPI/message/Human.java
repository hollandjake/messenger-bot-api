package com.hollandjake.messengerBotAPI.message;

import com.hollandjake.messengerBotAPI.util.DatabaseController;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.awt.datatransfer.StringSelection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.hollandjake.messengerBotAPI.util.CONSTANTS.CLIPBOT;
import static com.hollandjake.messengerBotAPI.util.XPATHS.MESSAGE_SENDER;

public class Human extends DatabaseObject {
	private final String name;

	private Human(Integer id, String name) {
		super(id);
		this.name = name;
	}

	public static Human extractFrom(DatabaseController db, WebElement messageElement) {
		WebElement humanContainer = messageElement.findElement(By.xpath(MESSAGE_SENDER));
		String name = humanContainer.getAttribute("data-tooltip-content");
		Human human = new Human(null, name);
		return db.saveHuman(human);
	}

	public static Human fromResultSet(ResultSet resultSet) {
		try {
			return new Human(resultSet.getInt("human_id"), resultSet.getString("name"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String prettyPrint() {
		return "@" + name;
	}

	@Override
	public void send(WebElement inputBox) {
		CLIPBOT.paste(new StringSelection(prettyPrint()), inputBox);
		inputBox.sendKeys(Keys.ENTER);
	}

	public String getName() {
		return name;
	}
}
