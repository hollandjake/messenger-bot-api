package com.hollandjake.messenger_bot_api.util;

import org.openqa.selenium.WebElement;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

import static com.hollandjake.messenger_bot_api.util.CONSTANTS.PASTE;

public class Clipbot implements ClipboardOwner {
	private final Clipboard clipboard;
	private Transferable buffer;

	public Clipbot(Clipboard clipboard) {
		this.clipboard = clipboard;
	}

	public void cache() {
		buffer = clipboard.getContents(null);
	}

	public void flush() {
		// 5 Retries before failing to set the clipboard back to default
		for (int i = 0; i < 5; i++) {
			try {
				if (buffer != null) {
					clipboard.setContents(buffer, null);
					buffer = null;
				}
				return;
			} catch (IllegalStateException ignored) {
			}
		}
	}

	public void paste(Transferable transferable, WebElement inputBox) {
		clipboard.setContents(transferable, this);
		inputBox.sendKeys(PASTE);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {

	}
}
