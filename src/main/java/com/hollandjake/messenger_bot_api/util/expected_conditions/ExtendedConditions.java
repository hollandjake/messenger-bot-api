package com.hollandjake.messenger_bot_api.util.expected_conditions;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class ExtendedConditions {

	public static ExpectedCondition<Boolean> pageLoaded() {
		return driver -> {
			assert driver != null;
			return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
		};
	}

	public static ExpectedCondition<Boolean> pageReloading() {
		return driver -> {
			assert driver != null;
			return !((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
		};
	}

}