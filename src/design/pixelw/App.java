package design.pixelw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import design.pixelw.beans.DecryptedFile;
import design.pixelw.beans.JFXInputFile;
import design.pixelw.decrypt.Ncm;
import design.pixelw.decrypt.Qmc;
import design.pixelw.exception.MUErrors;
import design.pixelw.exception.MUException;
import design.pixelw.utils.FileIO;
import design.pixelw.utils.FileType;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * @author Carl Su
 * @date 2020/2/23
 */
public class App extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private JFXController controller;

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private ObservableList<JFXInputFile> jfxInputFiles = FXCollections.observableArrayList();

    public ObservableList<JFXInputFile> getJfxInputFiles() {
        return jfxInputFiles;
    }

    private ObservableList<JFXInputFile> selectedFiles = FXCollections.observableArrayList();

    public void setSelectedFiles(ObservableList<JFXInputFile> selectedFiles) {
        this.selectedFiles = selectedFiles;
    }

    private File configFile = new File("MUConfig.json");
    private File outputFolder;

    public static void start(String[] args) {
        launch(args);
    }

    //JFX启动
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Music Unlock");

        initLayout();
        loadPreference();
    }

    //加载配置
    public void loadPreference() {
        try {
            if (!configFile.exists()) {
                writeJson("last_path","",new JSONObject());
                writeJson("last_out_path","",new JSONObject());
                return;
            }
            String json = FileIO.readTextFile(configFile);
            if (!JSONObject.isValid(json)){
                return;
            }
            JSONObject configJSON = JSONObject.parseObject(json);
            String lastPath = configJSON.getString("last_path");
            if (lastPath != null) {
                File folder = new File(lastPath);
                if (folder.exists() && folder.isDirectory()) {
                    readFolder(folder);
                }
            }
            String lastOutPath = configJSON.getString("last_out_path");
            if (lastOutPath != null) {
                controller.setOutputFolder(lastOutPath);
                setOutputFolder(new File(lastOutPath));
            }
        } catch (IOException | MUException e) {
            e.printStackTrace();
        }

    }

    /**
     * 保存配置（记忆本次打开的文件夹）
     */
    public void savePreference(String key, String value) {
        try {
            JSONObject configJson;
            if (configFile.exists()) {
                String string = FileIO.readTextFile(configFile);
                if (JSONObject.isValid(string)){
                    configJson = JSONObject.parseObject(string);
                    writeJson(key, value, configJson);
                }else {
                    configJson = new JSONObject();
                    writeJson(key, value, configJson);
                }
            } else {
                configJson = new JSONObject();
                writeJson(key, value, configJson);
            }

        } catch (IOException | MUException e) {
            e.printStackTrace();
        }

    }

    private void writeJson(String key, String value, JSONObject configJson) throws IOException {
        if (configJson.containsKey(key)) {
            configJson.replace(key, value);
        } else {
            configJson.put(key, value);
        }
        FileIO.writeTextToFile(configFile, configJson.toJSONString());
    }

    //布局初始化
    private void initLayout() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        //载入fxml
        fxmlLoader.setLocation(JFXController.class.getResource("layout/nx.fxml"));
        try {
            rootLayout = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setScene(new Scene(rootLayout));
        primaryStage.show();
        controller = fxmlLoader.getController();
        controller.bindApp(this);
    }

    /**
     * 读取文件夹和其中的文件
     *
     * @param folder 文件夹
     */
    public void readFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                String extension = FileIO.getExtension(file);
                if (extension.equals("ncm") || extension.equals("qmc0") || extension.equals("qmc3")) {
                    JFXInputFile JFXInputFile = new JFXInputFile(file.getName(), extension,
                            FileIO.readCreationTime(file), file);
                    jfxInputFiles.add(JFXInputFile);
                }
            }
            controller.setFolderPath(folder.getAbsolutePath());
        }
    }

    /**
     * 处理
     * 从已选择的JFXInputFiles 遍历
     */
    public void processFiles() {
        //统计时间用
        long seqBeginTime = System.currentTimeMillis();
        int tasks = 0;
        //遍历返回的File数组
        for (JFXInputFile item : selectedFiles) {
            DecryptedFile decryptedFile;
            String fileExt = FileIO.getExtension(item.getFile());
            long beginTime = System.currentTimeMillis();
            try {
                switch (fileExt) {
                    case "ncm":
                        decryptedFile = Ncm.decrypt(FileIO.loadBytes(item.getFile()), Main.getCipher());
                        break;
                    case "qmc0":
                    case "qmc3":
                        decryptedFile = Qmc.decrypt(FileIO.loadBytes(item.getFile()), fileExt);
                        break;
                    default:
                        throw new MUException(MUErrors.FILE_UNSUPPORTED);
                }
                String newFileName = item.getName().substring(0, item.getName().lastIndexOf(".") + 1)
                        + decryptedFile.getFileType().getFileExt();
                FileIO.outputToFolder(outputFolder, newFileName, decryptedFile.getStreamData());
//                FileIO.outputBesideInput(item.getFile(), decryptedFile.getStreamData(), decryptedFile.getFileType());
                long finishTime = System.currentTimeMillis();
                System.out.println("Finished in " + (finishTime - beginTime) + " ms\n");
                System.out.println();
                tasks++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long seqEndTime = System.currentTimeMillis();
        System.out.println(tasks + " task(s) finished in " + (seqEndTime - seqBeginTime) + " ms, average per "
                + ((seqEndTime - seqBeginTime) / tasks));
    }

    /**
     * 打开选择文件夹对话框
     *
     * @param type open / save
     * @return 文件夹的绝对路径
     */
    public String selectFolder(FileType type) {
        File folder = FileIO.chooseFolder(primaryStage);
        if (folder == null || !folder.exists()) {
            return "";
        }
        switch (type) {
            case open:
                savePreference("last_path", folder.getAbsolutePath());
                readFolder(folder);
                break;
            case save:
                setOutputFolder(folder);
                savePreference("last_out_path", folder.getAbsolutePath());
                return outputFolder.getAbsolutePath();
        }
        return folder.getAbsolutePath();
    }

    public void setOutputFolder(File folder) {
        outputFolder = folder;
    }
}
