package design.pixelw;

import com.alibaba.fastjson.JSONObject;
import design.pixelw.beans.DecryptedFile;
import design.pixelw.beans.InputFile;
import design.pixelw.decrypt.Ncm;
import design.pixelw.decrypt.Qmc;
import design.pixelw.exception.MUErrors;
import design.pixelw.exception.MUException;
import design.pixelw.utils.FileIO;
import design.pixelw.utils.FileType;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Carl Su
 * @date 2020/1/22
 */
public class JFXApp extends Application {

    private List<InputFile> inputFiles;
    private File configFile = new File("./MUConfig.json");
    private String lastPath;

    private Stage primaryStage;
    private FlowPane rootLayout;

    public static void start(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Music Unlock");
        initLayout();
        loadPreference();
    }

    private void initLayout() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        //载入fxml
        fxmlLoader.setLocation(JFXApp.class.getResource("layout/fxe.fxml"));
        try {
            rootLayout = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setScene(new Scene(rootLayout));
        primaryStage.show();
    }

    @FXML
    public TextArea textArea;

    @FXML
    public void initialize() {
        textArea.setEditable(false);
    }

    private void loadPreference() {
        if (!configFile.exists()) {
            return;
        }
        String json;
        try {
            json = FileIO.readTextFile(configFile);
            JSONObject configJSON = JSONObject.parseObject(json);
            lastPath = configJSON.getString("last_path");
        } catch (IOException | MUException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void selectFolder(ActionEvent actionEvent) {
        File folder = FileIO.chooseFolder(primaryStage);
        if (folder == null) {
            return;
        }
        File[] files = folder.listFiles();  //read all files in the folder
        inputFiles = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                String extension = FileIO.getExtension(file);
                if (extension.equals("ncm") || extension.equals("qmc0") || extension.equals("qmc3")) {
                    textArea.appendText(file.getName() + "\n");
                    InputFile inputFile = new InputFile();
                    inputFile.setName(file.getName());
                    inputFile.setExtension(extension);
                    inputFile.setCreationTime(FileIO.readCreationTime(file));
                    inputFiles.add(inputFile);
                }
            }
            savePreference(folder.getAbsolutePath());
        }
    }

    private void savePreference(String absolutePath) {
        JSONObject configJson = new JSONObject();
        configJson.put("last_path", absolutePath);
        try {
            FileIO.writeTextToFile(configFile, configJson.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openAndProcessFiles() {
        List<File> fileList = FileIO.chooseFiles(FileType.supportedEncrypted, primaryStage);
        //点了取消的话
        if (fileList.size() == 0) return;
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
                        decryptedFile = Ncm.decrypt(FileIO.loadBytes(inputFile), Main.getCipher());
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
                textArea.appendText("Finished in " + (finishTime - beginTime) + " ms\n");
                System.out.println();
                tasks++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long seqEndTime = System.currentTimeMillis();
        textArea.appendText(tasks + " task(s) finished in " + (seqEndTime - seqBeginTime) + " ms, average per "
                + ((seqEndTime - seqBeginTime) / tasks));
    }
}
