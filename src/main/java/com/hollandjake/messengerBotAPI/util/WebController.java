package com.hollandjake.messengerBotAPI.util;

import com.hollandjake.messengerBotAPI.API;
import com.hollandjake.messengerBotAPI.message.Human;
import com.hollandjake.messengerBotAPI.message.Message;
import com.hollandjake.messengerBotAPI.message.MessageThread;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

import static com.hollandjake.messengerBotAPI.util.XPATHS.*;

public class WebController {
	private final API api;
	private final WebDriver webDriver;
	private final Actions keyboard;
	private final WebDriverWait wait;
	private final WebDriverWait messageWait;
	private final Config config;
	private final MessageThread thread;
	private final DatabaseController db;
	private int numMessages;
	public WebController(Config config, API api) {
		config.checkForProperties("email", "password", "thread_name");

		this.api = api;
		this.config = config;
		this.db = new DatabaseController(api, config);
		this.thread = db.getThread();

		//Setup Driver
		if (config.hasProperty("chromedriver")) {
			if (api.debugging()) {
				System.out.println("User provided chromedriver");
			}
			System.setProperty("webdriver.chrome.driver", config.getProperty("chromedriver"));
		}

		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments(
				"--log-level=3",
				"--silent",
				"--lang=en-GB",
				"--mute-audio",
				"--disable-infobars",
				"--disable-notifications");
		this.webDriver = new ChromeDriver(chromeOptions);
		this.wait = new WebDriverWait(this.webDriver, 30L, api.getRefreshRate());
		this.messageWait = new WebDriverWait(this.webDriver, api.getMessageTimeout().getSeconds(), api.getRefreshRate());
		Runtime.getRuntime().addShutdownHook(new Thread(this::quit));

		//Setup inputs
		this.keyboard = new Actions(this.webDriver);

		openThread(config.getProperty("email"), config.getProperty("password"));
	}

	public void openThread(String email, String password) {
		webDriver.get("https://www.messenger.com/t/" + thread.getThreadName());
		List<WebElement> emailFields = webDriver.findElements(By.xpath(XPATHS.LOGIN_EMAIL));
		for (WebElement emailField : emailFields) {
			wait.until(ExpectedConditions.elementToBeClickable(emailField));
			emailField.sendKeys(email);
			webDriver.findElement(By.xpath(XPATHS.LOGIN_PASS)).sendKeys(password);
			webDriver.findElement(By.xpath(XPATHS.LOGIN)).click();
		}
		wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(OTHERS_MESSAGES), 0));
		numMessages = getNumberOfMessages();
	}

	public List<Message> waitForMessage() {
		while (api.isRunning()) {
			try {
				messageWait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(OTHERS_MESSAGES), numMessages));
				int newCount = getNumberOfMessages();
				List<Message> messages = new ArrayList<>();
				for (int i = newCount - numMessages; i > 0; i--) {
					Message message = Message.fromElement(db, webDriver.findElement(By.xpath(LAST_MINUS_N(OTHERS_MESSAGES, i - 1))));
					if (message != null) {
						messages.add(message);
					}
				}
				numMessages = newCount;
				return messages;
			} catch (TimeoutException e) {
				if (api.debugging()) {
					System.out.println("No messaged received in the last " + api.getMessageTimeout().toString()
							.substring(2)
							.replaceAll("(\\d[HMS])(?!$)", "$1 ")
							.toLowerCase());
				}
			} catch (WebDriverException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void quit() {
		webDriver.quit();
		if (api.debugging()) {
			System.out.println("Closed browser");
		}
	}

	public void sendMessage(Message message) {
		WebElement inputBox = webDriver.findElement(By.xpath(INPUT_BOX));
		//Focus Box
		inputBox.click();

		//Send message
		message.send(inputBox, wait);
	}

	public Human getMe() {
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(SETTING_COG))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(SETTINGS_DROPDOWN))).click();
		String myName = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(MY_REAL_NAME))).getText();
		webDriver.findElement(By.xpath(SETTINGS_DONE)).click();
		return db.getHuman(myName);
	}

	public int getNumberOfMessages() {
		return webDriver.findElements(By.xpath(OTHERS_MESSAGES)).size();
	}

	public MessageThread getThread() {
		return thread;
	}
}
