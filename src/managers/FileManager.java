package managers;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by Lance Judan on 1/21/2018.
 */
public class FileManager {
    private static final String ALGORITHM = "AES";
    private static final int KEY_LENGTH = 256;

    public static Serializable loadFile(String fileName) {
        String path = getFilePath(fileName);
        try {
            if (new File(path).exists()) {
                Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
                CipherInputStream cipherInputStream = new CipherInputStream(new FileInputStream(path), cipher);
                ObjectInputStream in = new ObjectInputStream(cipherInputStream);
                SealedObject sealedObject = (SealedObject) in.readObject();
                Serializable input = (Serializable) sealedObject.getObject(cipher);
                in.close();
                return input;
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Could not load " + fileName + "\n\n" + e.toString(), "Error: Failed to Load", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        return null;
    }

    public static void saveFile(Serializable object, String fileName) {
        File tempFolder = new File(getFolderPath());
        try {
            //Makes sure "Card Manager" folder exists before saving
            if (!tempFolder.exists() || tempFolder.mkdir()) {
                throw new Exception();
            }
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            SealedObject sealedObject = new SealedObject(object, cipher);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(new FileOutputStream(getFilePath(fileName)), cipher);
            ObjectOutputStream out = new ObjectOutputStream(cipherOutputStream);
            out.writeObject(sealedObject);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Could not save " + fileName + "\n\n" + e.toString(), "Error: Failed to Save", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Returns the absolute path of the "Card Manager" directory based on operating system located in the user's
    // documents directory
    private static String getFolderPath() {
        String osName = System.getProperty("os.name");
        String path = System.getProperty("user.home");
        path += osName.equalsIgnoreCase("Linux") ? "/Documents/Card Manager/" : "\\Documents\\Card Manager\\";
        return path;
    }

    //Returns the absolute path of file located in "Card Manager" directory
    private static String getFilePath(String fileName) {
        return getFolderPath() + fileName;
    }

    //Returns cipher initialized to the specified mode
    private static Cipher getCipher(int mode) {
        Cipher cipher = null;
        if (loadSecretKey() == null || loadIV() == null) {
            generateSecretKey();
            generateIV();
        }
        SecretKey secretKey = loadSecretKey();
        IvParameterSpec ivParameterSpec = loadIV();

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            switch (mode) {
                case Cipher.ENCRYPT_MODE:
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
                    break;
                case Cipher.DECRYPT_MODE:
                    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
                    break;
            }
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            String modeString = mode == 1 ? "Encrypt Mode" : "Decrypt Mode";
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Could not create cipher in " + modeString + "\n\n" + e.toString(), "Error: Failed to Create Cipher in " + modeString, JOptionPane.ERROR_MESSAGE);
            System.exit(2);
        }
        return cipher;
    }

    private static void generateIV() {
        byte[] iv = new byte[KEY_LENGTH/8];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        try {
            FileOutputStream out = new FileOutputStream(getFilePath("IV Spec"));
            out.write(iv);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Could not save Initilization Vector\n\n" + e.toString(), "Error: Failed to Generate Initialization Vector", JOptionPane.ERROR_MESSAGE);
            System.exit(3);
        }
    }

    private static IvParameterSpec loadIV() {
        try {
            byte[] iv = Files.readAllBytes(Paths.get(getFilePath("IV Spec")));
            return new IvParameterSpec(iv);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Could not load Initilization Vector\n\n" + e.toString(), "Error: Failed to Load Initialization Vector", JOptionPane.ERROR_MESSAGE);
            System.exit(4);
        }
        return null;
    }

    private static void generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_LENGTH);
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] keyBytes = secretKey.getEncoded();
            FileOutputStream out = new FileOutputStream(getFilePath("Secret Key"));
            out.write(keyBytes);
            out.close();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Could not save Secret Key\n\n" + e.toString(), "Error: Failed to Generate Secret Key", JOptionPane.ERROR_MESSAGE);
            System.exit(5);
        }
    }

    private static SecretKey loadSecretKey() {
        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(getFilePath("Secret Key")));
            return new SecretKeySpec(keyBytes, ALGORITHM);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Could not load Secret Key\n\n" + e.toString(), "Error: Failed to Load Secret Key", JOptionPane.ERROR_MESSAGE);
            System.exit(6);
        }
        return null;
    }
}