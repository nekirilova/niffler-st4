package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.jupiter.UserRepositoryExtension;
import guru.qa.niffler.pages.LoginPage;
import guru.qa.niffler.pages.WelcomePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ExtendWith(UserRepositoryExtension.class)
public class LoginWithUserRepositoryTest {
    private UserRepository userRepository;

    private UserAuthEntity userAuth;
    private UserEntity user;


    @BeforeEach
    void createAndUpdateUser() {
        userAuth = new UserAuthEntity();
        userAuth.setUsername("Vanessa");
        userAuth.setPassword("12345");
        userAuth.setEnabled(true);
        userAuth.setAccountNonExpired(true);
        userAuth.setAccountNonLocked(true);
        userAuth.setCredentialsNonExpired(true);
        userAuth.setAuthorities(
                Arrays.stream(Authority.values())
                        .map(e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setAuthority(e);
                            return ae;
                        }).toList()
        );

        user = UserEntity.builder().build();
        user.setUsername("Vanessa");
        user.setCurrency(CurrencyValues.RUB);

        userRepository.createInAuth(userAuth);
        userRepository.createInUserData(user);
        user.setSurname("Black");
        user.setFirstname("Nessa");
        userRepository.updateInUserData(user);
    }

    @AfterEach
    void deleteUser() {
        userRepository.deleteInAuthById(userAuth.getId());
        userRepository.deleteInUserDataById(user.getId());
    }

    @Test
    void statisticShouldBeVisibleAfterLogin() {

        Selenide.open("http://127.0.0.1:3000/main");
        WelcomePage welcomePage = new WelcomePage();
        LoginPage loginPage = welcomePage.loginButtonClick();
        loginPage.setUsername(userAuth.getUsername());
        loginPage.setPassword(userAuth.getPassword());
        loginPage.submitButtonClick();
        $(".main-content__section-stats").shouldBe(visible);
    }

}
