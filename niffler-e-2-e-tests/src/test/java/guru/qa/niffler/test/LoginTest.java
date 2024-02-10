package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.jupiter.DbUser;
import guru.qa.niffler.jupiter.UserAuthExtension;
import guru.qa.niffler.jupiter.UserRepositoryExtension;
import guru.qa.niffler.pages.LoginPage;
import guru.qa.niffler.pages.WelcomePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;


public class LoginTest extends BaseWebTest {

    @DbUser()
    @Test
    void statisticShouldBeVisibleAfterLogin(UserAuthEntity userAuth) {

        Selenide.open("http://127.0.0.1:3000/main");
        WelcomePage welcomePage = new WelcomePage();
        LoginPage loginPage = welcomePage.loginButtonClick();
        loginPage.setUsername(userAuth.getUsername());
        loginPage.setPassword(userAuth.getPassword());
        loginPage.submitButtonClick();
        $(".main-content__section-stats").shouldBe(visible);
    }

}
