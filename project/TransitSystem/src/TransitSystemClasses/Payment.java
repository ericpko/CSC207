package TransitSystemClasses;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.*;

/**
 * A Payment via a credit card that needs to be confirmed by an AdminUser
 */
public abstract class Payment implements Serializable {
  static final Logger logger = Logger.getLogger(Payment.class.getName());
  private static ArrayList<Payment> pendingPayment;

  // Initialize logger to record activities of Payment creation, confirmation and rejection.
  static {
    logger.setUseParentHandlers(false);
    try {
      Handler fileHandler = new FileHandler("PaymentLog.txt", true);
      logger.addHandler(fileHandler);
      SimpleFormatter formatter = new SimpleFormatter();
      fileHandler.setFormatter(formatter);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String creditCardNumber;
  private String cvv;
  private String creditCardHolder;
  private String transactionId;
  private double value;
  private boolean isPending;
  private boolean isAccept;
  // The time of the confirmation of this payment.
  private LocalDateTime time;

  public Payment(double value, String creditCardHolder, String cvv, String creditCardNumber) {
    this.cvv = cvv;
    this.creditCardNumber = creditCardNumber;
    this.creditCardHolder = creditCardHolder;
    this.isPending = true;
    Random random = new Random();
    StringBuilder id = new StringBuilder();
    // Generate transaction Id
    for (int i = 0; i < 10; i++) {
      id.append(random.nextInt(10));
    }
    transactionId = id.toString();
    pendingPayment.add(this);
    this.value = value;
  }

  /**
   * Confirms the Payment with the given transaction.
   */
  static void confirmId(String transactionId) {
    search(transactionId).confirmFinalize();
  }

  /**
   * Rejects the Payment with the given transaction.
   */
  static void declineId(String transactionId) {
    search(transactionId).rejectFinalize();
  }

  /**
   * Returns the Payment object with given transactionId.
   */
  private static Payment search(String transactionId) {
    return pendingPayment
            .stream()
            .filter(x -> x.toString().equals(transactionId))
            .findFirst()
            .orElse(null);
  }

  /**
   * Finalizes the confirm operation, including change the attribute of this payment properly.
   */
  private void confirmFinalize() {
    confirm();
    pendingPayment.remove(this);
    isAccept = true;
    isPending = false;
    this.time = LocalDateTime.now();
    logger.log(
            Level.INFO,
            "The following payment is accepted." + System.lineSeparator() + this.toString());
  }

  /**
   * Finalizes the reject operation, including change the attribute of this payment properly.
   */
  private void rejectFinalize() {
    reject();
    pendingPayment.remove(this);
    isAccept = false;
    isPending = false;
    logger.log(
            Level.INFO,
            "The following payment is rejected." + System.lineSeparator() + this.toString());
  }

  /**
   * The operation when this payment is confirmed.
   */
  public abstract void confirm();

  /**
   * The operation when this payment is rejected.
   */
  public abstract void reject();

  /**
   * Returns the basic information of this payment.
   */
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("Transaction Number: ");
    result.append(transactionId);
    result.append(System.lineSeparator());
    result.append("CreditCardNumber: ");
    result.append("xxxx-xxxx-xxxx-");
    result.append(creditCardNumber.substring(12));
    result.append(System.lineSeparator());
    result.append("Credit Card Holder: ");
    result.append(creditCardHolder);
    result.append(System.lineSeparator());
    result.append("CVV: ");
    result.append(cvv);
    result.append(System.lineSeparator());
    result.append("Value: ");
    result.append(String.format("%.2f", value));
    result.append(System.lineSeparator());
    if (isPending) {
      result.append("This payment is pending.");
    } else {
      result.append("This payment is");
      if (isAccept) {
        result.append(" accepted.");
      } else {
        result.append(" rejected.");
      }
    }
    return result.toString();
  }

  double getValue() {
    return value;
  }

  public LocalDateTime getTime() {
    return time;
  }

  public static void setPendingPayment(ArrayList<Payment> pendingPayment) {
    Payment.pendingPayment = pendingPayment;
  }
}
