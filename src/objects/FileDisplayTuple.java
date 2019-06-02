package objects;

import javafx.scene.control.ScrollBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import objects.util.ThreeTuple;

public class FileDisplayTuple extends ThreeTuple<Tab,ScrollBar,TextArea> {
    public FileDisplayTuple(Tab tab, ScrollBar scrollBar, TextArea textArea) {
        super(tab, scrollBar, textArea);
    }
}
