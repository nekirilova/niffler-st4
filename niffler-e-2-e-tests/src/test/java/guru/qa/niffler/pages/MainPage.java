package guru.qa.niffler.pages;

import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {
    private final By SPENDING_TABLE = By.cssSelector(".spendings-table tbody");
    private final By DELETE_BUTTON = By.xpath(".//button[text()='Delete selected']");

    public MainPage tickFirstSpendingCheckBox(String description) {
        $(SPENDING_TABLE)
                .$$("tr")
                .find(text(description))
                .$$("td")
                .first()
                .scrollIntoView(true)
                .click();
        return this;
    }

    public MainPage clickDeleteButton() {
        $(DELETE_BUTTON).click();
        return this;
    }
    public By spendingsTableSelector() {
        return SPENDING_TABLE;
    }

}
