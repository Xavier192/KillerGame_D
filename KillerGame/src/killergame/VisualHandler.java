/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package killergame;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Xavier
 */
public class VisualHandler implements Runnable {

    private KillerClient client;
    private KillerGame killerGameHandler;
    private Socket socket;
    private String ip;
    private int port;
    private BufferedReader in;
    private PrintWriter out;
    private String localIp;
    private boolean alive;

    private long controlTime;

    public VisualHandler(KillerGame kg) {
        this.ip = null;
        this.port = 0;
        this.socket = null;
        this.killerGameHandler = kg;
        this.client = new KillerClient(this.killerGameHandler, this);
    }

    public synchronized void initVisualHandler(Socket socket){
        try {
            this.socket = socket;
            this.ip = this.socket.getInetAddress().getHostAddress();
            this.localIp = this.socket.getLocalAddress().getHostAddress();
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.controlTime = System.currentTimeMillis();
        } catch (IOException ex) {
            this.socket=null;
        }

    }

    public void sendObject(VisibleObject object) {
        if (object instanceof KillerShip) {
            sendKillerShip((KillerShip) object);
        } else if (object instanceof Asteroids) {
            sendAsteroid((Asteroids) object);
        } else if (object instanceof KillerShoot) {
            sendKillerShoot((KillerShoot) object);
        }
    }

    public void sendControlMessage() {
        this.out.println("hola?");
    }

    public void recieveMessage(String message) {
        String msg = message.substring(0, 1);
        String parameters = message.substring(1);

        System.out.println(msg);

        switch (msg) {
            case "K":
                this.createKillerShip(parameters);//killership
                break;
            case "A":
                this.createAsteroid(parameters);//Asteroid
                System.out.println("Creating asteroid");
                break;
            case "S":
                this.createShoot(parameters);
                break;//Shoot
            case "P":
                killerShipCommands(message);
                break;
            case "X":
                killerShipCommands(message);
                break;
        }
    }

    public void searchKillerPad(String parameters) {
        System.out.println(parameters);

    }

    public void sendMessageToPad(String idShip, int port) {

        String message = "X/" + "dead" + "/" + idShip + "/" + this.localIp + "/" + port + "/";
        if (this.getSocket() != null) {
            sendMessage(message);
        }

    }

    public void killerShipCommands(String parameters) {
        String origin = parameters.substring(0, 1);
        String info = parameters.substring(1);
        String[] params = info.split("/");
        String order = params[1];
        String idShip = params[2];
        String localIp = params[3];
        int port = Integer.parseInt(params[4]);
        String trueOrder = order + "/" + idShip + "/";

        if (!this.localIp.equalsIgnoreCase(localIp) || this.killerGameHandler.getServer().getPort() != port) {
            this.controlTime = System.currentTimeMillis();
            if (origin.equals("P")) {
                KillerShip ship = this.killerGameHandler.searchSpaceShip(idShip);
                if (ship != null) {
                    try {
                        System.out.println("order to killerPad: " + trueOrder);
                        KillerPad.doRequest(this.killerGameHandler, trueOrder);

                    } catch (IOException ex) {
                        Logger.getLogger(VisualHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    this.sendMessageToAvailableVH(parameters);
                }
            }
            if (origin.equals("X")) {
                KillerPad pad = this.killerGameHandler.searchPad(idShip);
                if (pad == null) {
                    this.sendMessageToAvailableVH(parameters);
                } else {
                    pad.destroyKillerPad();
                }
            }

        } else {
            System.out.println("Loop message, no repetition");
        }
    }

    public void sendMessageToAvailableVH(String info) {
        if (this.killerGameHandler.getVisualR().getSocket() != null) {
            this.killerGameHandler.getVisualR().sendMessage(info);
        } else if (this.killerGameHandler.getVisualL().getSocket() != null) {
            System.out.println("Sending message to vh left");
            this.killerGameHandler.getVisualL().sendMessage(info);
        } else {
            System.out.println("Both visual handlers disconnected");
        }
    }

    public void createKillerShip(String parameters) {
        String params[] = parameters.split("/");
        double y;
        int width;
        int height;
        int state;
        double x;
        String idShip;
        String color;
        double xy;
        double yx;
        int dir;
        x = Double.parseDouble(params[9]);
        y = Double.parseDouble(params[1]);
        width = Integer.parseInt(params[2]);
        height = Integer.parseInt(params[3]);
        state = Integer.parseInt(params[4]);
        idShip = params[6];
        xy = Double.parseDouble(params[7]);
        yx = Double.parseDouble(params[8]);
        dir = Integer.parseInt(params[10]);
        color = params[11];

        System.out.println(idShip);

        this.killerGameHandler.createSpaceShipNewScreen(width, height, x, y, xy, yx, state, idShip, dir, color);
    }

    public void createAsteroid(String parameters) {

        String params[] = parameters.split("/");
        double x = Double.parseDouble(params[8]);
        double y = Double.parseDouble(params[1]);
        int width = Integer.parseInt(params[2]);
        int height = Integer.parseInt(params[3]);
        boolean state = Boolean.parseBoolean(params[4]);
        double v = Double.parseDouble(params[5]);
        double xy = Double.parseDouble(params[6]);
        double yx = Double.parseDouble(params[7]);

        this.killerGameHandler.createAsteroidNewScreen(x, y, xy, yx, v, height, width, state);
    }

    public void sendKillerShip(KillerShip ship) {
        String color = "";
        int dir = 2;
        for (int i = 0; i < ship.getMoves().length; i++) {
            if (ship.getMoves()[i]) {
                dir = i;
            }
        }
        if (ship.getColor() == Color.BLACK) {
            color = "Black";
        }
        if (ship.getColor() == Color.blue) {
            color = "Blue";
        }
        if (ship.getColor() == Color.red) {
            color = "Red";
        }
        if (ship.getColor() == Color.green) {
            color = "Green";
        }
        if (ship.getColor() == Color.YELLOW) {
            color = "Brown";
        }
        String message = "K" + "/" + ship.getY() + "/" + ship.getWidth() + "/" + ship.getHeight() + "/" + 0 + "/" + ship.getV() + "/" + ship.getId() + "/" + ship.getXy() + "/" + ship.getYx() + "/" + ship.getX() + "/" + dir + "/" + color + "/";

        sendMessage(message);
    }

    public void sendAsteroid(Asteroids asteroid) {
        String message = "A" + "/" + asteroid.getY() + "/" + asteroid.getWidth() + "/" + asteroid.getHeight() + "/" + true + "/" + asteroid.getV() + "/" + asteroid.getXy() + "/" + asteroid.getYx() + "/" + asteroid.getX() + "/";
        sendMessage(message);
    }

    public void sendKillerShoot(KillerShoot killershoot) {
        String message = "S" + "/" + killershoot.getY() + "/" + killershoot.getWidth() + "/" + killershoot.getHeight() + "/" + true + "/" + killershoot.getV() + "/" + killershoot.getId() + "/" + killershoot.getXy() + "/" + killershoot.getYx() + "/" + killershoot.getX() + "/" + 10 + "/" + killershoot.getFirstTime();
        sendMessage(message);
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public void createShoot(String parameters) {
        String params[] = parameters.split("/");
        double y;
        int width;
        int height;
        boolean state;
        double x;
        String idShip;
        double xy;
        double yx;
        int move;
        long firstTime;
        y = Double.parseDouble(params[1]);
        width = Integer.parseInt(params[2]);
        height = Integer.parseInt(params[3]);
        state = Boolean.parseBoolean(params[4]);
        idShip = params[6];
        xy = Double.parseDouble(params[7]);
        yx = Double.parseDouble(params[8]);
        x = Double.parseDouble(params[9]);
        move = Integer.parseInt(params[10]);
        firstTime = Long.parseLong(params[11]);

        this.killerGameHandler.createShootNewScreen(y, width, height, state, 10.0, idShip, xy, yx, x, move, firstTime);
    }

    @Override
    public void run() {
        (new Thread(client)).start();
        while (true) {
            if (this.getSocket() != null) { // Diferencia poner directamente this.socket... >> jumi?
                try {
                    String message = in.readLine();
                    // Mensaje para confirmar que el otro equipo está conectado
                    // Se reinicia el tiempo al confirmar conexión y contesta al otro equipo
                    // KC comprueba los tiempos y nullea socket y demás si no hay conexión
                    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ok pero probar con socket.setSoTiemout
                    if (message.equalsIgnoreCase("hola?")) {
                        this.controlTime = System.currentTimeMillis();
                        this.sendControlMessage();
                    } else {
                        System.out.println("vh: message " + message);
                        this.recieveMessage(message);
                    }
                } catch (IOException ex) {
                    System.out.println("VH: IOException socket");
                    this.socket = null; // Usar método killSocket?
                } catch (Exception ex) {
                    System.out.println("VH: Exception socket");
                    System.err.println(ex.getMessage());
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(VisualHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public long getControlTime() {
        return controlTime;
    }

    public void setControlTime(long controlTime) {
        this.controlTime = controlTime;
    }

    public void setSocket(Socket client) {
        this.socket = client;
    }

    public Socket getSocket() {
        return socket;
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

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

}
