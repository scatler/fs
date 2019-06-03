package objects;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.concurrent.CopyOnWriteArrayList;

public class FileInfo {

    CopyOnWriteArrayList<Long> pos = new CopyOnWriteArrayList<>();
    Label currTotalLab = new Label("0/0");
    int cpos = -1;
    private FileObject f;

    public FileInfo(FileObject f) {
        this.f = f;
    }

    public CopyOnWriteArrayList<Long> getPos() {
        return pos;
    }

    public void setCurrTotalLab(Label currTotalLab) {
        this.currTotalLab = currTotalLab;
    }

    public boolean isEmpty() {
        return pos.isEmpty();
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

}