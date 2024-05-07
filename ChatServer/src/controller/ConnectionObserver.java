/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import app.ServerApp;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import model.Client;
import model.DTO.User;
import view.ServerMainFrame;

/**
 *
 * @author ADMIN
 *  Nó giúp hiển thị thông tin chi tiết về client (IP, cổng, tên người dùng) trên giao diện người dùng của server.
 */

public class ConnectionObserver extends Thread{
    Map <Integer, Client> connectedClients;
    ServerMainFrame frame;
    Socket socket;
    
    public ConnectionObserver(ServerMainFrame frame){
        this.frame = frame;
        connectedClients = new HashMap<>();
        socket = new Socket();
    }

    @Override
    public void run() {
        while(ServerApp.mainController.running){
            try {
                sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ConnectionObserver.class.getName()).log(Level.SEVERE, null, ex);
            }
            updateConnectedTable();
        }
        connectedClients.clear();
        updateConnectedTable();
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionObserver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addConnectedClient(Client client){
        connectedClients.put(client.getSocket().getPort(), client);
        updateConnectedTable();
    }
    
    public void updateConnectedTable(){
        DefaultTableModel dtm = (DefaultTableModel) frame.getConnectedTable().getModel();
        dtm.setRowCount(0);
        for(int p : connectedClients.keySet()){
            Client client = connectedClients.get(p);
            boolean connecting = true;
            SocketAddress sa = new InetSocketAddress(client.getSocket().getInetAddress(), client.getSocket().getPort());
            try{
                socket.connect(sa, 5000);
            } catch (SocketTimeoutException | SocketException ex){
//                connecting = false;
            } catch (IOException ex){
                ex.printStackTrace();
            }
            if (connecting){
                String ip = client.getSocket().getInetAddress().getHostAddress();
                int port = client.getSocket().getPort();
                User user = client.getUser();
                String username = user == null?"":user.getUsername();
                String name = user == null?"":user.getName();            
                dtm.addRow(
                        new Object[]{
                            ip,
                            port,
                            username,
                            name
                        }
                );
            } else {
                connectedClients.remove(p);
            }
        }
    } 
}
