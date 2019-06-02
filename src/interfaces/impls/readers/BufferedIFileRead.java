package interfaces.impls.readers;

import interfaces.IFileRead;
import objects.FileObject;

import java.io.*;

public class BufferedIFileRead implements IFileRead {

    private FileObject file;
    private String searchstring;
    /*String buffer used to store text portion*/
    private String res = "";
    private BufferedReader bf;

    public BufferedIFileRead(FileObject file) {
        this.file = file;
        this.searchstring = searchstring;
        
        try {
            bf =  new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void read(long start, long end) {

        System.out.println("Reading file in thread" + Thread.currentThread());

        String s = "";
        try {


          bf.skip(start);

            for (int i = 0; i < end; i++) {
                s += bf.readLine();
                s += "\n";
            }

            res = s;

        } catch (IOException e) {

            throw new RuntimeException();
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

    @Override
    public void read() {

    }

    @Override
    public String getRes() {
        return res;
    }


}

