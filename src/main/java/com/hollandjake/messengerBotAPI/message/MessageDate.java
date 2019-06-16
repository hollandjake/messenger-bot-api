package com.hollandjake.messengerBotAPI.message;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.awt.datatransfer.StringSelection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hollandjake.messengerBotAPI.util.CONSTANTS.CLIPBOT;
import static com.hollandjake.messengerBotAPI.util.CONSTANTS.MESSAGE_DATE_FORMATTER;
import static com.hollandjake.messengerBotAPI.util.XPATHS.MESSAGE_DATE;

public class MessageDate extends MessageObject {

	private static Pattern pattern = Pattern.compile("(\\d\\d):(\\d\\d)");
	private final LocalDateTime date;

	private MessageDate(LocalDateTime date) {
		this.date = date;
	}

	public static MessageDate now() {
		return new MessageDate(LocalDateTime.now());
	}

	public static MessageDate extractFrom(WebElement messageElement) {
		List<WebElement> webElements = messageElement.findElements(By.xpath(MESSAGE_DATE));
		Optional<WebElement> dateElements = messageElement.findElements(By.xpath(MESSAGE_DATE)).stream().findFirst();
		if (dateElements.isPresent()) {
			WebElement dateElement = dateElements.get();
			String dateString = dateElement.getAttribute("data-tooltip-content").replace("at ", "");
			Matcher matcher = pattern.matcher(dateString);
			if (matcher.matches()) {
				//Message is from today
				return new MessageDate(LocalDateTime.of(
						LocalDate.now(),
						LocalTime.of(
								Integer.valueOf(matcher.group(1)),
								Integer.valueOf(matcher.group(2))
						)
				));
			} else {
				return new MessageDate(LocalDateTime.parse(dateString, MESSAGE_DATE_FORMATTER));
			}
		} else {
			return null;
		}
	}

	public static MessageDate fromResultSet(ResultSet resultSet) {
		try {
			return new MessageDate(resultSet.getTimestamp("date").toLocalDateTime());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String prettyPrint() {
		return date.format(MESSAGE_DATE_FORMATTER);
	}

	@Override
	public void send(WebElement inputBox) {
		CLIPBOT.paste(new StringSelection("[" + prettyPrint() + "]"), inputBox);
	}

	public LocalDateTime getDate() {
		return date;
	}
}
