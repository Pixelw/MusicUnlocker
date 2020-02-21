package design.pixelw.beans;

import java.io.Serializable;

/**
 * @author Carl Su
 * @date 2020/1/27
 */
public class InputFile implements Serializable {
    private String name;
    private boolean bypass;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBypass() {
        return bypass;
    }

    public void setBypass(boolean bypass) {
        this.bypass = bypass;
    }
}
