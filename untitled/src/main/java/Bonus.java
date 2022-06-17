import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Bonus extends Rectangle {

    Random random;

    int type;

    public Bonus(int x, int y, int width, int height) {

        super(x, y, width, height);
        random = new Random();
        type = random.nextInt(10);

    }

    public void draw(Graphics graphics) {
            if(type == 3) {
                graphics.setColor(Color.yellow);
                graphics.fillOval(x, y, width, height);
            }
            else{
                graphics.setColor(Color.cyan);
                graphics.fillOval(x, y, width, height);
            }

    }


}
