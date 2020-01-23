package design.pixelw.utils;

import design.pixelw.exception.MUErrors;
import design.pixelw.exception.MUException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Carl Su
 * @date 2020/1/19
 */
public class AES {
    public static Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance("AES");
    }

    public static byte[] aesCipherDecrypt(Cipher cipher,byte[] cipherText,byte[] key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, MUException {
        if (cipher == null){
            throw new MUException(MUErrors.AES_INIT_FAILED);
        }
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
        return cipher.doFinal(cipherText);
    }
}
