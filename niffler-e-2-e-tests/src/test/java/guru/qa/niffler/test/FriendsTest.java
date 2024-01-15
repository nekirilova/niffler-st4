package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.User;
import guru.qa.niffler.jupiter.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.pages.FriendsPage;
import guru.qa.niffler.pages.LoginPage;
import guru.qa.niffler.pages.MainPage;
import guru.qa.niffler.pages.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.User.UserType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(UsersQueueExtension.class)
public class FriendsTest extends BaseWebTest {
    private MainPage mainPage;
    private WelcomePage welcomePage;
    private LoginPage loginPage;
    private FriendsPage friendsPage;

    static {
        Configuration.browserSize = "1980x1024";

    }

    @BeforeEach
    void doLogin(@User(WITH_FRIENDS) UserJson user) {
        Selenide.open("http://127.0.0.1:3000/main");
        welcomePage = new WelcomePage();
        loginPage = welcomePage.loginButtonClick();
        mainPage = loginPage.authorizeInApp(user.username(), user.testData().password());
        friendsPage = mainPage.clickFriendsLink();
    }
    @DisplayName("У пользователя должен быть друг с заданным именем")
    @Test
    void userShouldHaveFriend(@User(WITH_FRIENDS) UserJson user) {
        boolean isFriendExist = friendsPage.findRecordInTableByText(user.testData().friendUserName());
        assertTrue(isFriendExist, "Не найден друг с именем " + user.testData().friendUserName());
    }

    @DisplayName("У пользователя должен быть только 1 друг")
    @Test
    void friendListShouldContainOneFriend(@User(WITH_FRIENDS) UserJson user) {
        int actualFriendListSize = friendsPage.countFriendsListSize();
        assertEquals(1, actualFriendListSize, "В списке должен быть 1 друг");
    }
}
