package services;

import controller.MainController;
import javafx.concurrent.Task;
import objects.FileObject;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;


public class NodeReviewTask extends Task<Integer> {

    private final MainController mainController;
    private FileObject f;
    private BlockingQueue<FileObject>
            nodesToReview,
            filesToReview;
    private String fileExtension;

    @Override
    protected Integer call() throws Exception {
        if (f != null) { // only for the first task (queue is null)
            return reviewFileSystem(f);
        } else {
            while (true) {

                f = nodesToReview.take();
                System.out.println("-->File " + f + " has been taken by" + Thread.currentThread());
                reviewFileSystem(f);

            }
        }

    }

    public NodeReviewTask(FileObject f, BlockingQueue<FileObject> nodesToReview, BlockingQueue<FileObject> filesToReview, String fileExtension, MainController mainController) {
        this.nodesToReview = nodesToReview;
        this.filesToReview = filesToReview;
        this.fileExtension = fileExtension;
        this.mainController = mainController;
        this.f = f;

    }

    private Integer reviewFileSystem(FileObject f) {
        System.out.println("-node-Review this folder:" + f);
        if (f == null) {
            return 0;
        }
        if (f.isFile()) {

            filesToReview.add(f);

            return 0;
        }
        FileObject[] files = f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File dir) {
                if (!dir.isFile()) return true;
                Pattern pat = Pattern.compile(".*\\."+fileExtension);
                return pat.matcher(dir.getName()).matches();
            }
        });

        if (files.length != 0) {

            for (int i = 0; i < files.length - 1; i++) {


                nodesToReview.add(files[i]);
                System.out.println("Folder/File " + files[i] + " added by  " + Thread.currentThread());
            }

            //last folder
            FileObject last = files[files.length - 1];
            reviewFileSystem(last);

        }

        return 777;

    }
}
