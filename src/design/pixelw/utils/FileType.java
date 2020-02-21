package design.pixelw.utils;

/**
 * @author  Carl Su
 * @date  2020.1.10
 */

public enum FileType {

    ncm("ncm", "网易云音乐加密格式"),
    qmc0("qmc0", "QQ音乐加密格式"),
    qmc3("qmc3","QQ音乐加密格式"),

    mp3("mp3","MP3 文件"),
    flac("flac","FLAC 文件"),
    ogg("ogg","OGG 文件"),
    supportedEncrypted("all supported encrypted format","所有支持的加密格式");

    private final String fileExt;
    private final String description;

    FileType(String fileExt, String description) {
        this.fileExt = fileExt;
        this.description = description;
    }

    public String getFileExt() {
        return fileExt;
    }

    public String getDescription() {
        return description;
    }

}
