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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Xavier
 */
public class KillerClient implements Runnable {

    private VisualHandler visualHandler;
    private KillerGame killerGameClient;

    public KillerClient(KillerGame killerGame, VisualHandler visual) {
        this.visualHandler = visual;
        this.killerGameClient = killerGame;

    }

    public void conect() {
        try {
           Socket cliente = new Socket();
            cliente.connect(new InetSocketAddress(this.visualHandler.getIp(), this.visualHandler.getPort()), 300);
            PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            sendRequest(out);
            String response = in.readLine();
            this.setSocketToVh(cliente, response);
            this.visualHandler.sendControlMessage();
        } catch (SocketTimeoutException ex) {
            System.out.println("Socket timeout");
        } catch (IOException ex) {
            System.out.println("Impossible to connect");
        }

    }

    public void sendRequest(PrintWriter out) {
        if (this.visualHandler == this.killerGameClient.getVisualL()) {
            out.println("R" + this.killerGameClient.getServer().getPort());
        } else {
            out.println("L" + this.killerGameClient.getServer().getPort());
        }
    }

    private void setSocketToVh(Socket socket, String answer) {

        if (this.visualHandler.getSocket() == null && answer.equalsIgnoreCase("listo")) {
            this.visualHandler.initVisualHandler(socket);
            this.visualHandler.setPort(socket.getPort());
        } else {
            System.out.println("No se ha recibido nada");
        }

    }

    @Override
    public void run() {
        while (true) {
            if (this.visualHandler.getIp() != null && this.visualHandler.getSocket() == null) {
                conect();
            }
            if (this.visualHandler.getSocket() != null) {
                if (System.currentTimeMillis() - this.visualHandler.getControlTime() > 1000) {
                    System.out.println(System.currentTimeMillis() - this.visualHandler.getControlTime());
                    this.visualHandler.setSocket(null);
                }
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(KillerClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
