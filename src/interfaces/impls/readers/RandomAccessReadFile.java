package interfaces.impls.readers;

import controller.MainController;
import interfaces.IFileRead;

import objects.FileObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;


public class RandomAccessReadFile implements IFileRead {


    private String res = "";
    private RandomAccessFile raFile;


    public RandomAccessReadFile(FileObject file) {
        try {
            this.raFile = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void read() {

    }


    public String getRes() {
        return res;
    }


    @Override
    public void read(long start, long nlines) {
        StringBuilder s = new StringBuilder();
        res = "";
        try {


            raFile.seek(start);
            if (start != 0) scrollBack(1, raFile);

            if (start >= raFile.length()) {
                raFile.seek(raFile.length());
                scrollBack(MainController.NUMLINES, raFile);
            }


            String buff = "";
            for (int j = 0; j < nlines; j++) {
                String temp = raFile.readLine();
                if (temp == null) break;

                String utfString = convertToEncoding(temp, "UTF-8");

                s.append(utfString + "\n");

            }

            raFile.close();
            res = s.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private String convertToEncoding(String temp, String charsetName) throws UnsupportedEncodingException {
        byte[] rawBytes = temp.getBytes("ISO-8859-1");

        return new String(rawBytes, charsetName);
    }

    @Override
    public String readLine(String encoding) {
        StringBuilder sb = new StringBuilder();
        int b = 0;
        try {
            if ((b = raFile.read()) != -1) {
                do {

                    sb.append((char) b);
                    if (b == MainController.CR) {
                        return convertToEncoding(sb.toString(), encoding);
                    }
                } while ((b = raFile.read()) != -1);
                return convertToEncoding(sb.toString(), encoding);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getFilePointer() throws IOException {
        return raFile.getFilePointer();
    }

    private int readBack(RandomAccessFile raFile) throws IOException {
        int b = 0;
        long pos = this.raFile.getFilePointer() - 1;
        if (pos < 0) return -1;
        raFile.seek(pos);
        b = this.raFile.read();
        raFile.seek(pos);
        return b;
    }


    private void scrollBack(int nlines, RandomAccessFile raFile) throws IOException {
        int i = 0;
        int b;
        while (i < nlines) {
            b = readBack(raFile);
            if (b == -1) return;
            if (b == '\n') i++;
        }
        if (raFile.getFilePointer() != 0) {

            raFile.seek(raFile.getFilePointer() + 1);
        }
    }


}
