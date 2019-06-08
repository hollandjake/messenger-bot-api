package bot.utils.message;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Message {

	private final MessageDate date;
	private final ArrayList<MessageComponent> messageComponents;

	private Message(MessageDate date, ArrayList<MessageComponent> messageComponents) {
		this.date = date;
		this.messageComponents = messageComponents;
	}

	public void send(WebElement inputBox) {
		for (MessageComponent messageComponent : messageComponents) {
			messageComponent.send(inputBox);
		}
		inputBox.sendKeys(Keys.ENTER);
	}

	public void prettyPrint() {
		System.out.println("Message{Sent=" + date.prettyPrint() + ", Content = [" + messageComponents.stream().map(MessageComponent::prettyPrint).collect(Collectors.joining()) + "]}");
	}

	public static Message fromString(String text) {
		ArrayList<MessageComponent> messageComponents = new ArrayList<>();
		messageComponents.add(Text.fromString(text));
		return new Message(MessageDate.now(), messageComponents);
	}

	public static Message fromElement(WebElement messageElement) {
		MessageDate date = MessageDate.extractFrom(messageElement);
		if (date == null) {
			return null;
		} else {
			return new Message(date, MessageComponent.extractComponents(messageElement));
		}
	}
}
