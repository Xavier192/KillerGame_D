/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package killergame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Xavier
 */
public class KillerGame extends JFrame implements ActionListener {

    private Dimension screenDimension;
    private KillerServer server;
    private ArrayList<VisibleObject> visibleObjects;
    private KillerRules killerRules;
    private Viewer v;
    private ArrayList<KillerPad> killerPads;
    private VisualHandler visualL;
    private VisualHandler visualR;
    private JTextField ipVisualHandlerRight;
    private JTextField ipVisualHandlerLeft;
    private JTextField portVisualLeft;
    private JTextField portVisualRight;
    private JLabel ipVR;
    private JLabel ipL;
    private JLabel portVHR;
    private JLabel portVHL;
    private JButton submitButtonR;
    private JButton submitButtonL;

    private JFrame ConnectionWindow;

    public KillerGame() {
        super("KillerGame");
        initFrame();
        this.visibleObjects = new ArrayList();
        this.killerPads = new ArrayList();
        this.visualR = new VisualHandler(this);
        this.visualL = new VisualHandler(this);
        this.server = new KillerServer(this);
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "True");
        KillerGame kg = new KillerGame();
        kg.start();
    }

    public void initFrame() {
	//JOptionPane to get the number of screens from user.
        String screen = JOptionPane.showInputDialog("How many screens do you want to have?");
        int screens = Integer.parseInt(screen);
	//Getting the size of the screen.
        this.screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
	//Setting the size of the window at the JFrame.
        setSize((int) this.screenDimension.getWidth() / screens, 
	(int) (this.screenDimension.getHeight() - 50) / screens);
	//What to do if the window is closed.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//Executed methods on window close.
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                destroyShipsFromPads();
                searchAndDestroyAnySpaceShip();
            }
        });
        connectionWindow(screens);
    }
    
    public void destroyShipsFromPads(){
        for (int i = 0; i < this.killerPads.size(); i++) {
            this.killerPads.get(i).destroyKillerPad();
        }
    }

    public void createPanels() {
        Panel leftPanel = new Panel(this, 30, 60, 0, 0, true, "left");
        Panel rightPanel = new Panel(this, this.v.getImage().getWidth() - 230, 60, 0, 0, true, "right");
        Thread t = new Thread(leftPanel);
        t.start();
        Thread t2 = new Thread(rightPanel);
        t2.start();

        this.visibleObjects.add(leftPanel);
        this.visibleObjects.add(rightPanel);
    }

    public void searchAndDestroyAnySpaceShip() {
        ArrayList<KillerShip> arrayShip = new ArrayList();

        for (int i = 0; i < this.visibleObjects.size(); i++) {
            if (this.visibleObjects.get(i) instanceof KillerShip) {
                arrayShip.add((KillerShip) this.visibleObjects.get(i));
            }
        }

        for (int i = 0; i < arrayShip.size(); i++) {
            this.destroyKillerShip(arrayShip.get(i));
        }
    }

    public void createAsteroids(int i) {
        Asteroids ast = new Asteroids(this, 0, 0, 0, 0, 0, 0, 0, true, Integer.toString(i));
        ast.calcularInici();
        ast.calcularTrajectoria();
        Thread asteroidThread = new Thread(ast);
        asteroidThread.start();
        this.visibleObjects.add(ast);
    }
    
    public void initGame(){
        int timeCounterAst=0;
        int timeCounterCon=0;
        int asteroidNumber=0;
        while(true){
            asteroidNumber=this.countAsteroids();
            if(timeCounterCon%10==0){
               killShipOnDisconnect(); 
            }
            if(timeCounterAst%((asteroidNumber+1)*100)==0 && asteroidNumber<4){
                this.createAsteroids(asteroidNumber);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(KillerGame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            timeCounterAst++;
            timeCounterCon++;
        }
    }

    public ArrayList<Asteroids> positionSpaceShip() {
        ArrayList<Asteroids> asteroids = new ArrayList();
        for (int visibleObject = 0; visibleObject < this.visibleObjects.size(); visibleObject++) {
            if (this.visibleObjects.get(visibleObject) instanceof Asteroids) {
                asteroids.add((Asteroids) this.visibleObjects.get(visibleObject));
            }
        }
        return asteroids;
    }

    public void start() {
        boolean infinite = true;
        createViewer();
        createKillerRules();
        createPanels();
        Thread visualL = new Thread(this.visualL);
        Thread visualR = new Thread(this.visualR);
        visualL.start();
        visualR.start();
        Thread threadServer = new Thread(this.server);
        threadServer.start();
        setVisible(true);

        initGame();
    }

    public void killShipOnDisconnect() {
        ArrayList <Panel> panels=new ArrayList();
        for (int i = 0; i < this.visibleObjects.size(); i++) {
            if (this.visibleObjects.get(i) instanceof Panel) {
               panels.add((Panel)this.visibleObjects.get(i));
            }
        }
        if(panels.get(0).getState().equals("Reconnecting") && panels.get(1).getState().equals("Reconnecting") && this.killerPads.isEmpty()){
            this.searchAndDestroyAnySpaceShip();
        }
    }

    public void createViewer() {
        this.v = new Viewer(this.visibleObjects, this);
        add(this.v);
        Thread threadViewer = new Thread(this.v);
        threadViewer.start();

    }

    public void createKillerPad(Socket clientSock, String info) {
        KillerPad killerPad = new KillerPad(clientSock, this);
        killerPad.prepareSpaceShip(info);
        Thread t = new Thread(killerPad);
        this.killerPads.add(killerPad);
        t.start();
    }

    public void createSpaceShip(Color c, double x, double y, boolean state, String id) {
        KillerShip killerShip = new KillerShip(this, x, y, 0, 0, 0, true, c, 50, 50, id);
        killerShip.setState(true);
        killerShip.positionSpaceShip();
        Thread threadKillerShip = new Thread(killerShip);
        threadKillerShip.start();
        this.visibleObjects.add(killerShip);
    }

    public void moveSpaceShip(String direction, String idShip) {
        KillerShip spaceship = searchSpaceShip(idShip);
        if (spaceship != null) {
            switch (direction) {
                case "left":
                    spaceship.moveX(-1);
                    break;
                case "right":
                    spaceship.moveX(1);
                    break;
                case "up":
                    spaceship.moveY(-1);
                    break;
                case "down":
                    spaceship.moveY(1);
                    break;
                case "upleft":
                    spaceship.moveXY(1);
                    break;
                case "downleft":
                    spaceship.moveXY(-1);
                    break;
                case "upright":
                    System.out.println("Moving rightup");
                    spaceship.moveYX(1);
                    break;
                case "downright":
                    spaceship.moveYX(-1);
                    break;
                case "stop":
                    spaceship.restardMove();
                    break;
                case "shoot":
                    createKillerShoot(spaceship);
                    break;
                default:
                    break;
            }
        } else {
            System.out.println("Impossible to " + direction + " There's no ship");
        }
    }

    public KillerShip searchSpaceShip(String idShip) {
        KillerShip kp = null;
        for (int i = 0; i < this.visibleObjects.size(); i++) {
            if (this.visibleObjects.get(i) instanceof KillerShip && ((KillerShip) this.visibleObjects.get(i)).getId().equalsIgnoreCase(idShip)) {
                kp = (KillerShip) this.visibleObjects.get(i);
            }
        }
        return kp;
    }

    public void connectionWindow(int screens) {
        this.ConnectionWindow = new JFrame("Connection window");
        this.ConnectionWindow.toFront();
        this.ConnectionWindow.setSize(900 / screens, 100);
        this.ConnectionWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();

        GridLayout grid = new GridLayout(2, 5);
        grid.setHgap(4);
        grid.setVgap(4);
        this.ipVisualHandlerRight = new JTextField(20);
        this.ipVisualHandlerLeft = new JTextField(20);
        this.ipL = new JLabel("Left visual handler ip");
        this.ipVR = new JLabel("Right visual handler ip");
        this.portVisualLeft = new JTextField(20);
        this.portVisualRight = new JTextField(20);
        this.portVHL = new JLabel("Left visual handler port");
        this.portVHR = new JLabel("Right visual handler port");
        this.submitButtonR = new JButton("Submit R");
        this.submitButtonR.addActionListener(this);
        this.submitButtonL = new JButton("Submit L");
        this.submitButtonL.addActionListener(this);

        panel.setLayout(grid);
        panel.add(this.ipVR);
        panel.add(this.ipVisualHandlerRight);

        panel.add(this.portVHR);
        panel.add(this.portVisualRight);

        panel.add(this.submitButtonR);

        panel.add(this.ipL);
        panel.add(this.ipVisualHandlerLeft);

        panel.add(this.portVHL);
        panel.add(this.portVisualLeft);

        panel.add(this.submitButtonL);

        this.ConnectionWindow.add(panel);
        this.ConnectionWindow.setVisible(true);
    }

    public void createKillerRules() {
        this.killerRules = new KillerRules(this);
    }

    public void createSpaceShipNewScreen(int width, int height, double x, double y, double xy, double yx, int state, String idShip, int dir, String color) {
        Color c = null;
        switch (color) {
            case "Black":
                c = Color.BLACK;
                break;
            case "Blue":
                c = Color.blue;
                break;
            case "Red":
                c = Color.red;
                break;
            case "Green":
                c = Color.green;
                break;
            case "Brown":
                c = Color.YELLOW;
                break;
            default:
                break;
        }

        KillerShip invaderShip = new KillerShip(this, x, y, xy, yx, 0, true, c, height, width, idShip);
        invaderShip.setMoves(dir);
        Thread threadInvaderShip = new Thread(invaderShip);
        threadInvaderShip.start();
        System.out.println("Creating ship new screen");
        this.visibleObjects.add(invaderShip);
    }

    public void createAsteroidNewScreen(double x, double y, double xy, double yx, double v, int height, int width, boolean state) {
        Asteroids asteroid = new Asteroids(this, x, y, xy, yx, v, height, width, state, Integer.toString(countAsteroids() + 1));
        Thread threadNewAsteroid = new Thread(asteroid);
        threadNewAsteroid.start();
        System.out.println("Asteroid created");

        this.visibleObjects.add(asteroid);
    }

    public void createShootNewScreen(double y, int width, int height, boolean state, double v, String idShoot, double xy, double yx, double x, int move,long firstTime) {

        KillerShoot killershoot = new KillerShoot(this, x, y, xy, yx, v, height, width, Color.green, state, idShoot,firstTime);
        killershoot.setMove(10);
        Thread threadShoot = new Thread(killershoot);
        threadShoot.start();

        System.out.println("Creating shoot new Screen");

        this.visibleObjects.add(killershoot);
    }

    public int countAsteroids() {
        int counter = 0;
        for (int i = 0; i < this.visibleObjects.size(); i++) {
            if (this.visibleObjects.get(i) instanceof Asteroids) {
                counter++;
            }
        }
        return counter;
    }

    public void SendAndeliminateObjectRight(VisibleObject object) {
        object.setX(0 - object.getWidth() + 1);
        this.visualR.sendObject(object);
        object.setState(false);
        this.visibleObjects.remove(object);

    }

    public void SendAndeliminateObjectLeft(VisibleObject object) {
        object.setX(this.v.getImage().getWidth());
        this.visualL.sendObject(object);
        object.setState(false);
        this.visibleObjects.remove(object);
    }

    public void moveObjectLeftFronter(VisibleObject object) {
        object.setX(0 - object.getWidth() + 1);
    }

    public void moveObjectRightFronter(VisibleObject object) {
        object.setX(this.v.getImage().getWidth());
    }

    public void colisionTest(VisibleObject colisionTestObject) {

        VisibleObject object;
        for (int i = 0; i < this.visibleObjects.size(); i++) {
            object = this.visibleObjects.get(i);
            if (object != colisionTestObject && colisionTestObject.colision(object)) {
                this.killerRules.programCollision(object, colisionTestObject);
            }
        }
        int colision = colisionTestBorders(colisionTestObject);

        if (colisionTestBorders(colisionTestObject) > 0) {
            this.killerRules.bordersColision(colisionTestObject, colision);
        }
    }

    public int colisionTestBorders(VisibleObject object) {
        int colision = 0;//1 right, 2 left, 3 top and 4 bot.
        int limit = 0;

        int spaceshipRadius = object.getWidth();
        if (object.getX() <= 0 - spaceshipRadius) {
            colision = 2;
        }
        if (object.getX() >= this.v.getImage().getWidth() + 1) {
            colision = 1;
        }
        if (object.getY() <= 0) {
            colision = 3;
        }
        if (object.getY() >= this.v.getImage().getHeight() - spaceshipRadius) {
            colision = 4;
        }

        return colision;
    }

    public void detectConnection(Panel p) {
        if (!p.getState().equalsIgnoreCase("Connected")) {
            if (this.visualL.getSocket() != null && p.getId().equals("left")) {
                p.setIp(this.visualL.getIp());
                p.setPort(this.visualL.getPort());
                p.setState("Connected");
            } else if (this.visualR.getSocket() != null && p.getId().equals("right")) {
                p.setIp(this.visualR.getIp());
                p.setPort(this.visualR.getPort());
                p.setState("Connected");
            }
        }

        if (this.visualL.getPort() != 0 && this.visualL.getSocket() == null && p.getId().equals("left")) {
            p.setState("Reconnecting");
        }

        if (this.visualR.getPort() != 0 && this.visualR.getSocket() == null && p.getId().equals("right")) {
            p.setState("Reconnecting");
        }
    }

    public synchronized void createKillerShoot(KillerShip ks) {
        this.visibleObjects.add(ks.shoot());
    }

    public synchronized void destroyKillerShip(VisibleObject ship) {
        checkPadShip(ship.getId());
        KillerShip ks = (KillerShip) ship;
        ks.setState(false);
        this.visibleObjects.remove(ship);
    }

    public void checkPadShip(String shipId) {
        KillerPad p = null;
        for (int i = 0; i < this.killerPads.size(); i++) {
            if (this.killerPads.get(i).getShipId().equals(shipId)) {
                p = killerPads.get(i);
            }
        }
        if (p == null) {
            if(this.visualR.getSocket()!=null){
               this.visualR.sendMessageToPad(shipId, this.server.getPort()); 
            }
            else if(this.visualL.getSocket()!=null){
               this.visualL.sendMessageToPad(shipId, this.server.getPort()); 
            }
            else{
                System.out.println("Both visual handlers disconnected");
            }
        } else {
            p.destroyKillerPad();
        }
    }

    public synchronized void destroyKillerShoot(VisibleObject kill) {
        kill.setState(false);
        this.visibleObjects.remove(kill);
    }

    public synchronized void destroyKillerPad(KillerPad kp) {
        this.killerPads.remove(kp);
    }

    public synchronized void destroyAsteroid(VisibleObject asteroid) {
        asteroid.setState(false);
        this.visibleObjects.remove(asteroid);
    }

    public void reboundAsteroidsTop(Asteroids a) {
        a.setYx(a.getV());
        a.setY(a.getYx());
    }

    public void reboundAsteroidsBottom(Asteroids a) {
        a.setYx(-a.getV());
    }

    public void reboundKillerShootTop(KillerShoot ks) {
        ks.setMove(10);
        ks.setYx(1);
    }

    public void reboundKillerShootBottom(KillerShoot ks) {
        ks.setMove(10);
        ks.setYx(-ks.getV());
    }

    public void reboundAsteroids(Asteroids a, Asteroids b) {
        /* int m1 = (a.getWidth() + a.getHeight()) / 2;
        int m2 = (b.getWidth() + b.getHeight()) / 2;

        //double v1 = (a.getV() * (m1 - m2) + 2 * m2 * b.getV()) / (m1 + m2);
        //double v2 = (b.getV() * (m2 - m1) + 2 * m1 * a.getV()) / (m1 + m2);
        if (a.isCollisionX()) {
            a.setXy(-(a.getXy()));
        }
        if (a.isCollisionY()) {
            a.setYx(-(a.getYx()));
        }

        a.setCollisionX(false);
        a.setCollisionY(false);
         */
    }

    public KillerPad searchPad(String idPad) {
        KillerPad p = null;
        for (int i = 0; i < this.killerPads.size(); i++) {
            if (this.killerPads.get(i).getShipId().equals(idPad)) {
                p = this.killerPads.get(i);
            }
        }

        return p;
    }

    public KillerServer getServer() {
        return server;
    }

    public void setServer(KillerServer server) {
        this.server = server;
    }

    public VisualHandler getVisualL() {
        return visualL;
    }

    public void setVisualL(VisualHandler visualL) {
        this.visualL = visualL;
    }

    public VisualHandler getVisualR() {
        return visualR;
    }

    public void setVisualR(VisualHandler visualR) {
        this.visualR = visualR;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        JButton boton = (JButton) ae.getSource();

        if (boton.getText().equals("Submit L")) {
            this.visualL.setIp(this.ipVisualHandlerLeft.getText());
            this.visualL.setPort(Integer.parseInt(this.portVisualLeft.getText()));
        } else {
            this.visualR.setIp(this.ipVisualHandlerRight.getText());
            this.visualR.setPort(Integer.parseInt(this.portVisualRight.getText()));
        }
    }

    public Dimension getScreenDimension() {
        return screenDimension;
    }

    public void setScreenDimension(Dimension screenDimension) {
        this.screenDimension = screenDimension;
    }

    public Viewer getV() {
        return v;
    }

    public void setV(Viewer v) {
        this.v = v;
    }

    public ArrayList<VisibleObject> getVisibleObjects() {
        return visibleObjects;
    }

    public void setVisibleObjects(ArrayList<VisibleObject> visibleObjects) {
        this.visibleObjects = visibleObjects;
    }

}
