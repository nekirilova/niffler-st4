package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.jupiter.DbUser;
import guru.qa.niffler.pages.LoginPage;
import guru.qa.niffler.pages.MainPage;
import guru.qa.niffler.pages.WelcomePage;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginTest extends BaseWebTest {
    private MainPage mainPage;
    private WelcomePage welcomePage;
    private LoginPage loginPage;

    @DbUser(username = "isolda1", password = "12345")
    @Test
    void statisticShouldBeVisibleAfterLogin(UserAuthEntity userAuth) {
        Selenide.open("http://127.0.0.1:3000/main");
        welcomePage = new WelcomePage();
        loginPage = welcomePage.loginButtonClick();
        loginPage.setUsername(userAuth.getUsername());
        loginPage.setPassword(userAuth.getPassword());
        mainPage = loginPage.submitButtonClick();
        $(".main-content__section-stats").shouldBe(visible);
    }
}
