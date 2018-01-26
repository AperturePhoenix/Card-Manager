package managers;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Lance Judan on 1/22/2018
 */
public class FileManager {
    private static final String ALGORITHM = "AES",
            SAVE_DIRECTORY = "Card Manager", SALT_PATH = "salt.enc", IV_PATH = "iv.enc";
    private static final int KEY_LENGTH = 128, ITERATIONS = 6215;
    private static final SecureRandom secureRandom = new SecureRandom();

    public static void saveFile(String password, Serializable object, String fileName) {
        try {
            File saveDirectory = getFolder();
            if (saveDirectory.exists() || saveDirectory.mkdir()) {
                File file = getFile(fileName);
                Cipher cipher = getCipher(password, Cipher.ENCRYPT_MODE);
                SealedObject sealedObject = new SealedObject(object, cipher);
                CipherOutputStream cipherOutputStream = new CipherOutputStream(new BufferedOutputStream(new FileOutputStream(file)), cipher);
                ObjectOutputStream out = new ObjectOutputStream(cipherOutputStream);
                out.writeObject(sealedObject);
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> Optional<ArrayList<T>> loadFile(String password, Class<T> type, String fileName) {
        try {
            File loadFile = getFile(fileName);
            if (loadFile.exists()) {
                Cipher cipher = getCipher(password, Cipher.DECRYPT_MODE);
                CipherInputStream cipherInputStream = new CipherInputStream(new BufferedInputStream(new FileInputStream(loadFile)), cipher);
                ObjectInputStream in = new ObjectInputStream(cipherInputStream);
                SealedObject sealedObject = (SealedObject) in.readObject();
                in.close();
                ArrayList<T> array = (ArrayList<T>) sealedObject.getObject(cipher);
                return Optional.of(array);
            }
        } catch (IOException | ClassNotFoundException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private static boolean generateSalt() {
        try {
            byte[] salt = new byte[KEY_LENGTH / 8];
            secureRandom.nextBytes(salt);
            write(salt, getFile(SALT_PATH));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static byte[] getSalt() {
        File saltFile = getFile(SALT_PATH);
        if (saltFile.exists() || generateSalt()) {
            try {
                return read(KEY_LENGTH / 8, saltFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static boolean generateIVBytes() {
        try {
            byte[] iv = new byte[KEY_LENGTH / 8];
            secureRandom.nextBytes(iv);
            write(iv, getFile(IV_PATH));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static IvParameterSpec getIVParameterSpec() {
        File ivFile = getFile(IV_PATH);
        if (ivFile.exists() || generateIVBytes()) {
            try {
                byte[] iv = read(KEY_LENGTH / 8, ivFile);
                return new IvParameterSpec(iv);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void write(byte[] bytes, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        out.write(bytes);
        out.close();
    }

    private static byte[] read(int arrayLength, File file) throws IOException {
        byte[] bytes = new byte[arrayLength];
        FileInputStream in = new FileInputStream(file);
        in.read(bytes);
        in.close();
        return bytes;
    }

    private static SecretKeySpec getSecretKeySpec(String password) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), getSalt(), ITERATIONS, KEY_LENGTH);
            SecretKey temp = factory.generateSecret(keySpec);
            return new SecretKeySpec(temp.getEncoded(), ALGORITHM);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Cipher getCipher(String password, int mode) {
        try {
            SecretKeySpec secretKeySpec = getSecretKeySpec(password);
            IvParameterSpec ivParameterSpec = getIVParameterSpec();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            switch (mode) {
                case Cipher.ENCRYPT_MODE:
                    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
                    break;
                case Cipher.DECRYPT_MODE:
                    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
                    break;
            }
            return cipher;
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Returns the "Card Manager" directory based on operating system located in the user's documents directory
    private static String getFolderPath() {
        String osName = System.getProperty("os.name");
        String path = System.getProperty("user.home");
        path += osName.equalsIgnoreCase("Linux") ? "/Documents/" + SAVE_DIRECTORY + "/" : "\\Documents\\" + SAVE_DIRECTORY + "\\";
        return path;
    }

    private static File getFolder() {
        return new File(getFolderPath());
    }

    //Returns the file located in "Card Manager" directory
    private static File getFile(String fileName) {
        return new File(getFolderPath() + fileName);
    }
}
