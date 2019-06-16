package com.hollandjake.messengerBotAPI.message;

import com.hollandjake.messengerBotAPI.util.DatabaseController;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hollandjake.messengerBotAPI.util.CONSTANTS.CLIPBOT;

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

	public static Message fromResultSet(DatabaseController db, ResultSet resultSet) throws SQLException {
		int messageId = resultSet.getInt("message_id");
		MessageThread thread = db.getThread();
		MessageDate date = MessageDate.fromResultSet(resultSet);
		Human sender = db.getMessageHuman(messageId);
		List<MessageComponent> components = db.getMessageComponents(messageId);
		return new Message(
				messageId,
				thread,
				sender,
				date,
				components);
	}

	public static Message fromElement(DatabaseController db, WebElement messageElement) {
		MessageDate date = MessageDate.extractFrom(messageElement);
		if (date != null) {
			List<MessageComponent> components = MessageComponent.extractComponents(messageElement);
			if (components.size() > 0) {
				return db.saveMessage(new Message(null, db.getThread(), Human.extractFrom(db, messageElement), date, components));
			}
		}
		return null;
	}

	public String prettyPrint() {
		return "Message #" + id + " {" + date.prettyPrint() + "}, " + sender.prettyPrint() + " -> [" + components.stream().map(MessageComponent::prettyPrint).collect(Collectors.joining()) + "]";
	}

	public void send(WebElement inputBox) {
		CLIPBOT.cache();
		for (MessageComponent messageComponent : components) {
			messageComponent.send(inputBox);
		}
		inputBox.sendKeys(Keys.ENTER);
		CLIPBOT.flush();
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
