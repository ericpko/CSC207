/**
 * Created by lindseyshorser on 2018-05-10.
 *
 * Edited by Eric Koehli
 */

import java.util.ArrayList;


public class Player {

    /**
     * A player of games.
     */

    // === Instance variables ===
    private String name;
    private int rank;
    private ArrayList<Game> games;

    // === Constructor ===
    Player(String name, int rank) {
        this.name = name;
        this.rank = rank;
        this.games = new ArrayList<>();
    }

    // === Methods ===

    /**
     *
     * @return the name of this player.
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return the rank of this player.
     */
    public int getRank() {
        return this.rank;
    }

    /**
     * Add a new game
     *
     * @param game      the game this player plays.
     */
    public void addGame(Game game) {
        this.games.add(game);
        if (!game.hasPlayer(this)) {
            game.addPlayer(this);
        }
    }

    /**
     *
     * @return an ArrayList of games.
     */
    public ArrayList getGames() {
        return this.games;
    }

    /**
     *
     * @param obj       the object to compare against.
     * @return true iff this players attributes == other player's attributes.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        Player other = (Player) obj;

        return ((this.name.equals(other.name)) && (this.rank == other.rank));
    }

    /**
     *
     * @return a human-readable string representation of this Player.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(this.name + ", " + this.rank);

        for (Game game : this.games) {
            s.append(System.lineSeparator() + game.getId());
        }
        return s.toString();
    }
}
