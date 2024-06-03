package com.demiroren.steps;

import com.demiroren.base.BaseTest;
import com.demiroren.model.ElementInfo;
import com.thoughtworks.gauge.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StepImplementation extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(StepImplementation.class);
    private static JavascriptExecutor jsExecutor;

    public StepImplementation() throws IOException {
        String currentWorkingDir = System.getProperty("user.dir");
        initMap(getFileList(currentWorkingDir + "/src"));
        //initMap(getFileList());
    }

    public WebElement findElementWithKey(String key){
        return findElement(key);
    }

    WebElement findElement(String key) {
        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(180));
        WebElement webElement = webDriverWait
                .until(ExpectedConditions.presenceOfElementLocated(infoParam));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'})",
                webElement);
        return webElement;
    }

    List<WebElement> findElements(String key) {
        return driver.findElements(getElementInfoToBy(findElementInfoByKey(key)));
    }

    public By getElementInfoToBy(ElementInfo elementInfo) {
        By by = null;
        if (elementInfo.getType().equals("css")) {
            by = By.cssSelector(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("name"))) {
            by = By.name(elementInfo.getValue());
        } else if (elementInfo.getType().equals("id")) {
            by = By.id(elementInfo.getValue());
        } else if (elementInfo.getType().equals("xpath")) {
            by = By.xpath(elementInfo.getValue());
        } else if (elementInfo.getType().equals("linkText")) {
            by = By.linkText(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("partialLinkText"))) {
            by = By.partialLinkText(elementInfo.getValue());
        }
        return by;
    }

    @Step("Element <key> varsa tıkla")
    public void getElementWithKeyClickIfExists(String key){
        try {
            List<WebElement> liste = findElements(key);
            if (liste.size() > 0){
                System.out.println("Element bulundu");
                findElementWithKey(key).click();
                System.out.println("Elemente tiklandi");
            }
        }catch (ElementNotInteractableException e) {
            System.out.println("Element bulunamadi");
        }
    }

    @Step("Frame'e odaklan <key>")
    public void chromeFocusFrameWithNumber(String key) {
        WebElement webElement = findElementWithKey(key);
        driver.switchTo().frame(webElement);
    }

    @Step("Frame'den çık ve varsayılan içeriğe dön")
    public void chromeSwitchBackToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    private void clickElement(WebElement element) {
        element.click();
    }

    @Step("Milliyet <url> adresine git")
    public void goToUrl(String url){
        driver.get(url);
        System.out.println(url + " adresine gidiliyor.");
    }

    @Step("<Key> saniye kadar bekle")
    public void waitWithSecond(int Key) throws InterruptedException {
        Thread.sleep(Key * 1000);
    }

    @Step("Elementine tıkla <key>")
    public void clickElement(String key) {
        clickElement(findElement(key));
    }
    @Step("Elemente xpath ile tıkla <xpath>")
    public void clickXpath(By xpath){
        driver.findElement(xpath).click();
    }

    @Step("Su an ki URL <text> degerini iceriyor mu kontrol et")
    public void checkURLContainsRepeat (String text) {
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains(text),text + " değeri url de bulunmuyor.");
        System.out.println(text + " url'sini iceriyor.");
    }

    @Step("<key> elementini kontrol et")
    public void checkElement(String key) {
        assertTrue(findElement(key).isDisplayed(), "Aranan element bulunamadı");
        System.out.println(key + " elementi bulundu.");
    }

    @Step("Yeni sekmeye odaklan")
    public void chromeFocusNewTabWithNumber() throws InterruptedException {
        waitWithSecond(5);
        driver.getWindowHandles().forEach(tab -> driver.switchTo().window(tab));
    }

    @Step("<key> elementi <text> degerini iceriyor mu kontrol et")
    public void checkElementContainsText(String key,String expectedText) {
        boolean containsText = findElement(key).getText().contains(expectedText);
        assertTrue(containsText, "Expected text is not contained");
        System.out.println(key + " elementi " + expectedText + " degerini iceriyor.");
    }

    @Step({"<key> alanına js ile kaydır"})
    public void scrollToElementWithJs(String key) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        WebElement element = driver.findElement(getElementInfoToBy(elementInfo));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    @Step("<int> kere <key> elemente tıklama")
    public void arkaArkayaElementeTıklat(int value, String key) throws InterruptedException {
        for (int i=0; i < value; i++){
            clickElement(key);
            waitWithSecond(2);
        }
    }

    @Step("Geri dön ve varsayılan içeriğe dön")
    public void navigateBackAndDefaultContent(){
        driver.navigate().back();
    }

    @Step("Bu key in <key> texti urlde var mı kontrol et")
    public void checkURLContainsWithKey (String text) {
        String newsHeader = findElement(text).getText();

        Pattern pattern = Pattern.compile("\\b(\\w+)\\b");
        Matcher matcher = pattern.matcher(newsHeader);

        if (matcher.find()) {
            String ilkKelime = matcher.group(1).toLowerCase();
            System.out.println("Ilk kelime: " + ilkKelime);
            checkURLContainsRepeat(ilkKelime);
        }
    }

    @Step("Secilen haberde status 200 code gorme")
    public void selectedNewsStatusCode() throws IOException {
        HttpURLConnection connection = (HttpURLConnection)new URL(driver.getCurrentUrl()).openConnection();
        // set HEADER request
        connection.setRequestMethod("HEAD");
        // connection initiate
        connection.connect();
        //get response code
        int response = connection.getResponseCode();
        System.out.println("Http response code: " + response);
    }

    @Step("Haber numaraları ile haberlere tıklama <key>")
    public void selectNewsWithNewsNumber(String key) throws InterruptedException {
        int size = findElements(key).size();
        for (int i=0; i < size; i++){
            findElements(key).get(i).click();
            waitWithSecond(3);
        }
    }
}
