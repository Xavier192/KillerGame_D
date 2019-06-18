/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package killergame;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Xavier
 */
public class KillerRules {

    private KillerGame killerGameRules;

    public KillerRules(KillerGame killerGameRules) {
        this.killerGameRules = killerGameRules;
    }

    public void rebotarObjeto() {

    }

    public void programCollision(VisibleObject o, VisibleObject o2) {
        if (o2 instanceof KillerShip) {
            this.killerGameRules.destroyKillerShip(o2);
        }
        if (o instanceof Asteroids && o2 instanceof Asteroids) {
            this.killerGameRules.reboundAsteroids((Asteroids)o,(Asteroids)o2);
        }
        if (o instanceof KillerShoot && o2 instanceof Asteroids) {
            this.killerGameRules.destroyAsteroid(o2);
            this.killerGameRules.destroyKillerShoot(o);
           
        }
        if(o instanceof KillerShoot && o2 instanceof KillerShoot){
            this.killerGameRules.destroyKillerShoot(o2);
            this.killerGameRules.destroyKillerShoot(o);
            
        }
    }

    public void bordersColision(VisibleObject object, int colision) {
        //1 right, 2 left, 3 top and 4 bot.

        switch (colision) {
            case 1:
                if(object instanceof KillerShoot){
                    ((KillerShoot) object).setMove(10);
                }
                if (this.killerGameRules.getVisualR().getSocket() != null) {
                    
                    this.killerGameRules.SendAndeliminateObjectRight(object);
                } else {
                    this.killerGameRules.moveObjectLeftFronter(object);
                }
                break;
            case 2:
                if (this.killerGameRules.getVisualL().getSocket() != null) {
                    this.killerGameRules.SendAndeliminateObjectLeft(object);

                } else {
                    this.killerGameRules.moveObjectRightFronter(object);
                }
                break;
            case 3:
                if (object instanceof KillerShip) {

                    object.setY(object.getY() + 1);
                } if(object instanceof Asteroids) {
                    this.killerGameRules.reboundAsteroidsTop((Asteroids)object);
                }
                if(object instanceof KillerShoot){
                    this.killerGameRules.reboundKillerShootTop((KillerShoot)object);
                }
                
                break;
            case 4:
                if (object instanceof KillerShip) {
                    object.setY(object.getY() - 1);
                } if(object instanceof Asteroids) {
                    this.killerGameRules.reboundAsteroidsBottom((Asteroids)object);
                }if(object instanceof KillerShoot){
                    this.killerGameRules.reboundKillerShootBottom((KillerShoot)object);
                }
                break;
            default:
                break;
        }
    }

}
