package design.pixelw;

import design.pixelw.beans.JFXInputFile;
import design.pixelw.utils.FileType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * @author Carl Su
 * @date 2020/1/22
 */
public class JFXController {
    @FXML
    public TableView<JFXInputFile> filesTable;
    @FXML
    private TableColumn<JFXInputFile, String> collName;
    @FXML
    private TableColumn<JFXInputFile, String> collType;
    @FXML
    private TableColumn<JFXInputFile, Long> collTime;
    @FXML
    public Hyperlink selectFolder;
    @FXML
    public Hyperlink outputTo;

    private App app;

    public void bindApp(App app) {
        this.app = app;
        filesTable.setItems(app.getJfxInputFiles());
        filesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setFolderPath(String path){
        selectFolder.setText(path);
    }
    public void setOutputFolder(String path){
        if (path != null){
            String string = "输出到:\n" + path;
            outputTo.setText(string);
        }
    }

    @FXML
    private void initialize() {
        System.out.println("JFX initialize");
        collName.setCellValueFactory(new PropertyValueFactory<>("name"));
        collType.setCellValueFactory(new PropertyValueFactory<>("type"));
        collTime.setCellValueFactory(new PropertyValueFactory<>("time"));

    }

    @FXML
    public void openFolder(ActionEvent actionEvent) {
        app.selectFolder(FileType.open);
    }

    @FXML
    public void saveToFolder(ActionEvent actionEvent) {
        String path = app.selectFolder(FileType.save);
        if (path != null){
            String string = "输出到:\n" + path;
            outputTo.setText(string);
        }
    }

    @FXML
    public void convert(ActionEvent actionEvent) {
        app.setSelectedFiles(filesTable.getSelectionModel().getSelectedItems());
        app.processFiles();
    }
}
