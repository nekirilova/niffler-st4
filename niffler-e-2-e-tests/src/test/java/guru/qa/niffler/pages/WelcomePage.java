package guru.qa.niffler.pages;

import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class WelcomePage {
    private final By LOGIN_BUTTON = By.cssSelector("a[href*='redirect']");
    private final By REGISTER_BUTTON = By.cssSelector("a[href*='register']");
    public LoginPage loginButtonClick() {
        $(LOGIN_BUTTON).click();
        return new LoginPage();
    }
    public RegisterPage registerButtonClick() {
        $(REGISTER_BUTTON).click();
        return new RegisterPage();
    }
}
