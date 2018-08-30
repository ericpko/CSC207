/**
 * Created by lindseyshorser on 2018-05-10.
 *
 * Edited by Eric Koehli
 */

import java.util.ArrayList;


public class Game {

    /**
     * A game.
     */

    // === Instance variables ===
    private String id;
    private ArrayList<Player> players;


    // === Constructors ===
    public Game(String id) {
        this.id = id;
        this.players = new ArrayList<>();
    }


    // === Methods ===

    /**
     * Gets the id of the game.
     *
     * @return the id of the game.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the players that play this game.
     *
     * @return an ArrayList of players of this game.
     */
    public ArrayList getPlayers() {
        return this.players;
    }

    /**
     * Add a player to this game.
     *
     * @param player    the player being added to this game.
     */
    public void addPlayer(Player player) {
        this.players.add(player);
        if (!player.getGames().contains(this)) {
            player.addGame(this);
        }
    }

    /**
     * Return true iff <player> plays this game.
     *
     * @param player    a potential player of this game.
     * @return true iff player plays this game.
     */
    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    /**
     * Return true iff this is equivalent to comparator.
     *
     * @param obj       the other Game class to compare
     * @return true iff obj has the same instance variable values.
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
        Game other = (Game) obj;

        if (this.players.size() == 0 && other.players.size() == 0) {
            return this.id.equals(other.id);
        }
        return (this.id.equals(other.id)
                && this.hasSamePlayers(other));
    }

    /**
     * Return true iff players has the same players as this.
     *
     * @param obj       the other game object to compare.
     * @return true iff this game has the same players as g2 (game2).
     */
    public boolean hasSamePlayers(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        Game other = (Game) obj;
        if (this.players.size() == 0 || other.players.size() == 0) {
            return false;
        } else if (this.players.size() != other.players.size()) {
            return false;
        }
        return this.players.containsAll(other.players);
    }

    /**
     * Override toString method.
     *
     * @return a string representation of this Game.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(this.getId() + " (");
        for (Player player : this.players) {
            s.append(player.getName() + ",");
        }
        if (this.players.size() != 0) {
            s.deleteCharAt(s.lastIndexOf(","));
        }
        s.append(")");
        return s.toString();
    }
}
