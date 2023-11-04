package assets.AES;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Scanner;

public class AESTextEncryption {
    public static String encrypt(String plainText, String password, int keySize, String encryptionMode) throws Exception {
        // Validate key size
        if (keySize != 128 && keySize != 192 && keySize != 256) {
            throw new IllegalArgumentException("Invalid key size. Please choose 128, 192, or 256 bits.");
        }

        // Validate encryption mode
        if (!encryptionMode.equalsIgnoreCase("CBC") && !encryptionMode.equalsIgnoreCase("ECB")) {
            throw new IllegalArgumentException("Invalid encryption mode. Please choose CBC or ECB.");
        }

        // Generate a random IV (Initialization Vector)
        byte[] ivBytes = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(ivBytes);

        // Derive a key from the password
        SecretKey secretKey = deriveKey(password, ivBytes, keySize);

        // Initialize the Cipher for encryption
        Cipher cipher = Cipher.getInstance("AES/" + encryptionMode + "/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));

        // Encrypt the plaintext
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

        // Combine IV and encrypted data for storage or transmission
        byte[] combined = new byte[ivBytes.length + encryptedBytes.length];
        System.arraycopy(ivBytes, 0, combined, 0, ivBytes.length);
        System.arraycopy(encryptedBytes, 0, combined, ivBytes.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    private static SecretKey deriveKey(String password, byte[] ivBytes, int keySize) throws Exception {
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), ivBytes, 65536, keySize);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] rawKey = factory.generateSecret(keySpec).getEncoded();
        return new SecretKeySpec(rawKey, "AES");
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter the plain text to encrypt: ");
            String plainText = scanner.nextLine();

            System.out.print("Enter the password: ");
            String password = scanner.nextLine();

            System.out.print("Enter the key size (128, 192, or 256): ");
            int keySize = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter the encryption mode (CBC or ECB): ");
            String encryptionMode = scanner.nextLine();

            // Encryption
            String encryptedText = encrypt(plainText, password, keySize, encryptionMode);
            System.out.println("Encrypted Text: " + encryptedText);

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
