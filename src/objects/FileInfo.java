package objects;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;

import java.util.concurrent.CopyOnWriteArrayList;

public class FileInfo {

    CopyOnWriteArrayList<Long> pos = new CopyOnWriteArrayList<>();
    Label currTotalLab = new Label("0/0");
    int cpos = -1;
    private FileObject f;
    private TreeItem<FileObject> tItem;
    private Tab tab;
    public FileInfo(FileObject f) {
        this.f = f;
    }

    public CopyOnWriteArrayList<Long> getPos() {
        return pos;
    }

    public Label getCurrTotalLab() {
        return currTotalLab;
    }

    public void setCurrTotalLab(Label currTotalLab) {
        this.currTotalLab = currTotalLab;
    }

    public boolean isEmpty() {
        return pos.isEmpty();
    }

    public Tab getTab() {
        return tab;
    }

    public void setTab(Tab tab) {
        this.tab = tab;
    }

    public TreeItem<FileObject> gettItem() {
        return tItem;
    }

    public void settItem(TreeItem<FileObject> tItem) {
        this.tItem = tItem;
    }

    public void addPos(Long val) {

        pos.add(val);
        updateLabels();
    }

    private void updateLabels() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String s = cpos + "/" + getTotal();
                currTotalLab.setText(s);
            }
        });

    }

    private int getTotal() {
        return pos.size();
    }

    public Long movePos(int shift) {
        cpos += shift;
        Long val;
        try {
            val = pos.get(cpos);
        } catch (IndexOutOfBoundsException e) {
            cpos = 0;
            return -1L;
        }
        updateLabels();
        return val;
    }

    public Long nextPos() {

        return movePos(1);
    }

    public Long prevPos() {

        return movePos(-1);
    }

}