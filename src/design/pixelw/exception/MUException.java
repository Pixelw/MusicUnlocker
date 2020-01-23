package design.pixelw.exception;

/**
 * @author Carl Su
 * @date 2020/1/16
 */
public class MUException extends Exception {
    public MUException (MUErrors MUErrors){
        super(MUErrors.getDescription());
    }
}
