package com.hollandjake.messengerBotAPI.message;

import com.hollandjake.messengerBotAPI.util.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.datatransfer.StringSelection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hollandjake.messengerBotAPI.util.CONSTANTS.CLIPBOT;
import static com.hollandjake.messengerBotAPI.util.CONSTANTS.MESSAGE_DATE_FORMATTER;
import static com.hollandjake.messengerBotAPI.util.XPATHS.MESSAGE_DATE;

public class MessageDate extends MessageObject {

	private static Pattern TODAY_REGEX = Pattern.compile("(\\d\\d):(\\d\\d)");
	private static Pattern OTHER_DAY_REGEX = Pattern.compile("(\\S+) (\\d\\d):(\\d\\d)");
	private final LocalDateTime date;

	private MessageDate(LocalDateTime date) {
		this.date = date;
	}

	public static MessageDate fromLocalDateTime(LocalDateTime date) {
		return new MessageDate(date);
	}

	public static MessageDate now() {
		return new MessageDate(LocalDateTime.now());
	}

	public static MessageDate extractFrom(Config config, WebElement messageElement) {
		List<WebElement> dateElements = messageElement.findElements(By.xpath(MESSAGE_DATE));
		LocalDateTime dateTime;
		if (!dateElements.isEmpty()) {
			WebElement dateElement = dateElements.get(0);
			String dateString = dateElement.getAttribute("data-tooltip-content").replace("at ", "");
			Matcher matcher = TODAY_REGEX.matcher(dateString);
			if (matcher.matches()) {
				//Message is from today
				dateTime = LocalDateTime.of(
						LocalDate.now(),
						LocalTime.of(
								Integer.valueOf(matcher.group(1)),
								Integer.valueOf(matcher.group(2))
						)
				);
			} else {
				matcher = OTHER_DAY_REGEX.matcher(dateString);
				if (matcher.matches()) {
					//Message is from this week
					DayOfWeek dayOfWeek = DayOfWeek.valueOf(matcher.group(1).toUpperCase());

					dateTime = LocalDateTime.of(
							LocalDate.now(),
							LocalTime.of(
									Integer.valueOf(matcher.group(2)),
									Integer.valueOf(matcher.group(3))
							)
					).with(TemporalAdjusters.previous(dayOfWeek));
				} else {
					dateTime = LocalDateTime.parse(dateString, MESSAGE_DATE_FORMATTER);
				}
			}
			return new MessageDate(dateTime);
		} else {
			return null;
		}
	}

	public static MessageDate fromResultSet(Config config, ResultSet resultSet) {
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
	public void send(WebElement inputBox, WebDriverWait wait) {
		CLIPBOT.paste(new StringSelection("[" + prettyPrint() + "]"), inputBox);
	}

	public LocalDateTime getDate() {
		return date;
	}
}
