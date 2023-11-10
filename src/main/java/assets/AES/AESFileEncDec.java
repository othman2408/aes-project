package assets.AES;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class AESFileEncDec {

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

    public static byte[] decryptFile(String algorithm, SecretKey key, IvParameterSpec iv, byte[] fileData)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        return cipher.doFinal(fileData);
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static void encryptionTest() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidKeyException,
            InvalidKeySpecException {

        byte[] salt = generateSalt();

        SecretKey key = getKeyFromPassword("pass1", salt, 128);
        IvParameterSpec iv = generateIv();

        File inputFile = new File("dectest.txt");
        File encryptedFile = new File("dectest.txt.enc");

        // Encrypt file and save it in encryptedFile
        byte[] encryptedData = encryptFile("AES/CBC/PKCS5Padding", key, iv,
                new FileInputStream(inputFile).readAllBytes());
        FileOutputStream outputStream = new FileOutputStream(encryptedFile);
        outputStream.write(encryptedData);
        outputStream.close();

        // Save the salt and IV into separate files
        Files.write(Paths.get("salt.enc"), salt);
        Files.write(Paths.get("iv.enc"), iv.getIV());
    }

    public static void decryptionTest() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidKeyException,
            InvalidKeySpecException {

        // Read the salt and IV from the files
        byte[] salt = Files.readAllBytes(Paths.get("salt.enc"));
        byte[] iv = Files.readAllBytes(Paths.get("iv.enc"));

        SecretKey key = getKeyFromPassword("pass1", salt, 128);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        File inputFile = new File("dectest.txt.enc");
        File decryptedFile = new File("dectest1.txt");

        // Decrypt file and save it in decryptedFile
        byte[] decryptedData = decryptFile("AES/CBC/PKCS5Padding", key, ivSpec,
                new FileInputStream(inputFile).readAllBytes());
        FileOutputStream outputStream = new FileOutputStream(decryptedFile);
        outputStream.write(decryptedData);
        outputStream.close();
    }

    public static void main(String[] args) {
        try {
            encryptionTest();
            decryptionTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
