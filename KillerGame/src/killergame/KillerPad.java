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
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Xavier
 */
public class KillerPad implements Runnable {

    private KillerGame killerGamePad;
    private Socket clientSocket;
    private String shipId;
    private String localIp;
    private boolean connected;
    private KillerShip ks;
    private BufferedReader in;
    private PrintWriter out;
    private boolean invisible;
    private int port;

    public KillerPad(Socket clientSock, KillerGame killerGamePad) {
        this.clientSocket = clientSock;
        this.killerGamePad = killerGamePad;
        this.connected = true;
        this.invisible = false;
        this.localIp = this.clientSocket.getLocalAddress().getHostAddress();
        this.shipId = this.clientSocket.getInetAddress().getHostAddress();
        //KillerPad.createKillerShip(this.killerGamePad, this.shipId);
        this.ks = this.killerGamePad.searchSpaceShip(this.shipId);

    }

    @Override
    public void run() {
        try {
            this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            this.port = this.clientSocket.getPort();
            System.out.println(this.shipId);
            processClient(in, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processClient(BufferedReader in, PrintWriter out) {
        int counterDisconnection = 0;
        String line;
        out.println("comu");
        
        while (this.connected) {
            try {
                if (in.ready()) {
                    counterDisconnection = 0;
                    line = in.readLine();
                    if (line.trim().equals("bbye")) {
                       destroyKillerPad();
                    }
                    if (line.trim().equals("answer")) {
                        out.println("comu");
                    } else {
                        processInfo(line);
                    }
                } else {
                    counterDisconnection++;
                }
                if (counterDisconnection >= 250) {
                   destroyKillerPad();
                }
            } catch (IOException e) {
                System.out.println("Connection error");
              destroyKillerPad();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(KillerPad.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void destroyKillerPad() {
        KillerShip ks=this.killerGamePad.searchSpaceShip(this.shipId);
        System.out.println("Pad disconnected");
        this.connected=false;
        out.println("dead");
        this.killerGamePad.destroyKillerPad(this); 
        
        if(ks!=null){
            this.killerGamePad.destroyKillerShip(ks);
        }   
        else{
            this.sendMessageToVisualHandler("killShip");
        }
    }

    public void setSocket(Socket s) {
        this.clientSocket = s;
    }

    public static void doRequest(KillerGame killerGamePad, String line) throws IOException {
        String[] content = line.split("/");

        String info = content[0];
        String shipId = content[1];
        String color = "";

        if (info.equals("newsh")) {
            color = content[2];
        }

        System.out.println("Mensaje: " + line);

        switch (info) {
            case "movel":
                killerGamePad.moveSpaceShip("left", shipId);
                break;
            case "mover":
                killerGamePad.moveSpaceShip("right", shipId);
                break;
            case "moveu":
                killerGamePad.moveSpaceShip("up", shipId);
                break;
            case "moved":
                killerGamePad.moveSpaceShip("down", shipId);
                break;
            case "killShip":
                killerGamePad.destroyKillerShip(killerGamePad.searchSpaceShip(shipId));
                break;
            case "shoot":
                killerGamePad.moveSpaceShip("shoot", shipId);
                System.out.println("Shooting");
                break;
            case "newsh":
                createKillerShip(killerGamePad, shipId, color);
                break;
            case "deadn":

                System.out.println("dying");
                break;
            case "movelu":
                killerGamePad.moveSpaceShip("upleft", shipId);
                break;
            case "moveld":
                killerGamePad.moveSpaceShip("downleft", shipId);
                break;
            case "moveru":
                killerGamePad.moveSpaceShip("upright", shipId);
                break;
            case "moverd":
                killerGamePad.moveSpaceShip("downright", shipId);
                break;
            case "st":
                killerGamePad.moveSpaceShip("stop", shipId);
                break;
            default:
                System.out.println("Ignoring input line");
                break;
        }
    }

    public void processInfo(String info) {
        prepareSpaceShip(info);
    }

    public void prepareSpaceShip(String info) {
        String order = "";
        String color = "";
        String completeInfo = "";
        String[] arrayInfo = info.split("/");
        if (info.length()>9) {
            order = arrayInfo[1];
            color = arrayInfo[2];
            completeInfo = order + "/" + this.shipId + "/" + color;
        } else {
            order = arrayInfo[0];
            completeInfo = order + "/" + this.shipId + "/";
        }

        System.out.println("Complete info: " + completeInfo);
        if (this.killerGamePad.searchSpaceShip(this.shipId) == null && !order.equals("newsh")) {
            sendMessageToVisualHandler(info);
        } else {
            try {
                KillerPad.doRequest(this.killerGamePad, completeInfo);
            } catch (IOException ex) {
                Logger.getLogger(KillerPad.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static Color knowColor(String color) {
        Color c = null;
        switch (color) {
            case "Black":
                c=Color.BLACK;
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

        return c;
    }

    public void sendMessageToVisualHandler(String info) {
        String msg = "P/" + info + "/" + this.shipId + "/" + this.localIp + "/" + this.killerGamePad.getServer().getPort() + "/";
        if (this.killerGamePad.getVisualR().getSocket() != null) {
             System.out.println("Sending message to visual handler right");
            this.killerGamePad.getVisualR().sendMessage(msg);
        }
        else if(this.killerGamePad.getVisualL().getSocket()!=null){
            System.out.println("Sending message to visual handler left");
            this.killerGamePad.getVisualL().sendMessage(msg);
        }
    }

    public static void createKillerShip(KillerGame killerGamePad, String shipId, String color) {
        if (killerGamePad.searchSpaceShip(shipId) == null) {
            Color c = knowColor(color);
            killerGamePad.createSpaceShip(c, 0, 0, true, shipId);
        } else {
            System.out.println("You already have a spaceship");
        }
    }

    public String getShipId() {
        return shipId;
    }

    public void setShipId(String shipId) {
        this.shipId = shipId;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

}
