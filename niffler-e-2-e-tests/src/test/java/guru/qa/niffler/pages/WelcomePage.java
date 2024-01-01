package guru.qa.niffler.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class WelcomePage {
    private final By LOGIN_BUTTON = By.cssSelector("a[href*='redirect']");
    private final By REGISTER_BUTTON = By.cssSelector("a[href*='register']");
    @Step("Нажать кнопку логина")
    public LoginPage loginButtonClick() {
        $(LOGIN_BUTTON).click();
        return new LoginPage();
    }

    @Step("Нажать кнопку регистрации")
    public RegisterPage registerButtonClick() {
        $(REGISTER_BUTTON).click();
        return new RegisterPage();
    }
}
