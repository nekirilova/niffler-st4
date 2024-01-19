package guru.qa.niffler.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;

public class AllPeoplePage {
    private final SelenideElement peopleTable = $(".abstract-table tbody");

    @Step("Найти в таблице всех пользователей запись")
    public void findRecordInTableByText(String text) {
        peopleTable
                .$$("tr").find(text(text)).shouldBe(visible);
    }

    @Step("Проверить, что запрос на дружбу отправлен нужному пользователю")
    public void checkInvitationSentToCorrectUser(String friendUserName) {
        peopleTable.$(byXpath(".//td[contains(text(), '" + friendUserName + "')]//..//div[contains(text(), 'Pending invitation')]"))
                .shouldBe(visible);
    }

    @Step("Проверить, что есть кнопка принять запрос на дружбу от заданного пользователя")
    public void findSubmitInvitationButton(String friendUserName) {
        peopleTable.$(byXpath(".//td[contains(text(), '" + friendUserName + "')]//..//div[@data-tooltip-id='submit-invitation']"))
                .shouldBe(visible);
    }
    @Step("Проверить, что есть кнопка отклонить запрос на дружбу от заданного пользователя")
    public void findDeclineInvitationButton(String friendUserName) {
        peopleTable.$(byXpath(".//td[contains(text(), '" + friendUserName + "')]//..//div[@data-tooltip-id='decline-invitation']"))
                .shouldBe(visible);
    }

}
