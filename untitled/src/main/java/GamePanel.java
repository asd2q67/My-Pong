import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable{

    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = (int)(GAME_WIDTH * (0.5555));  // tỉ lệ của bàn bóng bàn trong thực tế
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH,GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;

    static final int BONUS_DIAMETER = 40;
    static final int PADDLE_WIDTH = 20;
    static final int PADDLE_HEIGHT = 100;
    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;
    Paddle paddle2;
    Ball ball;
    Bonus bonus;
    Score score;


    public GamePanel() {
        newPaddles();
        newBall();
        newBonus();
        score  = new Score(GAME_WIDTH,GAME_HEIGHT);
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void newBall() {
        //random = new Random();
        ball = new Ball((GAME_WIDTH/2)-BALL_DIAMETER,(GAME_HEIGHT/2)-BALL_DIAMETER,BALL_DIAMETER,BALL_DIAMETER);
    }

    public void newPaddles() {
        paddle1 = new Paddle(0,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,1);
        paddle2 = new Paddle(GAME_WIDTH-PADDLE_WIDTH,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,2);

    }

    public void newBonus(){
        random = new Random();
        bonus = new Bonus(random.nextInt((GAME_WIDTH - 300)-BONUS_DIAMETER),random.nextInt((GAME_HEIGHT - 300)-BONUS_DIAMETER),BONUS_DIAMETER,BONUS_DIAMETER);
        System.out.print(bonus.type);
    }

    public void paint(Graphics g) {
        image = createImage(getWidth(),getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image,0,0,this);
    }

    public void draw(Graphics g) {
        if(score.player1 >= 10 || score.player2 >= 10) gameOver(g);
        else {
            paddle1.draw(g);
            paddle2.draw(g);
            ball.draw(g);
            score.draw(g);
            if (((score.player1 + score.player2) % 5 == 0) && (score.player1 + score.player2) != 0) {
                bonus.type = 3;

            }
            bonus.draw(g);
        }

    }

    public void move() {
        paddle1.move();
        paddle2.move();
        ball.move();

    }

    public void checkCollision() {
        //stops the ball at the two height edges
        if (ball.y <= 0) {
            ball.setYDirection(-ball.yVelocity);
        }

        if (ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
            ball.setYDirection(-ball.yVelocity);
        }

        //bounces ball off the left paddle
        if (ball.intersects(paddle1)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++; //Tăng tốc độ bóng theo chiều ngang
            if (ball.yVelocity > 0) ball.yVelocity++;
            else ball.yVelocity--;
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }

        //bounces ball off the right paddle
        if (ball.intersects(paddle2)) {
            ball.xVelocity = -Math.abs(ball.xVelocity);
            ball.xVelocity--;
            if (ball.yVelocity > 0) ball.yVelocity++;
            else ball.yVelocity--;
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }



        //stops paddles at window edges
        if (paddle1.y <= 0)
            paddle1.y = 0;
        if (paddle1.y >= (GAME_HEIGHT - PADDLE_HEIGHT)) paddle1.y = GAME_HEIGHT - PADDLE_HEIGHT;

        if (paddle2.y <= 0)
            paddle2.y = 0;
        if (paddle2.y >= (GAME_HEIGHT - PADDLE_HEIGHT)) paddle2.y = GAME_HEIGHT - PADDLE_HEIGHT;

        //give player 1 point and reset the game
        if (ball.x < 0) {
            score.player2++;

            newPaddles();
            newBall();
            System.out.println(score.player2);
        }

        if (ball.x > GAME_WIDTH - BALL_DIAMETER) {
            score.player1++;
            newPaddles();
            newBall();
            System.out.println(score.player1);
        }

        if(ball.intersects(bonus)) doBonus();

    }

    public void doBonus(){
        //Ball touch bonus

            if(bonus.type ==3) {
                ball.xVelocity *=2;
                ball.yVelocity *= 2;
                newBonus();
            }
            else {
                ball.xVelocity -=(Math.abs(ball.xVelocity) / ball.xVelocity) * 2;
                ball.yVelocity -=(Math.abs(ball.yVelocity) / ball.yVelocity) * 2;
                if(Math.abs(ball.xVelocity) < 5 ) { ball.xVelocity = (Math.abs(ball.xVelocity) / ball.xVelocity) * 5; ball.yVelocity = (Math.abs(ball.yVelocity) / ball.yVelocity) * 5;}
                newBonus();
            }
    }

    public void gameOver(Graphics g){

        g.setFont( new Font("Times New Roman",Font.BOLD, 25));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (GAME_WIDTH - metrics2.stringWidth("Game Over"))/2, GAME_HEIGHT/2);
        if(score.player1 > score.player2) {
            g.setColor(Color.red);
            g.drawString("Player Red win", (GAME_WIDTH - metrics2.stringWidth("Game Over")) / 2, GAME_HEIGHT / 2 );
            ball.xVelocity = 0;
            ball.yVelocity = 0;
        }
        else{
                g.setColor(Color.BLUE);
                g.drawString("Player Blue win", (GAME_WIDTH - metrics2.stringWidth("Game Over")) / 2, GAME_HEIGHT / 2 );

                ball.xVelocity = 0;
                ball.yVelocity = 0;
            }
        g.drawString("Press ENTER to continue",GAME_WIDTH /2 - 110,GAME_HEIGHT  - 20);
    }

    public void run() {
        //game loop
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        while (true){
            long now = System.nanoTime();
            delta +=    (now - lastTime)/ns;
            lastTime = now;
            if(delta >=1) {
                move();
                checkCollision();
                repaint();
                delta--;

            }
        }
    }

    public void resetGame(){
        newBall();
        newPaddles();
        newBonus();
        score.player1 = 0;
        score.player2 = 0;
        repaint();
    }
    public class AL extends KeyAdapter {
        public void keyPressed(KeyEvent e) {

                paddle1.keyPressed(e);
                paddle2.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if(score.player1 >= 10 || score.player2 >= 10)
                            resetGame();
                }

        }

        public void keyReleased(KeyEvent e) {
            paddle1.keyReleased(e);
            paddle2.keyReleased(e);
        }
    }
}
