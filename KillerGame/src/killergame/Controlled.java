/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package killergame;

import java.awt.Color;
import java.awt.Graphics;
import java.nio.file.Path;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Xavier
 */
public abstract class Controlled extends Alive {
    
    public Controlled(KillerGame killerGameObjects,double x, double y,double xy,double yx,double v,int height, int width, boolean state, String id) {
        super(killerGameObjects, x, y,xy,yx, v,height,width, state,id);
    }
    
     @Override
    public void run() {
    
    }
   
    
    
}
