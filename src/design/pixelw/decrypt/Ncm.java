package design.pixelw.decrypt;

import design.pixelw.beans.DecryptedFile;
import design.pixelw.exception.MUErrors;
import design.pixelw.exception.MUException;
import design.pixelw.utils.AES;
import design.pixelw.utils.JDataView;
import design.pixelw.utils.RC4;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Arrays;

/**
 * ncm解码
 * @author  Carl Su
 * @date  2020.1.10
 */

public class Ncm {
    private static final byte[] NCM_HEADER = {0x43, 0x54, 0x45, 0x4e, 0x46, 0x44, 0x41, 0x4d};
    private static final byte[] CORE_KEY = {104, 122, 72, 82, 65, 109, 115, 111, 53, 107, 73, 110, 98, 97, 120, 87};
    private static final byte[] META_KEY = {35, 49, 52, 108, 106, 107, 95, 33, 92, 93, 38, 48, 85, 60, 39, 40};

    private JDataView dataView;
    private int offset;
    private static Cipher cipher;

    /**
     * RamdomAccessFile方式
     * @param file 文件
     * @param cipher AES
     * @throws FileNotFoundException
     */
    private Ncm(File file,Cipher cipher) throws FileNotFoundException {
        dataView = new JDataView(file, "r");
        Ncm.cipher = cipher;
    }

    /**
     * 读取至buffer方式
     * @param bytes buffer
     * @param cipher AES
     */
    private Ncm(byte[] bytes, Cipher cipher){
        dataView = new JDataView(bytes);
        Ncm.cipher = cipher;
    }

    /**
     * 直接解码的静态方法
     * @param file 文件
     * @param cipher AES
     * @return 解码后的文件
     * @see DecryptedFile
     *
     * @throws Exception 中途的异常
     */
    public static DecryptedFile decrypt(File file, Cipher cipher) throws Exception{
        Ncm ncm = new Ncm(file,cipher);
        return ncm.decryptStraight();
    }

    public static DecryptedFile decrypt(byte[] bytes, Cipher cipher) throws Exception {
        Ncm ncm = new Ncm(bytes,cipher);
        return ncm.decryptStraight();
    }

//    private byte[] decryptAndRefactor() throws Exception {
//        offset = 0;
//        if (invalidNcm()) {
//            throw new MUException(MUErrors.NCM_FILE_INVALID);
//        }
//        byte[] keyBox = readKey();
//        readMeta();
//        return readAudioData(keyBox);
//    }

    /**
     * 不重构meta直接解码
     * @return 解码文件
     * @throws Exception 异常
     */
    private DecryptedFile decryptStraight() throws Exception {
        offset = 0;
        if (invalidNcm()) {
            throw new MUException(MUErrors.NCM_FILE_INVALID);
        }
        //读取密钥
        byte[] keyBox = readKey();
        //skip meta data chunk
        int metaDataLen = (int) dataView.getUint32(offset, true);
        offset = offset + metaDataLen + 4;

        byte[] decryptedData = readAudioData(keyBox);
        return new DecryptedFile(decryptedData);
    }


    public boolean invalidNcm() throws IOException {
        byte[] readHeader = dataView.readBytes(0, 8);
        //读文件开头8字节
        return !Arrays.equals(readHeader, NCM_HEADER);
    }

    /**
     * 解码音频流
     * @param keyBox S-box
     * @return  buffer
     * @throws IOException 异常
     */
    private byte[] readAudioData(byte[] keyBox) throws IOException {
        offset += dataView.getUint32(offset + 5, true) + 13;
        byte[] audioData = dataView.readRestBytes(offset);
        RC4.magic(keyBox,audioData);
        return audioData;
    }

//    private void readMeta() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, MUException{
//        int metaDataLen = (int) dataView.getUint32(offset, true);
//        offset += 4;
//        if (metaDataLen == 0) {
//            throw new MUException(MUErrors.NCM_READ_META_FAILED);
//        }
//
//        byte[] cipherText = dataView.readBytes(offset, metaDataLen);
//        for (int i = 0; i < cipherText.length; i++) {
//            cipherText[i] ^= 0x63;
//        }
//        //163 key
//        offset += metaDataLen;
//        byte[] decryptMeta = aesCipherDecrypt(META_KEY,
//                Base64.getDecoder().decode(Arrays.copyOfRange(cipherText, 22, cipherText.length)));
//        JSONObject metaJson = JSON.parseObject(new String(decryptMeta).substring(6));
//        //todo metadata
//    }

    private byte[] readKey() throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, MUException {
        //跳过2字节
        offset = 10;
        //读key长度（128
        int keyLen = (int) dataView.getUint32(offset, true);
        //读key
        offset += 4;
        byte[] cipherText = dataView.readBytes(offset, keyLen);
        //每个字节与0x64异或
        for (int i = 0; i < cipherText.length; i++) {
            cipherText[i] ^= 0x64;
        }
        offset += keyLen;
        byte[] decryptedKey = AES.aesCipherDecrypt(cipher,cipherText,Ncm.CORE_KEY);
        if (decryptedKey == null) {
            throw new MUException(MUErrors.NCM_DECRYPT_KEY_FAILED);
        }
        //去掉开头neteasecloudmusic（17位）
        byte[] trimmedKey = Arrays.copyOfRange(decryptedKey, 17, decryptedKey.length);
        System.out.println(new String(trimmedKey));

        //生成S-Box
        return RC4.ksa(trimmedKey);
    }

}
