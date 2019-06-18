/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package killergame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Xavier
 */
public class KillerShoot extends Autonomus {

    private Color color;
    private int move;
    private long firstTime;
    
    public KillerShoot(KillerGame killerGameObjects, double x, double y, double xy, double yx, double v, int height, int width, Color color, boolean state, String id,long firstTime) {
        super(killerGameObjects, x, y, xy, yx, v, height, width, state, id);
        this.color = color;
         if (firstTime == -1) {
            this.firstTime = System.currentTimeMillis();
        } else {
            this.firstTime = firstTime;
        }
    }

    @Override
     public void run() {
        long difference=0;
        while (this.isState()) {
            getKillerGameObjects().colisionTest(this);
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(KillerShoot.class.getName()).log(Level.SEVERE, null, ex);
            }
            direction();
            difference = (System.currentTimeMillis() / 1000)-this.firstTime/1000;
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(KillerShoot.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (difference > 6) {
                getKillerGameObjects().destroyKillerShoot(this);
            }
        }
    }

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;

        switch (this.move) {
            case 0:
                setX(getX() + 37);
                setXy(+1);
                setYx(0);
                break;
            case 1:
                setX(getX() - 34);
                setXy(-1);
                setYx(0);
                break;
            case 2:
                setY(getY() + 34);
                setYx(1);
                setXy(0);
                break;
            case 3:
                setY(getY() - 34);
                setYx(-1);
                setXy(0);
                break;
            case 4:
                setY(getY() - 34);
                setX(getX() - 34);
                setXy(-1);
                setYx(-1);
                break;
            case 5:
                setY(getY() + 34);
                setX(getX() - 34);
                setXy(-1);
                setYx(1);
                break;
            case 6:
                setY(getY() - 34);
                setX(getX() + 34);
                setYx(-1);
                setXy(1);
                break;
            case 7:
                setY(getY() + 34);
                setX(getX() + 34);
                setYx(1);
                setXy(1);
                break;
        }
    }

    public void direction() {

        switch (this.move) {
            case 0:
                setX(getV() + getX());
                break;
            case 1:
                setX(-getV() + getX());
                break;
            case 2:
                setY(getV() + getY());
                break;
            case 3:
                setY(-getV() + getY());
                break;
            case 4:
                setY(-getV() + getY());
                setX(-getV() + getX());

                break;
            case 5:
                setY(+getV() + getY());
                setX(-getV() + getX());
                
                break;
            case 6:
                setY(-getV() + getY());
                setX(+getV() + getX());
                break;
            case 7:
                setY(+getV() + getY());
                setX(+getV() + getX());
                break;
            default:
                moveX();
                moveY();
                break;
        }
    }

    @Override
    public void moveX() {
        double xCoor = getX();
        if (getXy() > 0) {
            xCoor += getV();
        } 
        else if(getXy()<0) {
            xCoor -= getV();
        }
        setX(xCoor);
    }

    @Override
    public void moveY() {
        double yCoor = getY();
        if (getYx() > 0) {
            yCoor += getV();
            
        } else if(getYx()<0) {
            yCoor -= getV();
        }

        setY(yCoor);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(this.color);
        g.fillOval((int) getX(), (int) getY(), getWidth(), getHeight());
    }

    public long getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(long firstTime) {
        this.firstTime = firstTime;
    }
    
    

}
