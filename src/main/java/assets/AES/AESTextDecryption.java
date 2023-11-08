package assets.AES;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Scanner;

public class AESTextDecryption {
    public static String decrypt(String encryptedText, String password, int keySize, String encryptionMode) throws Exception {
        // Validate key size
        if (keySize != 128 && keySize != 192 && keySize != 256) {
            throw new IllegalArgumentException("Invalid key size. Please choose 128, 192, or 256 bits.");
        }

        // Validate encryption mode
        if (!encryptionMode.equalsIgnoreCase("CBC") && !encryptionMode.equalsIgnoreCase("ECB")) {
            throw new IllegalArgumentException("Invalid encryption mode. Please choose CBC or ECB.");
        }

        // Decode the combined IV and encrypted data
        byte[] combined = Base64.getDecoder().decode(encryptedText);
        byte[] ivBytes = new byte[16];
        System.arraycopy(combined, 0, ivBytes, 0, 16);
        byte[] encryptedBytes = new byte[combined.length - 16];
        System.arraycopy(combined, 16, encryptedBytes, 0, encryptedBytes.length);

        // Derive the key from the password
        SecretKey secretKey = deriveKey(password, ivBytes, keySize);

        // Initialize the Cipher for decryption with the chosen encryption mode
        String transformation = "AES/" + encryptionMode + "/PKCS5Padding";
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));

        // Decrypt and return the plaintext
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    private static SecretKey deriveKey(String password, byte[] ivBytes, int keySize) throws Exception {
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), ivBytes, 65536, keySize);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] rawKey = factory.generateSecret(keySpec).getEncoded();
        return new SecretKeySpec(rawKey, "AES");
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter the encrypted text: ");
            String encryptedText = scanner.nextLine();

            System.out.print("Enter the password: ");
            String password = scanner.nextLine();

            System.out.print("Enter the key size (128, 192, or 256): ");
            int keySize = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter the encryption mode (CBC or ECB): ");
            String encryptionMode = scanner.nextLine();

            // Decryption
            String decryptedText = decrypt(encryptedText, password, keySize, encryptionMode);
            System.out.println("Decrypted Text: " + decryptedText);

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
