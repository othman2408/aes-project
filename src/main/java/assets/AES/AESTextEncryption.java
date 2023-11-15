package assets.AES;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Scanner;

public class AESTextEncryption {

    public static String encrypt(String plainText, String password, int keySize, String encryptionMode)
            throws Exception {

        // Validate key size
        if (keySize != 128 && keySize != 192 && keySize != 256) {
            throw new IllegalArgumentException("Invalid key size. Please choose 128, 192, or 256 bits.");
        }

        // Validate encryption mode
        if (!encryptionMode.equalsIgnoreCase("CBC") && !encryptionMode.equalsIgnoreCase("ECB")) {
            throw new IllegalArgumentException("Invalid encryption mode. Please choose CBC or ECB.");
        }

        // Generate a random IV (Initialization Vector) if using CBC mode
        byte[] ivBytes = null;
        if (encryptionMode.equalsIgnoreCase("CBC")) {
            ivBytes = generateRandomIV();
        }

        // Derive a key from the password
        SecretKey secretKey = deriveKey(password, ivBytes, keySize);

        // Initialize the Cipher for encryption with the chosen encryption mode
        String transformation = "AES/" + encryptionMode + "/PKCS5Padding";
        Cipher cipher = Cipher.getInstance(transformation);

        if (ivBytes != null) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new javax.crypto.spec.IvParameterSpec(ivBytes));
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        }

        // Encrypt the plaintext
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

        // Combine IV and encrypted data for storage or transmission
        byte[] combined;
        if (ivBytes != null) {
            combined = new byte[ivBytes.length + encryptedBytes.length];
            System.arraycopy(ivBytes, 0, combined, 0, ivBytes.length);
            System.arraycopy(encryptedBytes, 0, combined, ivBytes.length, encryptedBytes.length);
        } else {
            combined = encryptedBytes;
        }

        return Base64.getEncoder().encodeToString(combined);
    }

    private static byte[] generateRandomIV() {
        byte[] ivBytes = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(ivBytes);
        return ivBytes;
    }

    private static SecretKey deriveKey(String password, byte[] ivBytes, int keySize) throws Exception {
        // Ensure the salt is non-null
        byte[] salt = generateRandomSalt();

        if (ivBytes != null) {
            // Use IV as part of the salt for additional uniqueness
            byte[] combinedSalt = new byte[salt.length + ivBytes.length];
            System.arraycopy(salt, 0, combinedSalt, 0, salt.length);
            System.arraycopy(ivBytes, 0, combinedSalt, salt.length, ivBytes.length);
            salt = combinedSalt;
        }

        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, keySize);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] rawKey = factory.generateSecret(keySpec).getEncoded();

        return new SecretKeySpec(rawKey, "AES");
    }

    private static byte[] generateRandomSalt() {
        byte[] salt = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        return salt;
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

            // Validate encryption mode
            if (!encryptionMode.equalsIgnoreCase("CBC") && !encryptionMode.equalsIgnoreCase("ECB")) {
                throw new IllegalArgumentException("Invalid encryption mode. Please choose CBC or ECB.");
            }

            // Encryption
            String encryptedText = encrypt(plainText, password, keySize, encryptionMode);
            System.out.println("Encrypted Text: " + encryptedText);

            scanner.close();
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Encryption failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
