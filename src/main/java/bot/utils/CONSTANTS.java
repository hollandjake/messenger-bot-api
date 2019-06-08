package bot.utils;

import org.openqa.selenium.Keys;

import java.awt.*;
import java.time.format.DateTimeFormatter;

public interface CONSTANTS {
	DateTimeFormatter MESSAGE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
	DateTimeFormatter MESSAGE_TIMEOUT_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

	//region System
	Clipbot CLIPBOT = new Clipbot(Toolkit.getDefaultToolkit().getSystemClipboard());
	String COPY = Keys.chord(Keys.CONTROL, "c");
	String PASTE = Keys.chord(Keys.CONTROL, "v");
	//endregion

	//region Image
	int MAX_IMAGE_SIZE = 200000; //In Bytes
	//endregion

	static String LAST_MINUS_N(String query, int n) {
		return "(" + query + ")[last()-" + n + "]";
	}

	static String LAST(String query) {
		return LAST_MINUS_N(query, 0);
	}
}