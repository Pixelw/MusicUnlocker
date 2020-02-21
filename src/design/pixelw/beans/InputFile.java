package design.pixelw.beans;

/**
 * @author Carl Su
 * @date 2020/1/27
 */
public class InputFile {
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
