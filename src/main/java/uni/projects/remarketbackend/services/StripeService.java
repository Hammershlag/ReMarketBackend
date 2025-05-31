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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.order.payment.Payment;

@Service
public class StripeService {

    @Value("${STRIPE_SECRET}")
    private String stripeSecret;

    static {
        Stripe.apiKey = System.getenv("STRIPE_SECRET");
    }

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

    public PaymentIntent createPayment(Payment payment, Account account) throws Exception {
        double total = payment.getTotal();
        long amountInCents = Math.round(total * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(payment.getCurrency().getCode())
                .setCustomer(account.getStripeCustomerId())
                .setPaymentMethod("pm_card_visa") // Stripe test card: 4242 4242 4242 4242
                .setConfirm(true) // For now confirm immediately
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                .build()
                )
                .build();

        return PaymentIntent.create(params);
    }

    public String createCheckoutSession(long totalAmount) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/cart")
                .setCancelUrl("http://localhost:3000/cart")
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(2000l)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Cool Product")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);

        return session.getId();
    }
}
