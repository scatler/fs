package interfaces;

import java.io.IOException;

public interface IFileRead {
    void read();

    String getRes();

    void read(long start, long end) ;

    String readLine(String encoding);

    long getFilePointer() throws IOException;
}
