/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package killergame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Xavier
 */
public class Panel extends Static implements Renderizable {

    int port;
    String ip;
    String state;

    public Panel(KillerGame killerGameObjects, double x, double y, int height, int width, boolean state, String id) {
        super(killerGameObjects, x, y, height, width, state, id);
        this.port = 0;
        this.ip = "0";
        this.state = "Disconnected";
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
            }
            getKillerGameObjects().detectConnection(this);
            
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setFont(new Font("Verdana", Font.PLAIN, 20));
        g.setColor(Color.GREEN);

        g.drawString("Visual Handler " + getId(), (int) getX(), (int) getY());
        g.drawString("Port: " + getPort(), (int) getX() + 10, (int) getY() + 50);
        g.drawString("IP: " + getIp(), (int) getX() + 10, (int) getY() + 100);
        g.drawString("State: " + getState(), (int) getX() + 10, (int) getY() + 150);

    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
