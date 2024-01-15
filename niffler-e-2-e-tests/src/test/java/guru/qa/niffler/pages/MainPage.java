package guru.qa.niffler.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {
    private final SelenideElement spendingTable = $(".spendings-table tbody");
    private final SelenideElement deleteSpendingButton = $(byXpath(".//button[text()='Delete selected']"));
    private final SelenideElement linkToFriendsPage = $("a[href='/friends']");
    private final SelenideElement linkToAllPeoplePage = $("a[href='/people']");

    @Step("Отметить первый спендинг")
    public MainPage tickFirstSpendingCheckBox(String description) {
        spendingTable
                .$$("tr")
                .find(text(description))
                .$("td")
                .scrollIntoView(true)
                .click();
        return this;
    }
    @Step("Нажать кнопку для удаления спендинга")
    public MainPage clickDeleteSpendingButton() {
       deleteSpendingButton.click();
        return this;
    }

    @Step("Проверить, что в таблице после удаления спендинга не осталось строк")
    public MainPage assertThatTableIsEmptyAfterDeletingSpending(int expectedSize) {
        spendingTable.$$("tr").shouldHave(size(expectedSize));
        return this;
    }

    @Step("Перейти на страницу со списком друзей")
    public FriendsPage clickFriendsLink() {
        linkToFriendsPage.click();
        return new FriendsPage();
    }
    @Step("Перейти на страницу со списком всех пользователей")
    public AllPeoplePage clickAllPeopleLink() {
        linkToAllPeoplePage.click();
        return new AllPeoplePage();
    }

}
