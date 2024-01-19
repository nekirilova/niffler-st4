package guru.qa.niffler.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {
    private final SelenideElement friendsTable = $(".table.abstract-table tbody");
    @Step("Найти в таблице друзей запись")
    public boolean findRecordInTableByText(String text) {
        return friendsTable
                .$$("tr").find(text(text)).isDisplayed();
    }

    @Step("Посчитать количество строк в таблице друзей")
    public int countFriendsListSize() {
        return friendsTable
                .$$("tr").size();
    }
}
