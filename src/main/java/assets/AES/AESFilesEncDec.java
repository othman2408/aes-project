package assets.AES;


import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class AESFilesEncDec {

    public static SecretKey generateKey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(keySize);
        return keyGenerator.generateKey();
    }

    public static SecretKey getKeyFromPassword(String password, byte[] salt, int keySize)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, keySize);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secret;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static byte[] encryptFile(String algorithm, SecretKey key, IvParameterSpec iv, byte[] fileData)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        return cipher.doFinal(fileData);
    }

    public static byte[] decryptFile(String algorithm, SecretKey key, IvParameterSpec iv, byte[] encryptedData)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        return cipher.doFinal(encryptedData);
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }


    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidKeyException,
            InvalidKeySpecException {

        byte[] salt = generateSalt();

        SecretKey key = getKeyFromPassword("pass1", salt, 128);
        IvParameterSpec iv = generateIv();

        File inputFile = new File("src/main/java/assets/AESTests/test.txt");
        File encryptedFile = new File("src/main/java/assets/AESTests/encryptedFile.enc");
        File decryptedFile = new File("src/main/java/assets/AESTests/decryptedFile.txt");

        // Encrypt file and save it in encryptedFile
        byte[] encryptedData = encryptFile("AES/CBC/PKCS5Padding", key, iv, new FileInputStream(inputFile).readAllBytes());
        FileOutputStream outputStream = new FileOutputStream(encryptedFile);
        outputStream.write(encryptedData);
        outputStream.close();

        // Decrypt file and save it in decryptedFile
        byte[] decryptedData = decryptFile("AES/CBC/PKCS5Padding", key, iv, new FileInputStream(encryptedFile).readAllBytes());
        outputStream = new FileOutputStream(decryptedFile);
        outputStream.write(decryptedData);
        outputStream.close();


    }

}
