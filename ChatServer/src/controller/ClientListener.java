/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import app.CONFIG;
import app.ServerApp;
import java.net.SocketException;
import model.Client;
import model.DTO.User;
import service.TCPService;
import service.UserService;

/**
 *
 * @author ADMIN
 */
public class ClientListener extends Thread{
    private Client client;

    public ClientListener(Client client) {
        this.client = client;
    }
    
    public void run(){
        try{
            while(client.getUser() == null){
                User user = (User) TCPService.receive(client.getSocket());
                if (user.getName() == null){
                    User validatedUser = UserService.loginValidate(user);
                    client.setUser(validatedUser);
                    new UserThread(client).start();
                    TCPService.send(client.getSocket(), client.getUser());
                    ServerApp.mainController.connectionObserver.updateConnectedTable();
                } else {
                    if (UserService.create(user) != null){
                        TCPService.send(client.getSocket(), CONFIG.SERVER_RESPONSE.SUCCESS);
                    } else {
                        TCPService.send(client.getSocket(), CONFIG.SERVER_RESPONSE.REGISTER_FAILED);
                    }
                }
            }
        } catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }
}
