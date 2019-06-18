/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package killergame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Xavier
 */
public class Asteroids extends Autonomus {

    private boolean collisionX;
    private boolean collisionY;

    public Asteroids(KillerGame killerGameObjects, double x, double y, double xy, double yx, double v, int height, int width, boolean state, String id) {
        super(killerGameObjects, x, y, xy, yx, v, height, width, state, id);
        this.collisionX = false;
        this.collisionY = false;
        if (getV() == 0) {
            setV(6);
        }

        if (getWidth() == 0 && getHeight() == 0) {
            int random = (30 + (int) (Math.random() * 30));
            setWidth(random);
            setHeight(random);
        }
    }

    public void run() {
        while (this.isState()) {
            try {
                Thread.sleep((int) (20));
            } catch (InterruptedException ex) {
                Logger.getLogger(Autonomus.class.getName()).log(Level.SEVERE, null, ex);
            }
            moveXBall();
            moveYBall();
            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
                Logger.getLogger(Asteroids.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.getKillerGameObjects().colisionTest(this);
        }
    }

    public void calcularInici() {
        Random a = new Random();
        setX(a.nextInt((int) getKillerGameObjects().getScreenDimension().getWidth()));
        setY(a.nextInt((int) getKillerGameObjects().getScreenDimension().getHeight()));

    }

    public void calcularTrajectoria() {
        Random r = new Random();
        if (r.nextBoolean()) {
            setXy(-getV());
        } else {
            setXy(getV());
        }
        if (r.nextBoolean()) {
            setYx(-getV());
        } else {
            setYx(1);
        }
    }

    public void moveXBall() {
        double xCoor = getX();
        if (getXy() > 0) {
            xCoor += getV();
        } else {
            xCoor -= getV();
        }

        setX(xCoor);
    }

    public void moveYBall() {
        double yCoor = getY();
        if (getYx() > 0) {
            yCoor += getV();
        } else {
            yCoor -= getV();
        }

        setY(yCoor);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.yellow);

        g.fillOval((int) getX(), (int) getY(), getWidth(), getHeight());
    }

    @Override
    public boolean colision(VisibleObject obs) {
        
        Rectangle me = new Rectangle((int) this.getX(), (int) this.getY(), this.getWidth(), this.getHeight());
        Rectangle obstacle = new Rectangle((int) obs.getX(), (int) obs.getY(), obs.getWidth(), obs.getHeight());

        boolean intersection = me.intersects(obstacle);

        if (intersection && obs instanceof Asteroids) {
            Asteroids astObs = (Asteroids) obs;
            if (this.getXy() * astObs.getXy() < 0) {
                this.collisionX = true;
            }
            if (this.getYx() * astObs.getYx() < 0) {
                this.collisionY = true;
            }
        }
        return intersection;
    }

    public boolean isCollisionX() {
        return collisionX;
    }

    public void setCollisionX(boolean collisionX) {
        this.collisionX = collisionX;
    }

    public boolean isCollisionY() {
        return collisionY;
    }

    public void setCollisionY(boolean collisionY) {
        this.collisionY = collisionY;
    }

}
