package jops;

import java.io.File;

public class LoadRequest implements Request {

    private File selectedFile;

    public LoadRequest(File inSelectedFile) {
	this.selectedFile = inSelectedFile;
    }

    @Override
    public String toString() {
	return "load \"" + this.selectedFile.getPath() + "\"";
    }
}
