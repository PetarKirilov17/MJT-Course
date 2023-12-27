package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Rijndael implements SymmetricBlockCipher {
    private static final int KILOBYTE = 1024;
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private final SecretKey secretKey;

    public Rijndael(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public void encrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE);
        try (var cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
            cipherOutputStream.write(inputStream.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new CipherException("Error occurred while trying to encrypt the data");
        }
    }

    @Override
    public void decrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        Cipher cipher = initCipher(Cipher.DECRYPT_MODE);
        try (InputStream encryptedInputStream = new CipherInputStream(inputStream, cipher)) {
            byte[] buffer = new byte[KILOBYTE];
            int bytesRead;
            while ((bytesRead = encryptedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new CipherException("Error occurred while trying to decrypt the data!");
        }
    }

    private Cipher initCipher(int mode) throws CipherException {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(mode, secretKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CipherException("Error occurred while initializing the cipher");
        }
        return cipher;
    }
}
