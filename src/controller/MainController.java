package controller;

import interfaces.Display;
import interfaces.IFileRead;
import interfaces.impls.displays.TextAreaDisplay;
import interfaces.impls.readers.RandomAccessReadFile;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import objects.FileObject;
import services.FileMountTask;
import services.FileReviewerTask;
import services.NodeReviewTask;

import java.net.URL;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class MainController implements Initializable {
    //TODO delete test data
    private static final String FILE_PATH = "D:\\From int\\pwned-passwords-ntlm-ordered-by-hash-v4\\3-Кириллица\\Последняя новость из Lenta.txt";
    private static final long READ_BUFF = 2000;
    public static final int NUMLINES = 200;
    public static final int CR = 10;
    public static final String ENCODING = "UTF-8";
    public static volatile AtomicInteger foundFiles = new AtomicInteger(0);
    private final Node rootIcon =
            new ImageView(new Image(("pics/folder_16x16.png")));
    public TextField inputTextToFind;
    public TextField inputSearchPath;
    public TextField inputFileExtension;
    public Label statusLabel;
    public Label filesUnderReview;

    public TabPane tabPaneCntrl;
    public Button browseBtn;
    public Button btnStop;


    private FileObject searchPath = new FileObject("C:\\");
    private ListIterator<Long> it;

    public String getTextToFind() {
        return textToFind;
    }

    private String textToFind = "";
    private FileObject f;

    private BlockingQueue<FileObject>
            nodesToReview = new LinkedBlockingQueue<>(),
            filesToReview = new LinkedBlockingQueue<>(),
            filesToSet = new LinkedBlockingQueue<>();
    @FXML
    private Button openTestFile;
    @FXML
    private ScrollBar scrollBar;
    @FXML
    private TextArea firstTextDisplay;
    @FXML
    private TreeView<FileObject> treeViewer;

    private FileObject cfile; //current file
    private Display cdisp; // current display
    private String fileExtension;
    private ExecutorService exec;
    private ExecutorService nodeReview;
    private ExecutorService fileReview;
    private IFileRead fileReader;
    private boolean firstTime = true;
    private Map<FileObject, Tab> mapTabFile = new HashMap<>();
    public final Map<FileObject, List<Integer>> curIndex = new HashMap<>(); //unvisited number/total/current

    public Node getRootIcon() {
        return rootIcon;
    }

    private Display getTextDisplay() {
        return cdisp;
    }

    private void setTextDisplay(TextArea ta) {
        cdisp = new TextAreaDisplay(ta, this);
    }

    public IFileRead makeReader(FileObject cfile) {
        return new RandomAccessReadFile(cfile);
    }

    public Display makeDisplay(TextArea ta) {
        return new TextAreaDisplay(ta, this);
    }

    public void actionButtonPressed(ActionEvent actionEvent) {

        Object source = actionEvent.getSource();
        if (!(source instanceof Button)) return;

        Button btnClicked = (Button) source;
        switch (btnClicked.getId()) {
            case "btnInsertText":
                scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                    }
                });
                String s = "Some test text";
                insertText(s);
                break;
            case "btnOpenTestFile":
                fileOpened();
                break;


        }

    }

    private void insertText(String s) {
        TextAreaDisplay txtdisp = (TextAreaDisplay) getTextDisplay();

        txtdisp.insertText(0, s);

    }


    //for debug purpose
    public void fileOpened() {

        cfile = new FileObject(FILE_PATH);
        fileReader = makeReader(cfile);

        show(0, NUMLINES, cdisp, fileReader, cfile);

    }


    private void fileOpened(FileObject value) {

        cfile = value;
        cdisp = getTextDisplay();
        fileReader = makeReader(cfile);
        scrollBar.setValue(cfile.getFi().nextPos());
        show((long) scrollBar.getValue(), NUMLINES, cdisp, fileReader, cfile);


    }

    public void scrollStarted(ScrollEvent scrollEvent) {

        show((long) scrollBar.getValue(), NUMLINES, cdisp, fileReader, cfile);

    }

    private void show(long start, long numlines, Display cdisp, IFileRead fileReader, FileObject cfile) {

        fileReader.read(start, numlines);
        System.out.println(fileReader.getRes());
        cdisp.setMode(1);
        if (start >= cfile.length()) cdisp.setMode(-1);
        cdisp.displayText(fileReader.getRes());


    }


    public void searchStart(MouseEvent mouseEvent) {
        beginSearch();
    }

    private void beginSearch() {

        //init section


        //end of init section

        fileExtension = inputFileExtension.getText();
        textToFind = inputTextToFind.getText();
        searchPath = new FileObject(inputSearchPath.getText());

        //TODO replace with methods
        if (treeViewer.getRoot() != null) {

            shutdownServices();


        }


        //scan file system for the first time
        exec = Executors.newFixedThreadPool(1);
        exec.submit(new NodeReviewTask(searchPath, nodesToReview, filesToReview, fileExtension, this));

        nodeReview = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 1; i++) {
            nodeReview.submit(new NodeReviewTask(null, nodesToReview, filesToReview, fileExtension, this));

        }

        fileReview = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            fileReview.submit(new FileReviewerTask(textToFind, filesToReview, filesToSet, this));

        }


        ExecutorService fileSet = Executors.newFixedThreadPool(1);
        fileSet.submit(new FileMountTask(treeViewer, filesToSet, searchPath, this));


    }

    private void shutdownServices() {
        exec.shutdownNow();
        nodeReview.shutdownNow();
        fileReview.shutdownNow();
        treeViewer.setRoot(null);
        foundFiles.set(0);
    }

    private void treeInit() {

        treeViewer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    TreeItem<FileObject> tfo = treeViewer.getSelectionModel().getSelectedItem();
                    FileObject fo = tfo.getValue();
                    if (!fo.isFile()) return;

                    String name = fo.toString();


                    if (!mapTabFile.containsKey(fo)) {
                        constructTab(fo, name, tfo);
                    } else {
                        tabPaneCntrl.getSelectionModel().select(mapTabFile.get(fo));

                    }


                }
            }
        });


    }

    private void constructTab(FileObject fo, String name, TreeItem<FileObject> tfo) {


        System.out.println(fo.getFi().getPos());
        //Front-end
        Tab tb = new Tab(name);
        tabPaneCntrl.getTabs().add(tb);
        VBox vb = new VBox();
        TextArea ta = new TextArea();
        Display dis = makeDisplay(ta);
        Label currTotalLab = new Label("0/0");

        fo.getFi().setCurrTotalLab(currTotalLab);

        //Back-end
        ObservableList<Long> visited = FXCollections.observableArrayList();

        //Control buttons
        HBox ctrlBtnHBox = new HBox();
        Button fBtn, bBtn, allBtn;
        fBtn = new Button(">");
        bBtn = new Button("<");
        allBtn = new Button("SelectAll");
        ctrlBtnHBox.getChildren().addAll(fBtn, bBtn, allBtn);
        ctrlBtnHBox.getChildren().add(currTotalLab);
        int i = 0;

        //Configure buttons

        fBtn.setOnMouseClicked(event -> {
            scrollToPosition(fo, dis,1);
        });


        bBtn.setOnMouseClicked(event -> {
            scrollToPosition(fo, dis,-1);
        });


        //Display
        HBox tabHBox = new HBox();
        ta.setEditable(false);
        ta.setMouseTransparent(true);

        //Scroll bar
        ScrollBar bar = new ScrollBar();
        bar.setOrientation(Orientation.VERTICAL);
        bar.valueProperty().addListener((observable, oldValue, newValue) -> {
            show(newValue.longValue(), NUMLINES, makeDisplay(ta), makeReader(fo), fo);

        });


        //Flex interface
        VBox.setVgrow(vb, Priority.ALWAYS);
        HBox.setHgrow(ta, Priority.ALWAYS);
        VBox.setVgrow(tabHBox, Priority.ALWAYS);


        tabHBox.getChildren().addAll(ta, bar);


        bar.setMax(fo.length());
        bar.setUnitIncrement(fo.length() / 200);
        vb.getChildren().addAll(tabHBox, ctrlBtnHBox);
        tb.setContent(vb);
        tabPaneCntrl.setVisible(true);

        ta.heightProperty().addListener((observable, oldValue, newValue) -> {

            if (!fo.getFi().isEmpty()) {
                bar.setValue(fo.getFi().nextPos());
            } else {
                show(0, NUMLINES, makeDisplay(ta), makeReader(fo), fo);

            }
        });


        mapTabFile.put(fo, tb);


    }

    private void scrollToPosition(FileObject fo, Display dis, int shift) {
        Long val = 0L;
        IFileRead reader = makeReader(fo);
        val = fo.getFi().movePos(shift);
        if (val == -1L) {
            //TODO add message box
            return;
        }
        show(val, NUMLINES, dis, reader, fo);
    }

    private void clearTree(TreeItem<FileObject> root) {
        LinkedList<TreeItem<FileObject>> q = new LinkedList<>();
        q.push(root);
        while (!q.isEmpty()) {
            root = q.poll();

            q.addAll(root.getChildren());
            root.getChildren().clear();


        }
    }

    public void clearTree(ActionEvent actionEvent) {

        clearTree(treeViewer.getRoot());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tabPaneCntrl.setVisible(false);
        btnStop.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                shutdownServices();
            }
        });

        browseBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser fc = new FileChooser();
                fc.setTitle("Pick a path");

                fc.showOpenDialog(((Button) event.getSource()).getScene().getWindow());
            }
        });
        treeInit();
    }


    ;
}



