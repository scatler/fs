package interfaces.impls;

import objects.FileObject;
//from ww w . jav  a 2s. c om
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

class TreeFileBuilder extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private TreeItem<FileObject> createNode(final FileObject f) {
        return new TreeItem<FileObject>(f) {
            private boolean isLeaf;
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;

            @Override
            public ObservableList<TreeItem<FileObject>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;
                    super.getChildren().setAll(buildChildren(this));
                }
                return super.getChildren();
            }

            @Override
            public boolean isLeaf() {
                if (isFirstTimeLeaf) {
                    isFirstTimeLeaf = false;
                    FileObject f = (FileObject) getValue();
                    isLeaf = f.isFile();
                }
                return isLeaf;
            }

            private ObservableList<TreeItem<FileObject>> buildChildren(
                    TreeItem<FileObject> TreeItem) {
                FileObject f = TreeItem.getValue();
                if (f == null) {
                    return FXCollections.emptyObservableList();
                }
                if (f.isFile()) {
                    return FXCollections.emptyObservableList();
                }
                FileObject[] files = f.listFiles();
                if (files != null) {
                    ObservableList<TreeItem<FileObject>> children = FXCollections
                            .observableArrayList();
                    for (FileObject childFile : files) {
                        children.add(createNode(childFile));
                    }
                    return children;
                }
                return FXCollections.emptyObservableList();
            }
        };
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Group(), 300, 300);
        VBox vbox = new VBox();

        TreeItem<FileObject> root = createNode(new FileObject("c:/"));
        TreeView treeView = new TreeView<FileObject>(root);

        vbox.getChildren().add(treeView);
        ((Group) scene.getRoot()).getChildren().add(vbox);

        stage.setScene(scene);
        stage.show();
    }
}