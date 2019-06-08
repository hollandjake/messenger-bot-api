package bot.utils.message;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

import static bot.utils.CONSTANTS.CLIPBOT;
import static bot.utils.XPATHS.MESSAGE_TEXT;
import static org.apache.commons.lang.StringEscapeUtils.unescapeHtml;


public class Text extends MessageComponent {
	private final int ID;
	private final String text;

	private Text(int ID, String text) {
		this.ID = ID;
		this.text = text;
	}

	public static Text fromString(String text) {
		return new Text(0, text);
	}

	public static ArrayList<MessageComponent> extractFrom(WebElement messageElement) {
		ArrayList<MessageComponent> messageComponents = new ArrayList<>();
		List<WebElement> textComponents = messageElement.findElements(By.xpath(MESSAGE_TEXT));
		for (WebElement textComponent : textComponents) {
			messageComponents.add(new Text(0, textComponent.getAttribute("aria-label")));
		}
		return messageComponents;
	}

	@Override
	public void send(WebElement inputBox) {
		CLIPBOT.paste(new StringSelection(unescapeHtml(text) + " "), inputBox);
	}

	@Override
	public String prettyPrint() {
		return "\"" + text + "\"";
	}

	public int getID() {
		return ID;
	}

	public String getText() {
		return text;
	}
}
