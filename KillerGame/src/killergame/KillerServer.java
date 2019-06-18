/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package killergame;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Xavier
 */
public class KillerServer implements Runnable {

    private KillerGame kg;
    private int port;
    private String cliAddr;
    private Socket clientSock;

    public KillerServer(KillerGame kg) {
        this.port = 8000;
        this.kg = kg;
    }

    @Override
    public void run() {
        ServerSocket serverSock = configurePort();
        System.out.println("Waiting for a client...");
        System.out.println("Port: "+serverSock.getLocalPort());
        while (true) {
            try {
                this.clientSock = serverSock.accept();
                this.cliAddr = clientSock.getInetAddress().getHostAddress();
                Thread t = new Thread(new ConnectionHandler(this.clientSock, this.cliAddr, this.port, this.kg));
                t.start();
            } catch (IOException ex) {
                Logger.getLogger(KillerServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public ServerSocket configurePort() {
        ServerSocket serverSock = null;
        boolean check = false;
        while (serverSock == null) {
            try {
                serverSock = new ServerSocket(this.port);
            } catch (IOException ex) {
                System.out.println(ex);
                this.port++;
            }
        }

        return serverSock;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
