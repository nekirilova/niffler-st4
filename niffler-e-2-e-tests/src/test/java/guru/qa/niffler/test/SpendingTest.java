package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.GenerateCategory;
import guru.qa.niffler.jupiter.GenerateSpend;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.pages.LoginPage;
import guru.qa.niffler.pages.MainPage;
import guru.qa.niffler.pages.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$;


public class SpendingTest {

  private MainPage mainPage;
  private WelcomePage welcomePage;
  private LoginPage loginPage;

  static {
    Configuration.browserSize = "1980x1024";

  }

  @BeforeEach
  void doLogin() {
    Selenide.open("http://127.0.0.1:3000/main");
    welcomePage = new WelcomePage();
    loginPage = welcomePage.loginButtonClick();
    loginPage.setUsername("duck");
    loginPage.setPassword("12345");
    mainPage = loginPage.submitButtonClick();
  }

  @GenerateCategory(
          username = "duck",
          category = "Обучение"
  )
  @GenerateSpend(
      username = "duck",
      description = "QA.GURU Advanced 4",
      amount = 72500.00,
      category = "Обучение",
      currency = CurrencyValues.RUB
  )
  @Test
  void spendingShouldBeDeletedByButtonDeleteSpending(CategoryJson category, SpendJson spend) {
    mainPage.tickFirstSpendingCheckBox(spend.description());
    mainPage.clickDeleteSpendingButton();
    mainPage.assertThatTableIsEmptyAfterDeletingSpending(0);

  }
}
