/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package killergame;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Xavier
 */
public abstract class Static extends VisibleObject implements Runnable {

    public Static(KillerGame killerGameObjects,double x,double y, int height, int width, boolean state, String id) {
        super(killerGameObjects,x,y,height,width,state, id);
    }

    @Override
    public void run() {
        
    }
    
   
   
}
