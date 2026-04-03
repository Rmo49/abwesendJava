package com.rmo.abwesend.util;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.rmo.abwesend.view.util.CmUtil;

public class SwissTennisPage {
	
	private static WebDriver driver = null;
	
	/**
	 * Den Webdriver vorbereiten.
	 * @return
	 */
	public static WebDriver getDriver() {
		Trace.println(4, "SwissTennis get WebDriver");
		
		if (driver != null) {
			return driver;
		}
		try {
			if (Config.webDriver.contains("chrome")) {
				// im System muss der driver gesetzt werden.
				System.setProperty(Config.webDriver, Config.webDriverFile);
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--headless");
//				driver = new ChromeDriver(options);
				driver = new ChromeDriver();

			} else {
				throw new Exception("WebDriver nicht gefunden: " + Config.webDriverFile);
			}
		} catch (Exception ex) {
			CmUtil.alertError("SwissTennis Seite öffnen ", ex);
			return null;
		}
		
		return driver;
	}
	
	/**
	 * Closes the currently active browser window or tab.
	 */
	public static void closeDriver() {
		// das geht nicht, da Error
//		driver.close();
		driver = null;
	}
	
	public static boolean startSeiteLesen(WebDriver driver) throws NoSuchElementException {
		Trace.println(3, "SwissTennis startSeiteLesen()");

		String expectedTitle = "Login-Zone";
		String actualTitle = "";

		// launch Browser and direct it to the Base URL
		Trace.println(4, "SwissTennis get baseUrl: " + Config.swissTennisUrlTournament);
		try {
			driver.get(Config.swissTennisUrlTournament);
		} catch (Exception ex) {
			CmUtil.alertError(
					"Kann Turnierseite von Swiss Tennis nicht öffnen. '" + Config.swissTennisUrlTournament + "'",
					ex.getMessage());
			return false;
		}

		// get the actual value of the title, müsste Login-Zone sein
		// 8.8.25, das ist die neue Zwischenseite, der
		// der Button "Altes Login" ruft das Java-Script "toggleLoginZone()" auf.
		actualTitle = driver.getTitle();
		Trace.println(4, "SwissTennis Zwischenseite driver.getTitle(): " + actualTitle);
		if (actualTitle.contentEquals(expectedTitle)) {
			((JavascriptExecutor)driver).executeScript("toggleLoginZone();");
		} else {
			CmUtil.alertError("Swiss Tennis", "Zwischenseite mit Altes Login nicht gefunden: " + Config.swissTennisUrlTournament);
			return false;
		}		
		
		// Login wird neu unterhalb Zwischenseite angezeigt, Titel immer noch gleich
		actualTitle = driver.getTitle();
		Trace.println(4, "SwissTennis erste Seite driver.getTitle(): " + actualTitle);		
		if (actualTitle.contentEquals(expectedTitle)) {
			WebElement element = null;
			element = driver.findElement(By.name("id"));
			element.sendKeys(Config.swisstennisId);

			element = driver.findElement(By.name("pwd"));
			element.sendKeys(Config.swisstennisPwd);

			element = driver.findElement(By.name("Tournament"));
			element.click();
		} else {
			CmUtil.alertError("Swiss Tennis", "Seite nicht gefunden: " + Config.swissTennisUrlTournament);
			return false;
		}
		return true;
	}


}
