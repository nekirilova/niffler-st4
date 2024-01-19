package guru.qa.niffler.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {
    private final SelenideElement friendsTable = $(".table.abstract-table tbody");
    @Step("Найти в таблице друзей запись")
    public void findRecordInTableByText(String text) {
        friendsTable
                .$$("tr").find(text(text)).shouldBe(visible);
    }

    @Step("Посчитать количество строк в таблице друзей")
    public void countFriendsListSize() {
        friendsTable
                .$$("tr").shouldHave(size(1));
    }
}
