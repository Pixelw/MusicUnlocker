package design.pixelw;

import design.pixelw.beans.DecryptedFile;
import design.pixelw.decrypt.Ncm;
import design.pixelw.decrypt.Qmc;
import design.pixelw.exception.MUErrors;
import design.pixelw.exception.MUException;
import design.pixelw.utils.FileIO;
import design.pixelw.utils.FileType;

import javax.crypto.Cipher;
import java.awt.*;
import java.io.File;

/**
 * 传统无GUI的应用前端，使用Swing组件打开文件，输出文件直接输出在文件旁。
 *
 * @author Carl Su
 * @date 2020/1/22
 */
public class LegacyApp {
    public static void start(Cipher cipher) {
        File[] inputFiles = FileIO.chooseFiles(FileType.supportedEncrypted, (Component) null);
        //点了取消的话
        if (inputFiles == null) return;
        //统计时间用
        long seqBeginTime = System.currentTimeMillis();
        int tasks = 0;
        //遍历返回的File数组
        for (File inputFile : inputFiles) {
            DecryptedFile decryptedFile;
            String fileExt = FileIO.getExtension(inputFile);
            long beginTime = System.currentTimeMillis();
            try {
                switch (fileExt) {
                    case "ncm":
                        decryptedFile = Ncm.decrypt(FileIO.loadBytes(inputFile), cipher);
                        break;
                    case "qmc0":
                    case "qmc3":
                        decryptedFile = Qmc.decrypt(FileIO.loadBytes(inputFile), fileExt);
                        break;
                    default:
                        throw new MUException(MUErrors.FILE_UNSUPPORTED);
                }
                FileIO.outputBesideInput(inputFile, decryptedFile.getStreamData(), decryptedFile.getFileType());
                long finishTime = System.currentTimeMillis();
                System.out.println("Finished in " + (finishTime - beginTime) + " ms");
                tasks++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long seqEndTime = System.currentTimeMillis();
        System.out.println(tasks + " task(s) finished in " + (seqEndTime - seqBeginTime) + " ms, average per "
                + ((seqEndTime - seqBeginTime) / tasks));
    }
}
