package design.pixelw;

import design.pixelw.beans.DecryptedFile;
import design.pixelw.decrypt.Ncm;
import design.pixelw.decrypt.Qmc;
import design.pixelw.exception.MUErrors;
import design.pixelw.exception.MUException;
import design.pixelw.utils.FileIO;
import design.pixelw.utils.FileType;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.crypto.Cipher;
import java.io.File;
import java.util.List;

/**
 * @author Carl Su
 * @date 2020/1/22
 */
public class JFXApp extends Application {

    private static Cipher cipher;

    public static void start(String[] args, Cipher cipher){
        JFXApp.cipher = cipher;
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        List<File> fileList = FileIO.chooseFiles(FileType.supportedEncrypted,primaryStage);
        //点了取消的话
        if (fileList == null) return;
        //统计时间用
        long seqBeginTime = System.currentTimeMillis();
        int tasks = 0;
        //遍历返回的File数组
        for (File inputFile : fileList) {
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
