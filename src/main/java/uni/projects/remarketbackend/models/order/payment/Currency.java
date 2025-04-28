package uni.projects.remarketbackend.models.order.payment;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */
public enum Currency {

    PLN("PLN"),
    EUR("EUR"),
    USD("USD"),
    GBP("GBP"),
    CHF("CHF");

    private final String code;

    Currency(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
