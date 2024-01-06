package guru.qa.niffler.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class WelcomePage {

    private final SelenideElement loginButton = $("a[href*='redirect']");
    private final SelenideElement registerButton = $("a[href*='register']");

    @Step("Нажать кнопку логина")
    public LoginPage loginButtonClick() {
        loginButton.click();
        return new LoginPage();
    }

    @Step("Нажать кнопку регистрации")
    public RegisterPage registerButtonClick() {
       registerButton.click();
        return new RegisterPage();
    }
}
