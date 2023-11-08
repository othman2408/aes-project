package assets.AES;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Scanner;

public class AESText {
    private final String algorithm;
    private SecretKey key;
    private IvParameterSpec iv;

    public AESText(String algorithm) {
        this.algorithm = algorithm;
    }

    public void generateKey(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        this.key = secret;
    }

    public byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        this.iv = new IvParameterSpec(salt);
        return salt;
    }

    public String encrypt(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(this.algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, this.key, this.iv);
        byte[] cipherText = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public String decrypt(String cipherText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(this.algorithm);
        cipher.init(Cipher.DECRYPT_MODE, this.key, this.iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }

    public static void main(String[] args) {
        try {
            String algorithm = "AES/CBC/PKCS5Padding";
            String password = "password";
            AESText aes = new AESText(algorithm);
            byte[] salt = aes.generateSalt();
            aes.generateKey(password, salt);

            String message = "Hello World";
            String cipherText = aes.encrypt(message);
            System.out.println("Message: " + message);
            System.out.println("Cipher Text: " + cipherText);

            String decryptedCipherText = aes.decrypt(cipherText);
            System.out.println("Decrypted Message: " + decryptedCipherText);
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
