package bot.utils;

import bot.MBotAPI;
import bot.utils.message.Message;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static bot.utils.CONSTANTS.LAST;
import static bot.utils.XPATHS.*;

public class WebController {
	private final MBotAPI api;
	private final WebDriver webDriver;
	private final Actions keyboard;
	private final WebDriverWait wait;
	private final WebDriverWait messageWait;
	private final Config config;

	public WebController(Config config, MBotAPI api) {
		this.api = api;
		this.config = config;

		//Setup Driver
		if (config.hasProperty("chromedriver")) {
			System.out.println("User provided chromedriver");
			System.setProperty("webdriver.chrome.driver", config.getProperty("chromedriver"));
		}

		ChromeOptions chromeOptions = new ChromeOptions();
		this.webDriver = new ChromeDriver(chromeOptions);
		this.wait = new WebDriverWait(this.webDriver, 30L);
		this.messageWait = new WebDriverWait(this.webDriver, api.getMessageTimeout().getSeconds(), api.getRefreshRate());

		//Setup inputs
		this.keyboard = new Actions(this.webDriver);
	}

	public void openThread(String email, String password, String threadId) {
		webDriver.get("https://www.messenger.com/t/" + threadId);
		List<WebElement> emailFields = webDriver.findElements(By.xpath(XPATHS.LOGIN_EMAIL));
		for (WebElement emailField : emailFields) {
			wait.until(ExpectedConditions.elementToBeClickable(emailField));
			emailField.sendKeys(email);
			webDriver.findElement(By.xpath(XPATHS.LOGIN_PASS)).sendKeys(password);
			webDriver.findElement(By.xpath(XPATHS.LOGIN)).click();
		}
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(LOADING_WHEEL)));
	}

	public Message waitForMessage() {
		while (api.isRunning()) {
			try {
				wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(OTHERS_MESSAGES), getNumberOfMessages()));
				Message message = Message.fromElement(webDriver.findElement(By.xpath(LAST(OTHERS_MESSAGES))));
				if (message != null) {
					return message;
				}
			} catch (TimeoutException e) {
				if (config.getProperty("debug_messages") != null) {
					System.out.println("No messaged received in the last " + api.getMessageTimeout().toString()
							.substring(2)
							.replaceAll("(\\d[HMS])(?!$)", "$1 ")
							.toLowerCase());
				}
			}
		}
		return null;
	}

	public void sendMessage(Message message) {
		WebElement inputBox = webDriver.findElement(By.xpath(INPUT_BOX));
		//Focus Box
		inputBox.click();

		//Send message
		message.send(inputBox);
	}

	public int getNumberOfMessages() {
		return webDriver.findElements(By.xpath(OTHERS_MESSAGES)).size();
	}
}
