/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package killergame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author Xavier
 */
public class Viewer extends Canvas implements Runnable {

    private BufferedImage image;
    private BufferedImage image2;
    private ArrayList<VisibleObject> visibleObjects;
    private KillerGame killerGameViewer;
    private int height;
    private int width;
    private File img;

    @Override
    public void run() {
        try {
            Thread.sleep(50);

            while (true) {
                try {
                    Thread.sleep(5);
                    bufferStrategy();

                } catch (InterruptedException ex) {
                    Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void bufferStrategy() {
        BufferStrategy bs = getBufferStrategy();
        Graphics2D g;
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics gBufer = bs.getDrawGraphics();

        this.image = copyImage(this.image2);

        g = (Graphics2D) this.image.getGraphics();

        for (int i = 0; i < this.visibleObjects.size(); i++) {
            this.visibleObjects.get(i).render(g);
        }

        gBufer.drawImage(this.image, 0, 0, getWidth(), getHeight(), 0, 0, this.image.getWidth(), this.image.getHeight(), this);

        gBufer.dispose();
        bs.show();
    }

    public Viewer(ArrayList<VisibleObject> visibleObjects, KillerGame killerGameViewer) {
        super();

        this.img = new File(getRandomImage());
        try {
            this.image2 = ImageIO.read(this.img);
            this.image = ImageIO.read(this.img);
        } catch (IOException ex) {
            Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.killerGameViewer = killerGameViewer;
        this.height = (int) this.killerGameViewer.getScreenDimension().getHeight();
        this.width = (int) this.killerGameViewer.getScreenDimension().getWidth();
        this.visibleObjects = visibleObjects;
        setVisible(true);
    }

    public BufferedImage copyImage(BufferedImage b) {
        ColorModel cm = b.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = b.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public String getRandomImage() {
        String randomImage;
        String[] images = new String[7];
        Random r = new Random();
        int pos = 0;

        images[0] = "img/background.jpg";

        for (int i = 1; i < 7; i++) {
            images[i] = "img/background_" + (i + 1) + ".jpg";
        }

        pos = r.nextInt(images.length);
        randomImage = images[pos];

        return randomImage;
    }

    public BufferedImage getImage() {
        return image;
    }

}
