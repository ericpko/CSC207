/**
 * Created by lindseyshorser on 2018-05-10.
 */

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.After;
//import org.junit.Before;
//import org.junit.Test;

public class TournamentTest {

    @Test
    void testPlayerGetName() {
        Player p = new Player("Serena Williams", 1);
        assertEquals("Serena Williams", p.getName());
    }

    @Test
    public void testPlayerGetRank() {
        Player p = new Player("Serena Williams", 1);
        assertEquals(1, p.getRank());
    }

    @Test
    public void testGameGetId() {
        Game g = new Game("A1");
        assertEquals("A1", g.getId());
    }

    @Test
    public void testGameHasPlayer() {
        Player p = new Player("Serena Williams", 1);
        Game g = new Game("A1");
        p.addGame(g);
        assertTrue(g.hasPlayer(p));
    }

    @Test
    public void testGameNotHasPlayer() {
        Player p = new Player("Serena Williams", 1);
        Game g = new Game("A1");
        assertFalse(g.hasPlayer(p));
    }

    @Test
    public void testGetGameOnePlayerNoGame() {
        Player p = new Player("Serena Williams", 1);
        assertTrue(p.getGames().size() == 0);
    }

    @Test
    public void testOnePlayerAddGameGetGames() {
        Player p = new Player("Serena Williams", 1);
        Game g = new Game("A1");
        p.addGame(g);
        assertTrue(p.getGames().size() == 1);
        assertTrue(p.getGames().contains(g));
    }

    @Test
    public void testOnePlayerAddPlayerGetGames() {
        Player p = new Player("Serena Williams", 1);
        Game g = new Game("A1");
        g.addPlayer(p);
        assertTrue(p.getGames().size() == 1);
        assertTrue(p.getGames().contains(g));
        assertTrue(g.hasPlayer(p));
    }

    @Test
    public void testHasSamePlayers() {
        Player p = new Player("Serena Williams", 1);
        Game g1 = new Game("A1");
        Game g2 = new Game("C3");
        p.addGame(g1);
        p.addGame(g2);
        assertTrue(g1.hasSamePlayers(g2));
    }

    @Test
    public void testNotHasSamePlayers() {
        Game g1 = new Game("A1");
        Game g2 = new Game("C3");
        assertFalse(g1.hasSamePlayers(g2));
    }

    @Test
    public void testOnePlayerTwoGames() {
        Player p = new Player("Serena Williams", 1);
        Game g1 = new Game("A1");
        Game g2 = new Game("C3");
        p.addGame(g1);
        g2.addPlayer(p);

        // Does a contain both Games?
        assertTrue(p.getGames().size() == 2);
        assertTrue(p.getGames().contains(g1));
        assertTrue(p.getGames().contains(g2));

        // Was each Game played by the Player?
        assertTrue(g1.hasPlayer(p));
        assertTrue(g2.hasPlayer(p));

        // Were g1 and g2 played by the same Player?
        assertTrue(g1.hasSamePlayers(g2));
        assertTrue(g2.hasSamePlayers(g1));
    }

    @Test
    public void testPlayerEquals() {
        Player p1 = new Player("Serena Williams", 1);
        Player p2 = new Player("Serena Williams", 1);
        assertTrue(p1.equals(p2));
    }

    @Test
    public void testPlayerEqualsDifferentGames() {
        Player p1 = new Player("Serena Williams", 1);
        Player p2 = new Player("Serena Williams", 1);
        Game g = new Game("A1");
        p1.addGame(g);
        assertTrue(p1.equals(p2));
    }

    @Test
    public void testPlayerNotEqualsName() {
        Player p1 = new Player("Serena Williams", 1);
        Player p2 = new Player("Roger Federer", 2);
        assertFalse(p1.equals(p2));
    }

    @Test
    public void testPlayerNotEqualsRank() {
        Player p1 = new Player("Serena Williams", 1);
        Player p2 = new Player("Serena Williams", 2);
        assertFalse(p1.equals(p2));
    }

    @Test
    public void testGameEqualsNoPlayers() {
        Game g1 = new Game("A1");
        Game g2 = new Game("A1");
        assertTrue(g1.equals(g2));
    }

    @Test
    public void testGameNotEqualsTwoGames() {
        Game g1 = new Game("A1");
        Game g2 = new Game("C3");
        assertFalse(g1.equals(g2));
    }

    @Test
    public void testGameNotEqualsNoGame() {
        Game g1 = new Game("A1");
        assertFalse(g1.equals("Not a Game"));
    }

    @Test
    public void testGameEqualsTwoPlayers() {
        Game g1 = new Game("A1");
        Game g2 = new Game("A1");
        Player p1 = new Player("Serena Williams", 1);
        Player p2 = new Player("Roger Federer", 2);
        g1.addPlayer(p1);
        g1.addPlayer(p2);
        g2.addPlayer(p1);
        g2.addPlayer(p2);
        assertTrue(g1.equals(g2));
    }

    @Test
    public void testGameEqualsTwoPlayersDifferentOrder() {
        Game g1 = new Game("A1");
        Game g2 = new Game("A1");
        Player p1 = new Player("Serena Williams", 1);
        Player p2 = new Player("Roger Federer", 2);
        g1.addPlayer(p1);
        g1.addPlayer(p2);
        g2.addPlayer(p2);
        g2.addPlayer(p1);
        assertTrue(g1.equals(g2));
    }

    @Test
    public void testGameNotEqualsExtraPlayer() {
        Game g1 = new Game("A1");
        Game g2 = new Game("C3");
        Player p1 = new Player("Serena Williams", 1);
        Player p2 = new Player("Roger Federer", 2);
        Player p3 = new Player("Alexander Zverev", 3);
        g1.addPlayer(p1);
        g1.addPlayer(p2);
        g2.addPlayer(p2);
        g2.addPlayer(p1);
        g2.addPlayer(p3);
        assertFalse(g1.equals(g2));
    }

    @Test
    public void testGameToStringNoPlayers() {
        Game g1 = new Game("A1");
        String res = g1.toString();
        String exp = "A1 ()";
        assertEquals(exp, res);
    }

    @Test
    public void testGameToStringOnePlayer() {
        Game g1 = new Game("A1");
        Player p1 = new Player("Serena Williams", 1);
        g1.addPlayer(p1);
        String res = g1.toString();
        String exp = "A1 (Serena Williams)";
        assertEquals(exp, res);
    }

    @Test
    public void testGameToStringThreePlayers() {
        Game g1 = new Game("A1");
        Player p1 = new Player("Serena Williams", 1);
        Player p2 = new Player("Roger Federer", 2);
        Player p3 = new Player("Alexander Zverev", 3);
        g1.addPlayer(p1);
        g1.addPlayer(p2);
        g1.addPlayer(p3);
        String res = g1.toString();
        String exp = "A1 (Serena Williams,Roger Federer,Alexander Zverev)";
        assertEquals(exp, res);
    }

    @Test
    public void testPlayerToStringNoGames() {
        Player p1 = new Player("Serena Williams", 1);
        String res = p1.toString();
        String exp = "Serena Williams, 1";
        assertEquals(exp, res);
    }

    @Test
    public void testPlayerToStringOnePlayer() {
        Game g1 = new Game("A1");
        Player p1 = new Player("Serena Williams", 1);
        g1.addPlayer(p1);
        String res = p1.toString();
        String exp = "Serena Williams, 1" + System.lineSeparator() + g1.getId();
        assertEquals(exp, res);
    }

    @Test
    public void testPlayerToStringFourGames() {
        Game g1 = new Game("A1");
        Game g2 = new Game("C3");
        Game g3 = new Game("D4");
        Game g4 = new Game("B5");
        Player p1 = new Player("Serena Williams", 1);
        p1.addGame(g1);
        p1.addGame(g2);
        p1.addGame(g3);
        p1.addGame(g4);
        String res = p1.toString();
        String exp = "Serena Williams, 1" + System.lineSeparator() + g1.getId() + System.lineSeparator() + g2.getId()
                + System.lineSeparator() + g3.getId() + System.lineSeparator() + g4.getId();
        assertEquals(exp, res);
    }
    
}
