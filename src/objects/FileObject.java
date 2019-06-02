package objects;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;


public class FileObject extends File {


    FileInfo fi = new FileInfo(this);

    public FileInfo getFi() {
        return fi;
    }

    public FileObject(String path) {
        super(path);
    }

    public FileObject(File file) {
        this(file.toString());
    }

    public FileObject[] listFiles() {

        File[] files = super.listFiles();
        FileObject[] fos = new FileObject[files.length];
        for (int i = 0; i < files.length; i++) {
            fos[i] = new FileObject(files[i]);
        }
        return fos;
    }


    public FileObject[] listFiles(FilenameFilter filter) {

        File[] files = super.listFiles(filter);
        FileObject[] fos = new FileObject[files.length];
        for (int i = 0; i < files.length; i++) {
            fos[i] = new FileObject(files[i]);
        }
        return fos;
    }

    public FileObject[] listFiles(FileFilter filter) {

        File[] files = super.listFiles(filter);
        FileObject[] fos = new FileObject[files.length];
        for (int i = 0; i < files.length; i++) {
            fos[i] = new FileObject(files[i]);
        }
        return fos;
    }


    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof File)) return false;
        FileObject fileObject = (FileObject) obj;
        return getAbsolutePath().equals(fileObject.getAbsolutePath());
    }
}
