import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Score extends Rectangle {
    static int GAME_WIDTH;
    static int GAME_HEIGHT;
    int player1;
    int player2;

    public Score(int GAME_WIDTH,int GAME_HEIGHT) {
        Score.GAME_WIDTH = GAME_WIDTH;
        Score.GAME_HEIGHT = GAME_HEIGHT;
    }

    public void draw(Graphics graphics){
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Consolas",Font.PLAIN,60));
        graphics.drawLine(GAME_WIDTH/2,0,GAME_WIDTH/2,GAME_HEIGHT);

        graphics.drawString(String.valueOf(player1/10)+String.valueOf(player1%10),(GAME_WIDTH/2)-80,50);
        graphics.drawString(String.valueOf(player2/10)+String.valueOf(player2%10),(GAME_WIDTH/2)+14,50);
    }

}
