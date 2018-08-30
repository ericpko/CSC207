/**
 * Created by lindseyshorser on 2018-05-10.
 *
 * Edited by Eric Koehli
 */

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class Tournament {

    public static void main(String[] args) throws IOException {

        // create two lists to hold the data
        ArrayList<Game> games = new ArrayList<>();
        ArrayList<String> gameNames = new ArrayList<>();

        // collect the data
        FileReader playerList = new FileReader("PlayerList.txt");
        try (Scanner sc = new Scanner(playerList)) {

            // continue parsing each line
            while (sc.hasNext()) {
                String line = sc.nextLine();
                String[] tokens = line.split(", | \\|");

                // create the new players
                Player player = new Player(tokens[0],
                        Integer.parseInt(tokens[1]));

                // create the new games
                for (int i = 2; i < tokens.length; i++) {

                    // skip if we have already made that game
                    if (!gameNames.contains(tokens[i].trim())) {
                        Game game = new Game(tokens[i].trim());
                        game.addPlayer(player);
                        games.add(game);
                        gameNames.add(game.getId());
                    }
                    // add the player to the existing game
                    else {
                        for (Game game : games) {
                            if (game.getId().equals(tokens[i].trim())) {
                                game.addPlayer(player);
                            }
                        }
                    }
                }
            }
        }
        // close the file
        playerList.close();

        // get user input
        Scanner input = new Scanner(System.in);
        System.out.println("Enter a game or 'exit' to terminate: ");
        String gameName = input.nextLine();

        // continue until 'exit' is typed
        while (!gameName.equals("exit")) {

            boolean found = false;

            // find the game and print
            for (Game game : games) {
                if (game.getId().equals(gameName)) {
                    String toPrint = game.toString();
                    toPrint = toPrint.replaceAll(",", ", ");
                    System.out.println(toPrint);
                    found = true;
                }
            }
            // check if the game has been found
            if (!found) {
                System.out.println("This is not a valid game.");
            }

            // update gameName
            System.out.println("Enter a new game: ");
            gameName = input.nextLine();
        }

    }
}
