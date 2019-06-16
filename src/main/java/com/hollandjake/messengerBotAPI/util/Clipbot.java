package com.hollandjake.messengerBotAPI.util;

import org.openqa.selenium.WebElement;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

import static com.hollandjake.messengerBotAPI.util.CONSTANTS.PASTE;

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
		clipboard.setContents(buffer, null);
		buffer = null;
	}

	public void paste(Transferable transferable, WebElement inputBox) {
		clipboard.setContents(transferable, this);
		inputBox.sendKeys(PASTE);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}
}
