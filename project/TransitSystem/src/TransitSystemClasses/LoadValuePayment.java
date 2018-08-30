package TransitSystemClasses;

import java.util.logging.Level;

/**
 * A payment for loading fund to a Card.
 */
public class LoadValuePayment extends Payment {
  // The Card to load value on.
  private Card card;

  /**
   * Creates a new loadValue payment of the give value with the given credit card information for
   * the card.
   */
  LoadValuePayment(
          double value, String creditCardHolder, String cvv, String creditCardNumber, Card card) {
    super(value, creditCardHolder, cvv, creditCardNumber);
    this.card = card;
    logger.log(
            Level.INFO, "The following payment is created" + System.lineSeparator() + this.toString());
  }

  @Override
  public void confirm() {
    this.card.addBalance(getValue());
  }

  // No additional operation is needed when rejecting a payment
  @Override
  public void reject() {
  }

  @Override
  public String toString() {
    return super.toString()
            + System.lineSeparator()
            + String.format("The fund is loading to card %s", card.getCardNumber());
  }
}
