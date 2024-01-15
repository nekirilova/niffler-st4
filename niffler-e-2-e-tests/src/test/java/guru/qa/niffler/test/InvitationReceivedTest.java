package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.User;
import guru.qa.niffler.jupiter.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.pages.AllPeoplePage;
import guru.qa.niffler.pages.LoginPage;
import guru.qa.niffler.pages.MainPage;
import guru.qa.niffler.pages.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.User.UserType.INVITATION_RECEIVED;
import static guru.qa.niffler.jupiter.User.UserType.INVITATION_SENT;

@ExtendWith(UsersQueueExtension.class)
public class InvitationReceivedTest {
    private MainPage mainPage;
    private WelcomePage welcomePage;
    private LoginPage loginPage;
    private AllPeoplePage allPeoplePage;

    static {
        Configuration.browserSize = "1980x1024";
    }

    @BeforeEach
    void doLogin(@User(INVITATION_RECEIVED) UserJson user) {
        Selenide.open("http://127.0.0.1:3000/main");
        welcomePage = new WelcomePage();
        loginPage = welcomePage.loginButtonClick();
        mainPage = loginPage.authorizeInApp(user.username(), user.testData().password());
        allPeoplePage = mainPage.clickAllPeopleLink();
    }

    @DisplayName("Проверка наличия кнопки  Принять запрос в строке с заданным пользователем")
    @Test
    void userShouldHaveSubmitInvitationButton(@User(INVITATION_RECEIVED) UserJson user) {
        allPeoplePage.findSubmitInvitationButton(user.testData().friendUserName());
    }
    @DisplayName("Проверка наличия кнопки Отклонить запрос в строке с заданным пользователем")
    @Test
    void userShouldHaveDeclineInvitationButton(@User(INVITATION_RECEIVED) UserJson user) {
        allPeoplePage.findDeclineInvitationButton(user.testData().friendUserName());
    }
}
