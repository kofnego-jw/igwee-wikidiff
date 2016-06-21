package at.ac.uibk.igwee.wikidiff.web;

/**
 * Container class for an uploaded file.
 *
 * Created by Joseph on 16.06.2016.
 */
public class FileContainer {

    public String filename;

    public byte[] contents;

    public FileContainer() {
    }

    public FileContainer(String filename, byte[] contents) {
        this.filename = filename;
        this.contents = contents;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }
}
