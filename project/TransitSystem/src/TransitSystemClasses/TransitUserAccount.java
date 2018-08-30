package TransitSystemClasses;

import static TransitSystemClasses.Card.makeCard;

import TransitSystemExceptions.CardNotFoundException;
import TransitSystemExceptions.IncorrectOwnerException;
import TransitSystemExceptions.InvalidLoadAmountException;
import TransitSystemExceptions.InvalidPassDurationException;
import TransitSystemExceptions.LoginFailException;
import TransitSystemExceptions.LowBalanceException;
import TransitSystemExceptions.SingleCardException;
import TransitSystemExceptions.UserExistException;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.naming.InvalidNameException;

/** A TransitUserAccount for a User. */
public class TransitUserAccount implements Serializable {

  private static final Logger logger = Logger.getLogger(TransitUserAccount.class.getName());
  private static HashMap<String, TransitUserAccount> allTransitUserAccount;
  private TransitUser accountHolder; // The User (owner) of this Account.

  // Initialize the logger to log the information of operations by TransitUsers.
  static {
    logger.setUseParentHandlers(false);
    try {
      Handler fileHandler = new FileHandler("TransitUserAccountLog.txt", true);
      logger.addHandler(fileHandler);
      SimpleFormatter formatter = new SimpleFormatter();
      fileHandler.setFormatter(formatter);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Constructs a new TransitUserAccount for a User.
   *
   * @param accountHolder the username of the User that owns this Account.
   */
  public TransitUserAccount(TransitUser accountHolder) {
    this.accountHolder = accountHolder;
    allTransitUserAccount.put(accountHolder.getEmail(), this);
  }

  /**
   * handle the login request of a TransitUserAccount
   *
   * @param email the email of the account that is trying to log in
   * @param plainPassword the password string of the log in attempt.
   * @return the account corresponding to email
   * @throws LoginFailException if the email and password don't match.
   */
  public static TransitUserAccount handleTransitUserLogin(String email, String plainPassword)
      throws LoginFailException {
    TransitUserAccount account = allTransitUserAccount.get(email);
    if (account == null) {
      logger.log(Level.INFO, String.format("Login in fail: %s: account doesn't exist", email));
      throw new LoginFailException(String.format("%s: account doesn't exist", email));
    }
    if (account.getAccountHolder().verifyLogin(plainPassword)) {
      logger.log(Level.INFO, String.format("Transit User %s Login in.", email));
      return account;
    }
    logger.log(
        Level.INFO, String.format("Transit User %s Login fail because of wrong password.", email));
    throw new LoginFailException("Either password or account email is incorrect");
  }

  /**
   * Handle the request of creating a new TransitUserAccount.
   *
   * @param email the email of the new TransitUserAccount
   * @param firstName the first name of the new user
   * @param lastName the last name of the new user
   * @param plainPassword the plain password of the new user
   * @throws UserExistException if the email has already been registered in the system.
   */
  public static void handleNewAccount(
      String email, String firstName, String lastName, String plainPassword)
      throws UserExistException {
    if (allTransitUserAccount.keySet().contains(email)) {
      throw new UserExistException(
          String.format("%s has already registered in this system", email));
    }
    logger.log(Level.INFO, String.format("%s registers in the system", email));
    TransitUser user = new TransitUser(firstName, lastName, email, plainPassword);
    new TransitUserAccount(user);
  }

  /**
   * Changes the name of the User that owns this Account.
   *
   * @param firstName the first name of the User.
   * @param lastName the last name of the User.
   */
  public void changeName(String firstName, String lastName) throws InvalidNameException {
    String fullName = firstName + " " + lastName;
    if (isValidName(fullName)) {
      logger.log(
          Level.INFO,
          String.format(
              "%s change name from %s to %s",
              accountHolder.getEmail(), accountHolder.getName(), firstName + " " + lastName));
      accountHolder.setName(firstName, lastName);
    } else {
      throw new InvalidNameException("This is not a valid name.");
    }
  }

  /**
   * Returns true if and only if the given full name matches the regex pattern.
   *
   * @param fullName the full name of a person to check against regex.
   * @return true iff the given full name matches the regex pattern.
   */
  private boolean isValidName(String fullName) {
    return fullName.matches("^[\\p{L} .'-]+$");
  }

  /**
   * Returns the average transit cost per month that the User of this Account is spending.
   *
   * @param startDate the start date of transactions this User want to look at
   * @param endDate the end date of transactions this User want to look at
   * @return the average transit cost per month User of this Account spent between the two specified
   *     dates.
   */
  public double averageTransitCostPerMonth(LocalDate startDate, LocalDate endDate) {
    LocalDate start = LocalDate.of(startDate.getYear(), startDate.getMonth(), 1);
    LocalDate end =
        LocalDate.of(
            endDate.getYear(), endDate.getMonth(), endDate.getMonth().length(endDate.isLeapYear()));
    double result = 0;
    for (Card c : getAccountHolder().getMyCards()) {
      result += c.getTotalCostsBetweenMonths(startDate, endDate);
    }
    return result / getTotalMonths(start, end);
  }

  /**
   * Returns the months between the startDate and the endDate this transitUser's request viewing
   * average cost.
   *
   * @return the month different between startDate and endDate.
   */
  private long getTotalMonths(LocalDate startDate, LocalDate endDate) {
    return ChronoUnit.MONTHS.between(startDate, endDate) + 1;
  }

  /**
   * Create an load value request waiting for approval. Warning: proper amounts must be checked in
   * TransitUserAccount.
   *
   * @param value the amount of money to add.
   */
  public void addValue(
      float value, Card card, String creditCardHolder, String cvv, String creditCardNumber)
      throws IncorrectOwnerException, InvalidLoadAmountException {
    checkValidHolder(card);
    if (value == 10 | value == 20 | value == 50 | value == 100) {
      new LoadValuePayment(value, creditCardHolder, cvv, creditCardNumber, card);
      logger.log(
          Level.INFO,
          String.format(
              "%s start a request a load value request of loading $%s to %s",
              accountHolder.getEmail(), value, card.getCardNumber()));
    } else {
      throw new InvalidLoadAmountException(
          "The value added to cards should be $10, $20, $50 or $100.");
    }
  }

  /** Suspends this card by deactivation. This card may no longer be used. */
  public void deactivateCard(Card card) throws IncorrectOwnerException {
    checkValidHolder(card);
    card.setActivated(false);
    logger.log(
        Level.INFO,
        String.format("%s deactivate card %s", accountHolder.getEmail(), card.getCardNumber()));
  }

  /**
   * Activates this Card, or throws IncorrectOwnerException.
   *
   * @param card the email of the cardHolder that owns this Card.
   * @throws IncorrectOwnerException when the email doesn't match the cardHolder's email.
   */
  public void activateCard(Card card) throws IncorrectOwnerException {
    checkValidHolder(card);
    card.setActivated(true);
    logger.log(
        Level.INFO,
        String.format("%s activate card %s", accountHolder.getEmail(), card.getCardNumber()));
  }

  /**
   * Return the three most recent trips of some card of this account holder.
   *
   * @param card which information is to be acquired.
   * @return a string representation of the three most recent trips of the card.
   * @throws IncorrectOwnerException if this cardHolder is not the owner of the card.
   */
  public String viewCardRecentTrip(Card card) throws IncorrectOwnerException {
    checkValidHolder(card);
    Trip[] recentTrips = card.getLastThreeTrips();
    StringBuilder result = new StringBuilder();
    int num = recentTrips.length;
    if (num == 0) {
      result.append("This card has not been used recently.");
      return result.toString();
    }
    return Trip.getEnumeratedTrips(recentTrips);
  }

  /**
   * Check whether this TransitUserAccount is the valid holder of the card
   *
   * @param card the card to be checked
   * @throws IncorrectOwnerException if card is not associated with this TransitUserAccount
   */
  private void checkValidHolder(Card card) throws IncorrectOwnerException {
    if (!accountHolder.getEmail().equals(card.getCardHolderEmail())) {
      throw new IncorrectOwnerException(
          String.format(
              "%s is not the owner of card %s.", accountHolder.getEmail(), card.getCardNumber()));
    }
  }

  /**
   * Transfer the balance of card to transfer. Note that if the balance of card is negative, the
   * debt will also be transferred
   *
   * @param card the card to be remove
   * @param transfer the card that receive the balance/debt of the original card.
   * @throws IncorrectOwnerException if card is not associated with this card
   * @throws CardNotFoundException if card can't be found
   * @throws SingleCardException if this is the only card of the user.
   */
  public void removeCard(Card card, Card transfer)
      throws IncorrectOwnerException, CardNotFoundException, SingleCardException {
    if (this.getAccountHolder().getMyCards().size() == 1) {
      throw new SingleCardException("This is your only card, you can't remove it");
    }
    checkValidHolder(card);
    transfer.addBalance(card.getBalance());
    accountHolder.removeCard(card);
    logger.log(
        Level.INFO,
        String.format(
            "%s removes card %s and transfer balance/debt to card %s",
            accountHolder.getEmail(), card.getCardNumber(), transfer.getCardNumber()));
  }

  /**
   * Buy a new card for the holder of this TransitUserAccount.
   *
   * @return the card number of the new Card.
   */
  String finalizePurchaseCard() {
    Card card = makeCard(accountHolder.getEmail());
    accountHolder.addCard(card);
    logger.log(
        Level.INFO,
        String.format("%s buy a new card %s", accountHolder.getEmail(), card.getCardNumber()));
    return card.getCardNumber();
  }

  /**
   * Start a new buy card request with the credit card information
   *
   * @param creditCardHolder the holder of the credit card
   * @param cvv the cvv of the credit card
   * @param creditCardNumber the card number of the credit card.
   */
  public void purchaseCard(String creditCardHolder, String cvv, String creditCardNumber) {
    logger.log(
        Level.INFO,
        String.format("%s start a request of buying a new card", accountHolder.getEmail()));
    new BuyCardPayment(creditCardHolder, cvv, creditCardNumber, this);
  }

  /**
   * Buy a new transit pass for the card.
   *
   * @param purchaseTime the time of this purchase
   * @param card the card is being loaded a transit pass
   * @param startTime the start time of the transit pass
   * @param duration the duration of the transit pass
   * @throws LowBalanceException if the card done
   * @throws IncorrectOwnerException if this account is not associated with the card
   * @throws InvalidPassDurationException if there is not such a pass duration.
   */
  public void purchaseTransitPass(
      LocalDateTime purchaseTime, Card card, LocalDate startTime, int duration)
      throws LowBalanceException, IncorrectOwnerException, InvalidPassDurationException {
    checkValidHolder(card);
    if (!(duration == 7 || duration == 30)) {
      throw new InvalidPassDurationException("This is not a valid pass duration.");
    }
    TransitPass transitPass = TransitPass.makeTransitPass(startTime, duration);
    card.addTransitPass(purchaseTime, transitPass);
    logger.log(
        Level.INFO,
        String.format(
            "%s buy a new transit pass: %s", accountHolder.getEmail(), transitPass.getType()));
  }

  /**
   * Return the information of all the transit pass that is in effect at the give time. Return empty
   * string if there is not any.
   */
  public String passInfo(LocalDateTime time) {
    ArrayList<Card> cards = new ArrayList<>();
    for (Card card : accountHolder.getMyCards()) {
      if (card.hasValidPass(time)) {
        cards.add(card);
      }
    }
    StringBuilder result = new StringBuilder();
    for (Card card : cards) {
      ArrayList<TransitPass> passes = new ArrayList<>();
      for (TransitPass pass : card.getTransitPasses()) {
        if (pass.isValid(time.toLocalDate())) {
          passes.add(pass);
        }
      }
      for (TransitPass pass : passes) {
        result.append(pass.toString());
        result.append(System.lineSeparator());
      }
    }
    return result.toString();
  }

  public String viewCardTransactions(LocalDate startDate, LocalDate endDate, Card card) {
    LocalDate start = LocalDate.of(startDate.getYear(), startDate.getMonth(), 1);
    LocalDate end =
        LocalDate.of(
            endDate.getYear(), endDate.getMonth(), endDate.getMonth().length(endDate.isLeapYear()));
    Map<LocalDateTime, Double> fareTransactions = card.getTransactions();
    Map<LocalDateTime, Double> passTransactions = card.getPassTransactions();
    StringBuilder result = new StringBuilder();
    for (LocalDateTime time : fareTransactions.keySet()) {
      if ((time.toLocalDate().isAfter(start)
          || time.toLocalDate().equals(start)) && (time.toLocalDate().isBefore(end)
          || time.toLocalDate().equals(end))) {
        result.append(String.format("%s: %.2f %n", time, fareTransactions.get(time)));
      }
    }
    for (LocalDateTime time : passTransactions.keySet()) {
      if ((time.toLocalDate().isAfter(start)
          || time.toLocalDate().equals(start)) && (time.toLocalDate().isBefore(end)
          || time.toLocalDate().equals(end))) {
        result.append(String.format("%s: %.2f %n", time, passTransactions.get(time)));
      }
    }
    return result.toString();
  }

  /** Return whether any card associate with this account has a valid pass at the given time. */
  public boolean hasPass(LocalDateTime time) {
    return accountHolder.getMyCards().stream().anyMatch(x -> x.hasValidPass(time));
  }

  public static void setAllTransitUserAccount(
      HashMap<String, TransitUserAccount> allTransitUserAccount) {
    TransitUserAccount.allTransitUserAccount = allTransitUserAccount;
  }

  public TransitUser getAccountHolder() {
    return accountHolder;
  }

  public String getName() {
    return accountHolder.getName();
  }
}
