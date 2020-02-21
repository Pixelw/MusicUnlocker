package design.pixelw;

import design.pixelw.utils.AES;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Carl Su
 * @date 2020.1.9
 */

public class Main {

    public static void main(String[] args) {
        //初始化AES cipher。此操作耗时较长，所以在启动时初始化
        Cipher cipher = null;
        try {
            cipher = AES.getCipher();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        JFXApp.start(args,cipher);
//        LegacyApp.start(cipher);

    }
}
