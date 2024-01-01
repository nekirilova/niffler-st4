package guru.qa.niffler.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;


public class LoginPage {

    private final By USERNAME_INPUT = By.cssSelector("input[name='username']");
    private final By PASSWORD_INPUT = By.cssSelector("input[name='password']");
    private final By SUBMIT_BUTTON = By.cssSelector("button[type='submit']");

    @Step("Заполнить поле")
    public LoginPage setValue(By selector, String value) {
        $(selector).setValue(value);
        return this;
    }

    @Step("Нажать кнопку подтверждения")
    public MainPage submitButtonClick() {
        $(SUBMIT_BUTTON).click();
        return new MainPage();
    }
    public By getUserNameInput() {
        return USERNAME_INPUT;
    }
    public By getPasswordInput() {
        return PASSWORD_INPUT;
    }
}
