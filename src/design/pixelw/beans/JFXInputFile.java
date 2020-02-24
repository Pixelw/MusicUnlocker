package design.pixelw.beans;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.File;

/**
 * @author Carl Su
 * @date 2020/2/22
 */
public class JFXInputFile {
    public File getFile() {
        return file;
    }

    private File file;
    private final SimpleStringProperty name;
    private final SimpleStringProperty type;
    private final SimpleLongProperty time;
    

    public JFXInputFile(String name, String type, long time,File file) {
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);
        this.time = new SimpleLongProperty(time);
        this.file = file;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getType() {
        return type.get();
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public long getTime() {
        return time.get();
    }

    public SimpleLongProperty timeProperty() {
        return time;
    }

    public void setTime(long time) {
        this.time.set(time);
    }
}
