package design.pixelw.utils;

/**
 * @author  Carl Su
 * @date  2020.1.14
 */

public class RC4 {

    public static byte[] ksa(byte[] key) {
        byte b = 0;
        byte[] sBox = new byte[256];
        for (int i = 0; i <= 0xff; i++) {
            sBox[i] = b;
            b++;
        }
        for (int i = 0, j = 0; i < sBox.length; i++) {
            j = (j + sBox[i] + key[i % key.length]) & 0xff;
            swap(sBox,i,j);
        }
        return sBox;
    }

    public static byte[] prga(byte[] sBox, byte[] input) {
        int i = 0, j = 0, xorIndex;
        byte[] out = new byte[input.length];
        for (int byt = 0; byt < input.length; byt++) {
            i = (i + 1) & 0xff;
            j = ((sBox[i] & 0xff) + j) & 0xff;
            swap(sBox,i,j);
            xorIndex = ((sBox[i] & 0xff) + (sBox[j] & 0xff)) & 0xff;
            out[byt] = (byte) (input[i] ^ sBox[xorIndex]);
        }
        return out;
    }

    public static void magic(byte[] sBox,byte[] input){
        byte[] resultKey = new byte[sBox.length];
        for (int i = 0; i <= 0xff; i++) {
            resultKey[i] = sBox[(sBox[i] + sBox[(i + sBox[i]) & 0xff]) & 0xff];
        }
        for (int i = 0; i < input.length; i++) {
            byte cur = resultKey[(i + 1) % resultKey.length];
            input[i] ^= cur;
        }
    }

    private static void swap(byte[] bytes,int a, int b) {
        byte tmp = bytes[a];
        bytes[a] = bytes[b];
        bytes[b] = tmp;
    }

}
