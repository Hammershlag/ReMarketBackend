package uni.projects.remarketbackend.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.model.checkout.Session;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.listing.Listing;
import uni.projects.remarketbackend.models.order.Order;
import uni.projects.remarketbackend.models.order.payment.Currency;

@Service
public class StripeService {

    @Value("${STRIPE_SECRET}")
    private String stripeSecret;


    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecret;
    }


    public static String createStripeCustomer(Account account) throws Exception {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(account.getEmail())
                .setName(account.getUsername())
                .build();

        Customer customer = Customer.create(params);

        return customer.getId();
    }

    @Transactional
    public String createCheckoutSession(Order order) throws StripeException {
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://remarket-frontend.vercel.app/")
                .setCancelUrl("https://remarket-frontend.vercel.app/")
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                ;

        // Waluta wybrana przez użytkownika do płatności
        String paymentCurrency = order.getPayment().getCurrency().name().toLowerCase();

        // Add each listing as a separate line item
        for (Listing listing : order.getListings()) {
            SessionCreateParams.LineItem.PriceData.ProductData.Builder productBuilder =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(listing.getTitle())
                            .setDescription(listing.getDescription());

            // Możesz dodać informację o oryginalnej walucie do opisu
            if (order.getPayment().getCurrency() != Currency.USD) {
                String description = listing.getDescription() +
                        " (Originally priced in USD, converted to " + paymentCurrency.toUpperCase() + ")";
                productBuilder.setDescription(description);
            }

            // Przelicz cenę na wybraną walutę płatności (przykład)
            long unitAmount = Math.round(convertCurrency(
                    listing.getPrice(),
                    order.getPayment().getCurrency()
            ) * 100);

            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency(paymentCurrency) // Waluta wybrana przez użytkownika
                                            .setUnitAmount(unitAmount) // Przeliczona cena w centach
                                            .setProductData(productBuilder.build())
                                            .build()
                            )
                            .build()
            );
        }

        SessionCreateParams params = paramsBuilder.build();
        Session session = Session.create(params);

        return session.getId();
    }

    // Metoda do konwersji walut (przykładowa implementacja)
    private double convertCurrency(double amount, Currency currency) {


        if (currency == Currency.USD) {
            return amount;
        }
        if (currency == Currency.PLN) {
            return amount * 4.0;
        }
        if (currency == Currency.EUR) {
            return amount * 0.85;
        }
        if (currency == Currency.GBP) {
            return amount * 0.75;
        }
        if (currency == Currency.CHF) {
            return amount * 0.9;
        }

        return amount;
    }

}
