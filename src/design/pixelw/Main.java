package design.pixelw;

import design.pixelw.exception.MUErrors;
import design.pixelw.exception.MUException;
import design.pixelw.utils.AES;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Carl Su
 * @date 2020.1.9
 */

public class Main {

    private static Cipher cipher = null;

    public static Cipher getCipher() throws MUException {
        if (cipher == null){
            throw new MUException(MUErrors.AES_INIT_FAILED);
        }
        return cipher;
    }

    public static void main(String[] args) {
        new Thread(() -> JFXApp.start(args)).start();
        //初始化AES cipher。此操作耗时较长，所以在启动时初始化
        try {
            cipher = AES.getCipher();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
//        LegacyApp.start(cipher);
    }
}
