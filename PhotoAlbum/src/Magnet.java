import javax.swing.*;
import java.awt.*;

/**
 * Created by Will on 12/5/2016.
 */
public class Magnet extends JLabel{

    Point coord;
    int tag;

    int index;

    public Magnet(Point coord, int tag) {
        this.coord = coord;
        this.tag = tag;
        String text = "";

        switch (tag) {
            case 0:
                text = "Vacation";
                break;
            case 1:
                text = "Family";
                break;
            case 2:
                text = "School";
                break;
            case 3:
                text = "Work";
                break;
        }
        setText(text);
    }
}
