package at.ac.uibk.igwee.wikidiff;

/**
 * Created by Joseph on 03.06.2016.
 */
public class WikiDiffException extends Exception {

    public WikiDiffException() {
        super();
    }

    public WikiDiffException(String message) {
        super(message);
    }

    public WikiDiffException(String message, Throwable cause) {
        super(message, cause);
    }

    public WikiDiffException(Throwable cause) {
        super(cause);
    }
}
