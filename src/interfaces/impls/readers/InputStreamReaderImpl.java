package interfaces.impls.readers;

import controller.MainController;
import interfaces.IFileRead;
import objects.FileObject;


import java.io.*;

public class InputStreamReaderImpl implements IFileRead {

    private String res = "";
    private FileObject cfile;

    public InputStreamReaderImpl(FileObject cfile) {
        this.cfile = cfile;
    }

    @Override
    public void read() {

    }


    @Override
    public String getRes() {
        return res;
    }

    @Override
    public void read(long start, long end) {

        try {



            FileInputStream is = new FileInputStream(cfile);

            Reader fileReader = null;

            try {
                fileReader = new InputStreamReader(is, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            StringBuilder stringBuilder = new StringBuilder();

            fileReader.skip(start);

            int charsRead;
            char buf[] = new char[1];

            //Read until there is no more characters to read.
            int i = 0;
            while ((charsRead = fileReader.read(buf)) > 0) {
                stringBuilder.append(buf, 0, charsRead);
                if (buf[0]=='\n') i++;
                if (i > MainController.NUMLINES) break;
            }

            res = stringBuilder.toString();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public String readLine(String encoding) {
        return null;
    }

    @Override
    public long getFilePointer() throws IOException {
        return 0;
    }
}
