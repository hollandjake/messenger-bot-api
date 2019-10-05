package com.hollandjake.messenger_bot_api.util;

import com.hollandjake.messenger_bot_api.API;
import com.hollandjake.messenger_bot_api.message.Human;
import com.hollandjake.messenger_bot_api.message.Message;
import com.hollandjake.messenger_bot_api.util.expected_conditions.ExtendedConditions;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.hollandjake.messenger_bot_api.util.XPATHS.*;

public class WebController {
	private final API api;
	private final WebDriver webDriver;
	private final Actions keyboard;
	private final WebDriverWait wait;
	private final WebDriverWait messageWait;
	private final Config config;
	private final DatabaseController db;
	private int numMessages;
	private long lastMessage;

	public WebController(Config config, API api) {
		config.checkForProperties("email", "password", "thread_name");

		this.api = api;
		this.config = config;
		this.db = api.getDb();

		//Setup Driver
		if (config.hasProperty("geckodriver")) {
			if (api.debugging()) {
				System.out.println("User provided geckodriver");
			}
			System.setProperty("webdriver.gecko.driver", config.getProperty("geckodriver"));
			FirefoxOptions options = new FirefoxOptions();
			FirefoxProfile profile = new FirefoxProfile();
			profile.setPreference("app.update.enabled", false);
			profile.setPreference("media.volume_scale", "0.0");
			profile.setPreference("intl.accept_languages", "de");
			options.setLogLevel(FirefoxDriverLogLevel.FATAL);

			options.setProfile(profile);
			this.webDriver = new FirefoxDriver(options);
		} else {
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
		}
		this.wait = new WebDriverWait(this.webDriver, 30L, api.getRefreshRate());
		this.messageWait = new WebDriverWait(this.webDriver, api.getMessageTimeout().getSeconds(), api.getRefreshRate());
		Runtime.getRuntime().addShutdownHook(new Thread(this::quit));

		//Setup inputs
		this.keyboard = new Actions(this.webDriver);

		openThread(config.getProperty("email"), config.getProperty("password"));
	}

	public void openThread(String email, String password) {
		webDriver.get("https://www.messenger.com/t/" + db.getThread().getThreadName());
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
				messageWait.until(ExpectedConditions.and(
						ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(OTHERS_MESSAGES), numMessages),
						ExtendedConditions.pageLoaded()
				));

				int newCount = getNumberOfMessages();
				List<Message> messages = new ArrayList<>();
				webDriver.findElement(By.xpath(INPUT_BOX)).click();
				for (int i = newCount - numMessages; i > 0; i--) {
					WebElement messageElement = webDriver.findElement(By.xpath(LAST_MINUS_N(OTHERS_MESSAGES, i - 1)));
					try {
						Message message = db.saveMessage(Message.fromElement(db, messageElement));
						if (message != null) {
							messages.add(message);
						}
					} catch (SQLException | IOException e) {
						e.printStackTrace();
					}
				}
				numMessages = newCount;
				if (!messages.isEmpty()) {
					return messages;
				}
			} catch (TimeoutException e) {
				if (api.debugging()) {
					System.out.println("[" + LocalDateTime.now().toString() + "] No messaged received in the last " + api.getMessageTimeout().toString()
							.substring(2)
							.replaceAll("(\\d[HMS])(?!$)", "$1 ")
							.toLowerCase());
				}
			} catch (WebDriverException e) {
				if (!(e instanceof NoSuchSessionException) && !(e instanceof UnhandledAlertException)) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public void quit() {
		quit(false);
	}

	public void quit(Boolean withMessage) {
		if (withMessage) {
			api.sendMessage("I'm off to sleep now, see you soon!");
		}

		if (webDriver != null) {
			webDriver.quit();
			if (api.debugging()) {
				System.out.println("Closed browser");
			}
		}
	}

	public void sendMessage(Message message) {
		WebElement inputBox = webDriver.findElement(By.xpath(INPUT_BOX));

		//Send message
		message.send(inputBox, wait);
	}

	public void reload() {
		webDriver.navigate().refresh();
	}

	public Human getMe() throws SQLException {
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(SETTING_COG))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(SETTINGS_DROPDOWN))).click();
		String myName = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(MY_REAL_NAME))).getText();
		webDriver.findElement(By.xpath(SETTINGS_DONE)).click();
		return db.getHuman(myName);
	}

	public int getNumberOfMessages() {
		return webDriver.findElements(By.xpath(OTHERS_MESSAGES)).size();
	}
}
