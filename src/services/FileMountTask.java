package services;

import controller.MainController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import objects.FileObject;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FileMountTask extends Task<Void> {
    private TreeView<FileObject> tv;
    private FileObject file;
    private BlockingQueue<FileObject>
            filesToSet = new LinkedBlockingQueue<>();
    private FileObject root;
    private MainController mainController;
    private LinkedList<FileObject> parentsList;


    public FileMountTask(TreeView<FileObject> tv, BlockingQueue<FileObject> filesToSet, FileObject root, MainController mainController) {
        this.tv = tv;
        this.filesToSet = filesToSet;
        this.root = root;
        this.mainController = mainController;
    }


    public void start() {
        try {
            call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Void call() throws Exception {

        do  {
            file = filesToSet.take();

            mountFile(file);
            //if (filesToSet.isEmpty()) return null;
        } while (true);

       // return null;

    }

    private Void mountFile(FileObject f) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                parentsList = makeParentList(f);
                if (tv.getRoot() == null) {

                    tv.setRoot(new TreeItem<>(root));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                TreeItem<FileObject> ti = tv.getRoot();
                ti.setExpanded(true);
                insertListInTree(ti, parentsList);
            }
        });


        return null;
    }


    private class Images {
        ImageView File () {
            return new ImageView(new Image("pics/file_16x16.png"));
        }
        ImageView Folder () {
            return new ImageView(new Image("pics/folder_16x16.png"));
        }
    }
    /**
     * @param ti   starting element of the tree
     * @param list higher folders come first (Root ->Folder1->Folder2->File). Root should not be included in list
     */


    //TODO make generic method, because ti and list are not of the same type, that is weird
    private void insertListInTree(TreeItem<FileObject> ti, LinkedList<FileObject> list) {

        TreeItem<FileObject> pti;

        for (FileObject f : list
                ) {
            pti = ti;
            ti = searchTree(pti, f);
            if (ti == null) {
                if (f.isFile()) {
                    ti = new TreeItem<>(f,new Images().File());
                } else {
                    ti = new TreeItem<>(f,new Images().Folder());
                }

                ti.setExpanded(true);
                pti.getChildren().add(ti);


            }
        }
    }

    private LinkedList<FileObject> makeParentList(FileObject f) {

        if (f == null) return null;

        LinkedList<FileObject> q = new LinkedList<>();
        File par;
        q.add(f);
        while (true) {
            f = q.getFirst();
            par = f.getParentFile();
            if (par.equals(root)) {
                return q;
            } else {

                q.push(new FileObject(par));
            }

        }


    }

    /**
     * Using BFS
     *
     * @param ti - start point for searching
     * @param f  - fileObject to find in tree
     * @return null if not found, element of tree if found
     */
    private TreeItem<FileObject> searchTree(TreeItem<FileObject> ti, FileObject f) {

        if (ti == null || f == null) return null;
        LinkedList<TreeItem<FileObject>> queue = new LinkedList<>();
        queue.add(ti);

        while (!queue.isEmpty()) {
            ti = queue.poll();
            if (ti.getValue().equals(f))
                return ti; //this line position ensures condition checking for the topmost element (root)

            Iterator<TreeItem<FileObject>> iter = ti.getChildren().iterator();

            while (iter.hasNext()) {

                queue.add(iter.next());
            }


        }

        return null;
    }

}
