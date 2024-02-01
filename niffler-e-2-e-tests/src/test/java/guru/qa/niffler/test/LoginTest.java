package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.repository.UserRepositoryJdbc;
import guru.qa.niffler.jupiter.DbUser;
import guru.qa.niffler.pages.LoginPage;
import guru.qa.niffler.pages.MainPage;
import guru.qa.niffler.pages.WelcomePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginTest extends BaseWebTest{
    private MainPage mainPage;
    private WelcomePage welcomePage;
    private LoginPage loginPage;
    private final UserRepository userRepository = new UserRepositoryJdbc();
    private UserAuthEntity userAuth;
    private UserEntity user;

//    @BeforeEach
//    void createUser() {
//        userAuth = new UserAuthEntity();
//        userAuth.setUsername("valentin");
//        userAuth.setPassword("12345");
//        userAuth.setEnabled(true);
//        userAuth.setAccountNonExpired(true);
//        userAuth.setAccountNonLocked(true);
//        userAuth.setCredentialsNonExpired(true);
//        userAuth.setAuthorities(Arrays.stream(Authority.values())
//                .map(e -> {
//                    AuthorityEntity ae = new AuthorityEntity();
//                    ae.setAuthority(e);
//                    return  ae;
//                }).toList()
//        );
//     //   user = new UserEntity();
//        user.setUsername("valentin");
//        user.setCurrency(CurrencyValues.RUB);
//
//        userRepository.createInAuth(userAuth);
//        userRepository.createInUserData(user);
//    }

//    @AfterEach
//    void removeUser() {
//        userRepository.deleteInAuthById(userAuth.getId());
//        userRepository.deleteInUserDataById(user.getId());
//    }

    @DbUser(username = "valentina2", password = "12345")
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
