package interfaces.impls.displays;

import controller.MainController;
import interfaces.Display;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.stream.Collectors;


public class TextAreaDisplay implements Display {
    private TextArea disp;
    private MainController mainController;
    private int nlines;
    private int mode;

    /**
     * Reverse output -1, forward 1
     * @param mode
     */

    public void setMode(int mode) {
        this.mode = mode;
    }

    public TextAreaDisplay(TextArea disp, MainController mainController) {
        this.disp = disp;
        this.mainController = mainController;
    }

    @Override
    public int displayText(String s) {



        LinkedList<String> ll = new BufferedReader(new StringReader(s)).lines().collect(Collectors.toCollection(LinkedList::new));
        disp.setText("");
        if (mode == 1) {

            ll.iterator().forEachRemaining(this::setLine);
        } else {

            ll.descendingIterator().forEachRemaining(this::setLine);
        }

        String f = mainController.getTextToFind();
        String t = ((Text) disp.lookup(".text")).getText();
        int pos;
        if ((pos = t.indexOf(f)) != -1) {
            disp.selectRange(pos,pos +f.length());
        }

        return 0;
    }

    private void setLine(String s) {
        s = s + "\n";

        Text t = (Text) disp.lookup(".text");

        double ht;
        ht = t.getBoundsInLocal().getHeight();

        if (ht < disp.getHeight()) {

            if (mode == 1) {
                disp.appendText(s);
            } else  {
                disp.insertText(0,s);
            }


        } else {
            String temp = disp.getText();
            disp.setText(temp.replace(s,""));
        }

    }

    public int getLinesCount() {
        int n = 0;
        double ht = 0;
        String text = disp.getText();
        disp.setText("");
        Text t = (Text) disp.lookup(".text");
        while (ht < disp.getHeight()) {
            disp.appendText("\n");
            ht = t.getBoundsInLocal().getHeight();
            n++;
        }
        disp.setText(text);
        return nlines = n;
    }

    public void insertText(int index, String text) {

        disp.insertText(index, text);

    }
}
