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
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Xavier
 */
public class KillerShip extends Controlled {

    private Color color;
    private boolean[] moves;

    public KillerShip(KillerGame killerGameObjects, double x, double y, double xy, double yx, double v, boolean state, Color color, int height, int width, String idShip) {
        super(killerGameObjects, x, y, v, xy, yx, height, width, state, idShip);
        this.moves = new boolean[8];//
        this.color = color;
       
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(this.color);
        g.fillOval((int) getX(), (int) getY(), getWidth(), getHeight());

    }

    public void moveX(double move) {
        restardMove();
        if (move > 0) {
            this.moves[0] = true;
        } else {
            this.moves[1] = true;
        }
    }

    public void positionSpaceShip() {

        ArrayList<VisibleObject> asteroids = new ArrayList();
        asteroids = this.getKillerGameObjects().getVisibleObjects();
        Random r = new Random();
        boolean positionFound = false;
        boolean colision = false;
        int imageWidth = this.getKillerGameObjects().getV().getImage().getWidth() - 50;
        int imageHeight = this.getKillerGameObjects().getV().getImage().getHeight() - 50;
        int randomWidth = 0;
        int randomHeight = 0;

        while (!positionFound) {
            colision = false;
            randomWidth = r.nextInt(imageWidth);
            randomHeight = r.nextInt(imageHeight);
            for (int i = 0; i < asteroids.size() && !colision; i++) {
                for (int j = 0; j < 100 && !colision; j++) {
                    int astPosX = (int) (asteroids.get(i).getX()) + j;
                    int astPosXN = (int) (asteroids.get(i).getX()) - j;
                    int astPosY = (int) (asteroids.get(i).getY()) + j;
                    int astPosYN = (int) (asteroids.get(i).getY()) - j;
                    if (astPosX == randomWidth || astPosY == randomHeight || astPosYN == randomHeight || astPosXN == randomWidth) {
                        colision = true;
                    }
                }
            }
            if (!colision) {
                positionFound = true;
            }
        }

        setX((int) randomWidth);
        setY((int) randomHeight);

    }

    public void moveY(double move) {
        restardMove();
        if (move > 0) {
            this.moves[2] = true;
        } else {
            this.moves[3] = true;
        }
    }

    public void moveXY(double move) {
        restardMove();

        if (move > 0) {
            setXy(-1);
            this.moves[4] = true;
        } else {
            setXy(1);
            this.moves[5] = true;
        }
    }

    public void moveYX(double move) {
        restardMove();
        if (move > 0) {
            this.moves[6] = true;
        } else {

            this.moves[7] = true;
        }
    }

    public void restardMove() {
        for (int i = 0; i < this.moves.length; i++) {
            this.moves[i] = false;
        }
    }

    public KillerShoot shoot() {
        boolean estatic = true;
        KillerShoot ks = new KillerShoot(this.getKillerGameObjects(), getX() + getWidth() / 2 - 6, getY() + getHeight() / 2 - 6, getXy(), getYx(), 13, 10, 10, Color.green, true, getId(),-1);
        for (int i = 0; i < this.moves.length; i++) {
            if (this.moves[i] == true) {
                ks.setMove(i);
                estatic = false;
            }
        }
        if (estatic) {
            ks.setMove(3);
        }
        Thread t = new Thread(ks);
        t.start();

        return ks;
    }

    @Override
    public void run() {
        while (isState()) {

            for (int i = 0; i < this.moves.length; i++) {
                this.getKillerGameObjects().colisionTest(this);
                if (this.moves[i] == true) {
                    switch (i) {
                        case 0:
                            setX(2.5 + getX());
                            break;
                        case 1:
                            setX(-2.5 + getX());
                            break;
                        case 2:
                            setY(2.5 + getY());
                            break;
                        case 3:
                            setY(-2.5 + getY());
                            break;
                        case 4:
                            setY(-2 + getY());
                            setX(-2 + getX());
                            break;
                        case 5:
                            setY(+2 + getY());
                            setX(-2 + getX());
                            break;
                        case 6:
                            setY(-2 + getY());
                            setX(+2 + getX());
                            break;
                        case 7:
                            setY(+2 + getY());
                            setX(+2 + getX());
                            break;
                    }
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(KillerShip.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public boolean[] getMoves() {
        return moves;
    }

    public void setMoves(int pos) {
        this.moves[pos] = true;
    }
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
