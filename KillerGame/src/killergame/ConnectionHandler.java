/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package killergame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Xavier
 */
public class ConnectionHandler implements Runnable {

    private Socket clientSocket;
    private String cliAddr;
    private KillerGame kg;
    private int port;
    private BufferedReader in;
    private PrintWriter out;

    public ConnectionHandler(Socket clientSock, String cliAddr, int port, KillerGame kg) {
        this.clientSocket = clientSock;
        this.cliAddr = cliAddr;
        this.kg = kg;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            processClient(this.in, this.out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processClient(BufferedReader in, PrintWriter out) {
        String line;
        boolean done = false;
        try {
            if ((line = in.readLine()) == null) {
                done = true;
            } else {
                System.out.println("Client msg: " + line);
                if (line.trim().equals("bye")) {
                    done = true;
                } else {
                    doRequest(line);
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    private void configureVH(VisualHandler vh, String port) {
        System.out.println("ConnectionHandler: configurando VH...");
        if (vh.getSocket() == null) {
                System.out.println("ConnectionHandler:  VH no esta OK, lo configuro...");
                vh.initVisualHandler(this.clientSocket);
                vh.setPort(Integer.parseInt(port));
                (new Thread(vh)).start(); 
                this.out.println("listo");
        } else {
            System.out.println("ConnectionHandler: VisualHandler ya esta ok");
        }

    }
    
    private void processLeftVHrequest(String port) {
        VisualHandler vh = this.kg.getVisualL();
        configureVH(vh, port);
    }

    private void processRightVHrequest(String port) {
        VisualHandler vh = this.kg.getVisualR();
        configureVH(vh, port);
    }

    private void doRequest(String line) throws IOException {
        String protocol = line.substring(0, 1);
        String info=line.substring(1);//P|newsh
        
        System.out.println(protocol);
        System.out.println(info);
        switch (protocol) {
            case "L":
                this.processLeftVHrequest(info);
                System.out.println("Left client detected");
                break;
            case "R":
                this.processRightVHrequest(info);
                System.out.println("Right client detected");
                break;
            case "P":
                this.kg.createKillerPad(this.clientSocket,info);
                System.out.println("Pad detected");
                break;
            default:
                System.out.println("Ignoring input line");
                this.clientSocket.close();
                break;
        }
    }
    
    

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public String getCliAddr() {
        return cliAddr;
    }

    public void setCliAddr(String cliAddr) {
        this.cliAddr = cliAddr;
    }
}
