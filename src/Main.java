import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Main {
	
	public static final long THROTTLE_WAIT = 2000;
	
	public static final String[] PATTERNS = new String[]{"34913?2?9", "34913?9?2", "?491332?9", "?491339?2"};
	public static final int[] keys = new int[]{KeyEvent.VK_0, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4,
			KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9
	};
	
	private static Robot robot;

	public static void main(String[] args) throws AWTException, InterruptedException{
		System.setProperty("webdriver.chrome.driver", "C:/Users/gespo/Desktop/chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(30, TimeUnit.SECONDS)
                .pollingEvery(200, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class);

        // And now use this to visit passport page
        driver.get("http://www.mariomarathon.com/passport.html#f643FBi");
        boolean found = false;
		for(int i = 0; i < PATTERNS.length && !found; i++){
			String pattern = PATTERNS[i];
			for(int j = 0; j <= 25 && !found; j++){
				for(int k = 0; k <= 25 && !found; k++){
					found = tryCombo(pattern, j, k, driver);
				}
			}
		}
	}
	
	private static boolean tryCombo(String pattern, int j, int k, WebDriver driver) throws InterruptedException {
		boolean second = false;
		String candidate = constructString(pattern, j, k);
		// Find the text input element by its name
        WebElement form = (new WebDriverWait(driver, 10))
        		   .until(ExpectedConditions.visibilityOfElementLocated(By.id("claim-code")));
        // Enter something to search for
        form.sendKeys(candidate);

        // Now submit the form. WebDriver will find the form for us from the element
        form.submit();
        
		Thread.sleep(THROTTLE_WAIT);
        WebElement msg = (new WebDriverWait(driver, 10))
     		   .until(ExpectedConditions.visibilityOfElementLocated(By.id("msg")));
        
        boolean found = msg.getText().contains("Claimed Stamp");
        if(found){
        	System.out.println("Found valid string: " + candidate);
        	Toolkit.getDefaultToolkit().beep();
        	Thread.sleep(1000);
        	Toolkit.getDefaultToolkit().beep();
        	Thread.sleep(1000);
        	Toolkit.getDefaultToolkit().beep();
        } else {
        	System.out.println("Invalid string: " + candidate);
        }
        return found;
	}
	
	private static String constructString(String pattern, int j, int k){
		return pattern.replaceFirst("\\?", String.valueOf(j)).replaceFirst("\\?", String.valueOf(k));
	}


	private static void writeNumber(int j) {
		for(char c: String.valueOf(j).toCharArray()){
			writeDigit(c - '0');
		}
	}

	public static void writeDigit(int d){
		if(d < 0 || d > 9){
			throw new IllegalArgumentException(d + " is not a digit!");
		}

        // Simulate a key press
        robot.keyPress(keys[d]);
        robot.keyRelease(keys[d]);

	}
}
