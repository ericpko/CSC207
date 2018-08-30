package TransitSystemClasses;

import TransitSystemExceptions.CardNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * A TransitUser that contains personal information about this User.
 */
public class TransitUser extends User implements Serializable {

  // *** Instance variables ***
  private final ArrayList<Card> myCards; // A Stack of all this User's Card's. The
  // *** Constructor ***

  /**
   * Constructs a new TransitUser with a name and email address.
   *
   * @param firstName the name of this User.
   * @param lastName  the last name of this User.
   * @param email     the email of this User.
   */
  TransitUser(String firstName, String lastName, String email, String plainPassword) {
    super(firstName, lastName, email, plainPassword);
    this.myCards = new ArrayList<>();
  }

  /**
   * Returns a list of Card's that this TransitUser owns.
   *
   * @return a list of Card's belonging to this TransitUser.
   */
  public ArrayList<Card> getMyCards() {
    return myCards;
  }

  /**
   * Adds a Card to the top of the Stack.
   *
   * @param card the Card to be added.
   */
  void addCard(Card card) {
    if (!myCards.contains(card)) {
      myCards.add(card);
    }
  }

  /**
   * Removes the given Card from the Stack.
   *
   * @param card the Card to remove.
   */
  void removeCard(Card card) throws CardNotFoundException {
    if (!myCards.contains(card)) {
      throw new CardNotFoundException("This card has already been removed.");
    } else {
      myCards.remove(card);
    }
  }
}
