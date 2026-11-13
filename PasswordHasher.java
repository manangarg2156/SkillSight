import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** JDK-only password hashing using PBKDF2 with a per-account random salt. */
final class PasswordHasher {
    private static final int ITERATIONS = 210_000;
    private static final int KEY_BITS = 256;
    private PasswordHasher() { }

    static String hash(char[] password) throws Exception {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        byte[] derived = derive(password, salt);
        return Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(derived);
    }

    static boolean verify(char[] password, String saltText, String hashText) {
        try {
            byte[] salt = Base64.getDecoder().decode(saltText);
            byte[] expected = Base64.getDecoder().decode(hashText);
            return MessageDigest.isEqual(expected, derive(password, salt));
        } catch (Exception ignored) {
            return false;
        }
    }

    private static byte[] derive(char[] password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }
}
