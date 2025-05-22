package uni.projects.remarketbackend.models.order.payment;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */
public enum Currency {

    PLN("pln"),
    EUR("eur"),
    USD("usd"),
    GBP("gbp"),
    CHF("chf");

    private final String code;

    Currency(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
