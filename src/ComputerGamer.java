import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class ComputerGamer {
    String[] possibleMoves;

    public ComputerGamer(String[] possibleMoves) {
        this.possibleMoves = possibleMoves;
    }

    SecretKey getKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[16];
        secureRandom.nextBytes(bytes);
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    public int getMove() {
        Random random = new Random();
        return random.nextInt(possibleMoves.length);
    }

}
