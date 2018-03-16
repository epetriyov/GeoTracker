package upwork.sowl.com.upwork.actions;

/**
 * Created by evgenii on 6/24/17.
 */

public class OpenSettigsAction {

    public OpenSettigsAction() {
    }

    public Exception getException() {

        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    private Exception exception;

    public OpenSettigsAction(Exception e) {
        this.exception = e;
    }
}
