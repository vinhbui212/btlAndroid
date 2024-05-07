/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import app.ServerApp;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import model.Client;
import model.DTO.User;
import service.UserService;
import view.ServerMainFrame;

/**
 *
 * @author ADMIN
 */
public class MainServerController {
    ServerMainFrame frame;
    ConnectRequestReceiver server;
    ConnectionObserver connectionObserver;  
    boolean running;

    public MainServerController() {
    }
    
    public void initComponents(){
        frame = new ServerMainFrame();
        server = new ConnectRequestReceiver();
        connectionObserver = new ConnectionObserver(frame);
        running = false;
    }
    public void startApp(){
        initComponents();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                frame.setVisible(true);
            }
        });
    }
    
    public void startServer(){
        running = true;
        server = new ConnectRequestReceiver();
        server.setUpPort();
        server.start();
        connectionObserver.start();
        frame.getPortField().setText("" + server.port);
        frame.getPowerButton().setText("Stop");
        frame.getConnectedTable().setEnabled(true);
        System.out.println("Start server.");
    }
    
    public void stopServer(){
        running = false;
        try {
            server.socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        frame.getPowerButton().setText("Start");
        frame.getConnectedTable().setEnabled(false);   
        System.out.println("Stop server.");     
    }   
}