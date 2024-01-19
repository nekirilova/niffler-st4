package guru.qa.niffler.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;


public class LoginPage {

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");

    @Step("Заполнить поле логин")
    public LoginPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Заполнить поле пароль")
    public LoginPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Нажать кнопку подтверждения")
    public MainPage submitButtonClick() {
        submitButton.click();
        return new MainPage();
    }

    @Step("Авторизоваться в приложении")
    public MainPage authorizeInApp(String username, String password) {
        return setUsername(username).setPassword(password).submitButtonClick();
    }

}
