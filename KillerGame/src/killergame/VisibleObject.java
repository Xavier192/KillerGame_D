/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package killergame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author Xavier
 */
public abstract class VisibleObject implements Renderizable,Collisionable {
private double x;
private double y;
private int height;
private int width;
private String id;
private boolean state;
private KillerGame killerGameObjects;


    public VisibleObject(KillerGame killerGameObjects, double x, double y,int height, int width, boolean state,String id) {
    this.x=x;
    this.y=y;
    this.height=height;
    this.width=width;
    this.state=state;
    this.killerGameObjects=killerGameObjects;
    this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
   
    @Override
    public boolean colision(VisibleObject obs){
        Rectangle me = new Rectangle((int)this.x, (int)this.y, this.width, this.height);
        Rectangle obstacle = new Rectangle((int)obs.x, (int)obs.y, obs.width, obs.height);
        
        return me.intersects(obstacle);
    }
    
    public void destroyObject(){
        this.state=false;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public KillerGame getKillerGameObjects() {
        return killerGameObjects;
    }

    public void setKillerGameObjects(KillerGame killerGameObjects) {
        this.killerGameObjects = killerGameObjects;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
    
    

    @Override
    public void render(Graphics2D g) {
        
    }

   

    
    
    
}
