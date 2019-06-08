package bot.utils;

import org.openqa.selenium.WebElement;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

import static bot.utils.CONSTANTS.PASTE;

public class Clipbot implements ClipboardOwner {
	private final Clipboard clipboard;

	public Clipbot(Clipboard clipboard) {
		this.clipboard = clipboard;
	}

	public void paste(Transferable transferable, WebElement inputBox) {
		Transferable prev = clipboard.getContents(null);
		clipboard.setContents(transferable, this);
		inputBox.sendKeys(PASTE);
		clipboard.setContents(prev, null);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}
}
