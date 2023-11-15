package assets.AES;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Scanner;

public class AESTextEncDec {
    private static SecretKey secretKey;
    public static byte[] ivBytes;
    public static byte[] salt;

    public static String ivString;
    public static String saltString;

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

            // Generate a random IV (Initialization Vector) if using CBC mode
            if (encryptionMode.equalsIgnoreCase("CBC")) {
                ivBytes = generateRandomIV();
            }

            // Derive a key from the password with the specified key size
            salt = generateRandomSalt();
            secretKey = deriveKey(password, ivBytes, salt, keySize);

            // Encryption
            String encryptedText = encrypt(plainText, password, keySize, encryptionMode);
            System.out.println("Encrypted Text: " + encryptedText);

            System.out.println("////////////" + "getIVString() = " + getIVString() + "////////////");

            // Decryption
            String decryptedText = decrypt(encryptedText, password, keySize, encryptionMode);
            System.out.println("Decrypted Text: " + decryptedText);

            System.out.println("Salt used: " + Base64.getEncoder().encodeToString(salt));

            scanner.close();
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Encryption/Decryption failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String encrypt(String plainText, String password, int keySize, String encryptionMode)
            throws Exception {
        // Derive a key from the password with the specified key size
        salt = generateRandomSalt();
        saltString = Base64.getEncoder().encodeToString(salt);
        ivBytes = generateRandomIV();
        ivString = Base64.getEncoder().encodeToString(ivBytes);
        secretKey = deriveKey(password, ivBytes, salt, keySize);

        Cipher cipher = Cipher.getInstance("AES/" + encryptionMode + "/PKCS5Padding");
        IvParameterSpec ivParameterSpec = (ivBytes != null) ? new IvParameterSpec(ivBytes) : null;
        if (ivParameterSpec != null) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        }

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedText, String password, int keySize, String encryptionMode)
            throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedText);

        // Derive a key from the password with the specified key size
        secretKey = deriveKey(password, ivBytes, salt, keySize);

        Cipher cipher = Cipher.getInstance("AES/" + encryptionMode + "/PKCS5Padding");
        IvParameterSpec ivParameterSpec = (ivBytes != null) ? new IvParameterSpec(ivBytes) : null;
        if (ivParameterSpec != null) {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        }

        byte[] decryptedBytes = cipher.doFinal(combined);
        return new String(decryptedBytes);
    }

    private static byte[] generateRandomIV() {
        byte[] ivBytes = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(ivBytes);
        return ivBytes;
    }

    private static byte[] generateRandomSalt() {
        byte[] salt = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        return salt;
    }

    private static SecretKey deriveKey(String password, byte[] ivBytes, byte[] salt, int keySize) throws Exception {
        if (salt == null) {
            throw new IllegalArgumentException("Salt cannot be null.");
        }

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

    public static String getIVString() {
        return ivString;
    }

    public static String getSaltString() {
        return saltString;
    }

}
