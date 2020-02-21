package design.pixelw.beans;

import java.io.Serializable;

/**
 * @author Carl Su
 * @date 2020/1/27
 */
public class InputFile implements Serializable {
    private String name;
    private String extension;
    private long creationTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
