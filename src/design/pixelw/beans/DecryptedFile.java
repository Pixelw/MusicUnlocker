package design.pixelw.beans;

import design.pixelw.utils.FileType;

/**
 * @author Carl Su
 * @date 2020/1/16
 */
public class DecryptedFile {

    public static final byte[] FLAC_HEADER = {102, 76, 97, 67};
    private FileType fileType;
    private byte[] streamData;


    public DecryptedFile(byte[] streamData) {
        this.streamData = streamData;
        this.fileType = distinguish();
    }

    public DecryptedFile(FileType fileType, byte[] streamData) {
        this.fileType = fileType;
        this.streamData = streamData;
    }

    private FileType distinguish() {
        for (int i = 0; i < 4; i++) {
            if (streamData[i] != FLAC_HEADER[i]) {
                break;
            } else if (i == 3) {
                System.out.println(FileType.flac.getDescription());
                return FileType.flac;
            }
        }
        System.out.println(FileType.mp3.getDescription());
        return FileType.mp3;
    }


    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public byte[] getStreamData() {
        return streamData;
    }

    public void setStreamData(byte[] streamData) {
        this.streamData = streamData;
    }


}
