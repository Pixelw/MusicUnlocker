package design.pixelw.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Carl Su
 * @date 2020.1.12
 */

public class JDataView {
    private RandomAccessFile randomAccessFile;
    private int fileSize;
    private byte[] bytes;
    private boolean loaded = false;

    public JDataView(File file, String mode) throws FileNotFoundException {
        this.randomAccessFile = new RandomAccessFile(file, mode);
        fileSize = Math.toIntExact(file.length());
    }

    public JDataView(byte[] bytes) {
        this.bytes = bytes;
        fileSize = bytes.length;
        loaded = true;
    }


//    public long getUint32() throws IOException {
//        return getUint32(pointerOffset, true);
//    }

    public long getUint32(int fileOffset, boolean isLittleEndian) throws IOException {
        byte[] readBytes = new byte[8];
        if (loaded) {
            System.arraycopy(bytes, fileOffset, readBytes, 0, 4);
        } else {
            randomAccessFile.seek(fileOffset);
            randomAccessFile.read(readBytes, 0, 4);
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(readBytes);
        return byteBuffer.order(isLittleEndian ?
                ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN).getLong();
    }

    public byte[] readBytes(int fileOffset, int readLength) throws IOException {
        System.out.println("Read from " + fileOffset + " length: " + readLength);
        byte[] readBytes = new byte[readLength];
        if (loaded) {
            System.arraycopy(bytes, fileOffset, readBytes, 0, readLength);
        } else {
            randomAccessFile.seek(fileOffset);
            randomAccessFile.read(readBytes, 0, readLength);
        }

        return readBytes;
    }

    public byte[] readRestBytes(int fileOffset) throws IOException {
        int length = fileSize - fileOffset;
        System.out.println("Read from " + fileOffset + " length: " + length);
        byte[] readBytes = new byte[length];
        if (loaded) {
            System.arraycopy(bytes, fileOffset, readBytes, 0, length);
        } else {
            randomAccessFile.seek(fileOffset);
            randomAccessFile.read(readBytes, 0, length);
        }

        return readBytes;
    }


//    public long skip(long n) throws IOException {
//        pointerOffset += n;
//        randomAccessFile.seek(pointerOffset);
//        return pointerOffset;
//    }

}
