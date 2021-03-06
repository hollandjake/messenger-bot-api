package com.hollandjake.messenger_bot_api.message;

import com.hollandjake.messenger_bot_api.util.Config;
import com.hollandjake.messenger_bot_api.util.DatabaseController;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hollandjake.messenger_bot_api.util.CONSTANTS.CLIPBOT;

public class Message extends DatabaseObject {

	private MessageThread thread;
	private Human sender;
	private MessageDate date;
	private List<MessageComponent> components;

	public Message(Integer id, MessageThread thread, Human sender, MessageDate date, List<MessageComponent> components) {
		super(id);
		this.thread = thread;
		this.sender = sender;
		this.date = date;
		this.components = components;
	}

	public void print() {
		System.out.println(prettyPrint());
	}

	public static Message fromString(MessageThread thread, Human sender, String text) {
		ArrayList<MessageComponent> components = new ArrayList<>();
		components.add(Text.fromString(text));
		return new Message(null, thread, sender, MessageDate.now(), components);
	}

	public static Message fromResultSet(Config config, DatabaseController db, ResultSet resultSet) throws SQLException {
		int messageId = resultSet.getInt("message_id");
		MessageThread thread = db.getThread();
		MessageDate date = MessageDate.fromResultSet(config, resultSet);
		Human sender = Human.fromResultSet(config, resultSet);
		List<MessageComponent> components = db.getMessageComponents(messageId);

		return new Message(
				messageId,
				thread,
				sender,
				date,
				components);
	}

	public static Message fromElement(DatabaseController db, WebElement messageElement) {
		Config config = db.getConfig();
		MessageDate date = MessageDate.extractFrom(config, messageElement);
		if (date != null) {
			List<MessageComponent> components = MessageComponent.extractComponents(config, messageElement);
			if (components.size() > 0) {
				return new Message(null, db.getThread(), Human.extractFrom(config, messageElement), date, components);
			}
		}
		return null;
	}

	public String prettyPrint() {
		return "Message #" + getId() + " {" + date.prettyPrint() + "}, " + sender.prettyPrint() + " -> [" + components.stream().map(MessageComponent::prettyPrint).collect(Collectors.joining()) + "]";
	}

	public void send(WebElement inputBox, WebDriverWait wait) {
		try {
			CLIPBOT.cache();
			for (MessageComponent component : components) {
				component.send(inputBox, wait);
			}
			inputBox.sendKeys(" " + Keys.ENTER);
		} finally {
			CLIPBOT.flush();
		}
	}

	@Override
	public String toString() {
		return "Message{" +
				"thread=" + thread +
				", sender=" + sender +
				", date=" + date +
				", components=" + components +
				'}';
	}

	public List<MessageComponent> getComponents() {
		return components;
	}

	public MessageDate getDate() {
		return date;
	}

	public Human getSender() {
		return sender;
	}

	public MessageThread getThread() {
		return thread;
	}
}
