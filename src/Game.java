import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Game {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
//        String[] testArgs = {"1", "2", "3", "4", "5", "6", "7", "8","9"};
        Game game = new Game(args);
        game.play();
    }

    private ComputerGamer computerGamer;
    String[] moves;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public Game(String[] moves) {
        this.moves = moves;
        this.computerGamer = new ComputerGamer(this.moves);
    }

    private String getHexKey(SecretKey secretKey) {
        return new BigInteger(1, secretKey.getEncoded()).toString(16);
    }

    private String getHexHmac(int choice, SecretKey secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac signer = Mac.getInstance("HmacSHA256");
        signer.init(secretKey);
        byte[] hash = signer.doFinal(moves[choice].getBytes(StandardCharsets.UTF_8));
        return new BigInteger(1, hash).toString(16);
    }

    void play() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        if (isWrongArguments()) {
            System.out.println("""
                    Wrong incoming arguments! Please, try again!
                    Quantity of incoming arguments must be odd and >= 3
                    Incoming argument should not be repeated
                    Example: "rock paper scissors lizard Spock" or
                    "1 2 3 4 5 6 7" or " A B C D E F G\"""");
        } else {
            while (true) {
                int computerMove = computerGamer.getMove();
                SecretKey secretKey = computerGamer.getKey();
                String hmac = getHexHmac(computerMove, secretKey);
                System.out.println(hmac);
                showAvailableMoves();
                int playerMove = requestPlayerMove();
                if (playerMove == 0) {
                    break;
                } else if (playerMove >= 1 && playerMove <= moves.length) {
                    checkWinner(computerMove, playerMove);
                    String hexKey = getHexKey(secretKey);
                    System.out.println("HMAC key: " + hexKey);
                } else {
                    System.out.println("Bad input. Try again");
                }
            }
        }
    }

    private void checkWinner(int computerMove, int playerMove) {
        System.out.println("Your move:" + moves[playerMove - 1]);
        System.out.println("Computer move:" + moves[computerMove]);
        if (playerMove - 1 == computerMove) {
            System.out.println("Draw!");
        } else if ((computerMove > playerMove - 1 && computerMove <= playerMove - 1 + moves.length / 2)
                || computerMove < playerMove - 1 - moves.length / 2) {
            System.out.println("You lose...");
        } else {
            System.out.println("You win!");
        }
    }

    private int requestPlayerMove() throws IOException {
        System.out.println("Enter your move: ");
        while (true) {
            String playerChoice = reader.readLine();
            try {
                return Integer.parseInt(playerChoice);
            } catch (NumberFormatException e) {
                System.out.println("Wrong input! Try again!");
            }
        }
    }

    private void showAvailableMoves() {
        System.out.println("Available moves:");
        int count = 1;
        for (String move : moves) {
            System.out.println("" + count + " - " + move);
            count++;
        }
        System.out.println("0 - exit");
    }

    private boolean isWrongArguments() {
        if (moves.length % 2 == 0 || moves.length < 3) {
            return true;
        } else {
            for (String move : moves) {
                int count = 0;
                for (String s : moves) {
                    if (move.equals(s)) {
                        count++;
                    }
                }
                if (count >= 2) {
                    return true;
                }
            }
        }
        return false;
    }
}
