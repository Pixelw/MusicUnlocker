package design.pixelw.exception;

/**
 * @author Carl Su
 * @date 2020/1/16
 */
public enum MUErrors {

    FILE_UNSUPPORTED(101,"不支持的文件类型"),
    FILE_READ_FAILED(102,"读取失败"),
    FILE_TOO_LARGE(103,"文件过大"),
    AES_INIT_FAILED(104,"AES 初始化失败"),
    TEXT_READ_FAILED(105,"Read text failed"),

    NCM_FILE_INVALID(201,"ncm 文件无效"),
    NCM_READ_KEY_FAILED(202,"无法读取密钥"),
    NCM_READ_META_FAILED(203,"无法读取元数据"),
    NCM_DECRYPT_KEY_FAILED(204,"密钥解密失败");


    private final int code;
    private final String description;

    MUErrors(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
