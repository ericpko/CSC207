package TransitSystemClasses;

import java.util.ArrayList;
import java.util.logging.Level;

public class BuyCardPayment extends Payment {

  // All buy card records.
  private static ArrayList<BuyCardPayment> acceptedBuyCardPayment;
  private String cardNumber;
  // The account that is buying card.
  private TransitUserAccount account;

  /**
   * Creates a new BuyCardPayment for the given TransitUserAccount account with the given credit
   * card information.
   */
  BuyCardPayment(
          String creditCardHolder, String cvv, String creditCardNumber, TransitUserAccount account) {
    super(
            Fares.newCard.getFare() + Fares.cardInitValue.getFare(),
            creditCardHolder,
            cvv,
            creditCardNumber);
    this.account = account;
    logger.log(
            Level.INFO, "The following payment is created" + System.lineSeparator() + this.toString());
  }

  public static void setAcceptedBuyCardPayment(ArrayList<BuyCardPayment> payment) {
    acceptedBuyCardPayment = payment;
  }

  @Override
  public void confirm() {
    this.cardNumber = account.finalizePurchaseCard();
    acceptedBuyCardPayment.add(this);
  }

  // No additional operation is needed when rejecting a payment.
  @Override
  public void reject() {
  }

  @Override
  public String toString() {
    if (cardNumber != null) {
      return super.toString()
              + System.lineSeparator()
              + String.format("This fund is used to buy card %s.", cardNumber);
    } else {
      return super.toString() + System.lineSeparator() + "This fund is used to buy card.";
    }
  }
}
