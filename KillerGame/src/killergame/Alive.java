/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package killergame;

/**
 *
 * @author Xavier
 */
public abstract class Alive extends VisibleObject implements Runnable {
    private double v;
    private double xy;
    private double yx;
    public Alive(KillerGame killerGameObjects,double x, double y,double xy, double yx,double v, int height, int width, boolean state, String id) {
        super(killerGameObjects, x,y,height,width,state,id);
        this.v=v;
        this.xy=xy;
        this.yx=yx;
    }

    @Override
    public void run() {
        
    }

    public double getV() {
        return v;
    }

    public void setV(double v) {
        this.v = v;
    }

    public double getXy() {
        return xy;
    }

    public void setXy(double xy) {
        this.xy = xy;
    }

    public double getYx() {
        return yx;
    }

    public void setYx(double yx) {
        this.yx = yx;
    }
    
    
}
