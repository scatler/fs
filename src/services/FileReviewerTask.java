package services;

import controller.MainController;
import interfaces.IFileRead;
import javafx.application.Platform;
import javafx.concurrent.Task;
import objects.FileObject;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FileReviewerTask extends Task<Integer> {

    private final MainController mC;
    private IFileRead reader;

    private String textToFind;
    private BlockingQueue<FileObject>
            filesToReview = new LinkedBlockingQueue<>(),
            filesToSet = new LinkedBlockingQueue<>();
    private FileObject file;

    public FileReviewerTask(String textToFind, BlockingQueue<FileObject> filesToReview, BlockingQueue<FileObject> filesToSet, MainController mainController) {
        this.textToFind = textToFind;
        this.filesToReview = filesToReview;
        this.filesToSet = filesToSet;
        this.mC = mainController;
    }

    public void setTextToFind(String textToFind) {
        this.textToFind = textToFind;
    }

    public void setFilesToSet(BlockingQueue<FileObject> filesToSet) {
        this.filesToSet = filesToSet;
    }


    @Override
    protected Integer call() throws Exception {

        while (true) {

            file = filesToReview.take();

            fileReview(file);

        }


    }


    public void fileReview(FileObject f) {
        try {
            reader = mC.makeReader(f);
            String output = new String("");
            long pos = 0;
            boolean firstTime = true;
            while ((output = reader.readLine(MainController.ENCODING)) != null) {

                if (output.contains(textToFind)) {
                    long position = reader.getFilePointer() - output.getBytes(MainController.ENCODING).length;
                    f.getFi().addPos(position);
                    System.err.println("Positions added" + position + " in file " + f);

                    if (firstTime) {
                        firstTime = false;
                        filesToSet.add(f);
                        if (textToFind.equals("")) {
                            f.getFi().addPos(position);

                            return;
                        }
                        int i = MainController.foundFiles.addAndGet(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                mC.statusLabel.setText("Files found:" + MainController.foundFiles);

                            }
                        });
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "FileReviewerTask{}";
    }
}
