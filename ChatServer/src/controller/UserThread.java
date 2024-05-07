/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import app.CONFIG;
import app.ServerApp;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import model.Client;
import model.DTO.Group;
import model.DTO.Message;
import model.DTO.MessageBox;
import model.DTO.MessageRepository;
import model.DTO.User;
import service.GroupService;
import service.MessageBoxService;
import service.MessageService;
import service.TCPService;
import service.UDPService;
import service.UserService;

/**
 *
 * @author ADMIN
 */
public class UserThread extends Thread{
    private Client client;
    private DatagramSocket datagramSocket;
    
    public UserThread(Client client) {
        this.client = client;
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendMessageRepository(){
        MessageRepository messageRepository = MessageService.getMessageRepositoryOf(client.getUser());
        TCPService.send(client.getSocket(), messageRepository);
    }
    private void sendServerDatagramSocketPort(){
        TCPService.send(client.getSocket(), datagramSocket.getLocalPort());
    }
    
    private void getClientDatagramSocketPort(){
        int port = (int) TCPService.receive(client.getSocket());
        ServerApp.mainController.connectionObserver.connectedClients.get(client.getSocket().getPort()).setClientDatagramSocketPort(port);
    }
    
    private void sendResposeType(Client receiver, CONFIG.RESPONSE_TYPE type){
        sendToClient(receiver, type);
    }
    
    private void sendToClient(Client receiver, Object object){
        UDPService.send(datagramSocket,
                receiver.getSocket().getInetAddress(),
                receiver.getClientDatagramSocketPort(),
                (Serializable) object);
    }
    private void listen(){
        while(true){
            CONFIG.REQUEST_TYPE request_type = (CONFIG.REQUEST_TYPE) UDPService.receive(datagramSocket);
            Object data = UDPService.receive(datagramSocket);
            switch(request_type){
                case MESSAGE:
                    handleMessage(data);
                    break;
                case SEARCH_USERS:
                    handleSearchUsersRequest(data);
                    break;
                case ADD_FRIEND:
                    handleAddFriendsRequest(data);
                    break;
                case GET_FRIENDS_LIST:
                    handleGetFriendsListRequest(data);
                    break;
                case NEW_GROUP:
                    handleNewGroupRequest(data);
                    break;
                default:
                    System.out.println("Unknown request: " + request_type);
                    break;
            }
        }
    }
    
    private void handleMessage(Object data){
        Message message = (Message) data;
        Map <Integer, Client> connectedClientsById = 
                ServerApp.mainController.connectionObserver.connectedClients
                        .values().stream()
                        .filter(c -> c.getUser() != null)
                        .collect(
                                Collectors.toMap(
                                        c -> c.getUser().getId(),
                                        Function.identity(),
                                        (c1, c2) -> c1
                                )
                        );
        Group targetGroup = GroupService.getGroupById(message.getReceiver().getId());
        sendMessageToOnlineUsers(message, connectedClientsById, targetGroup);
        MessageService.saveMessage(message);
    }
    private void sendMessageToOnlineUsers(Message message, Map <Integer, Client> connectedClientsById, Group targetGroup){
        if (targetGroup != null){
            for(User u:UserService.getAllMembersOf(targetGroup)){
                if (u.getId() == message.getSender().getId()){
                    continue;
                }
                if (connectedClientsById.containsKey(u.getId())){
                    Client receiveClient = connectedClientsById.get(u.getId());
                    sendResposeType(receiveClient, CONFIG.RESPONSE_TYPE.GROUP_MESSAGE);
                    sendToClient(receiveClient, message);
                }
            }
        }
        else if (connectedClientsById.containsKey(message.getReceiver().getId())){
            Client receiveClient = connectedClientsById.get(message.getReceiver().getId());            
            sendResposeType(receiveClient, CONFIG.RESPONSE_TYPE.DIRECT_MESSAGE);
            sendToClient(receiveClient, message);
        }
    }
    
    private void handleSearchUsersRequest(Object data){
        sendResposeType(client, CONFIG.RESPONSE_TYPE.SEARCH_RESULT);
        sendToClient(client, UserService.getAllUsersWhoAreNotFriendsOf(client.getUser(), (String) data));
    }
    
    private void handleAddFriendsRequest(Object data){
        sendResposeType(client, CONFIG.RESPONSE_TYPE.ADD_FRIEND_REQUEST_RESULT);
        if(1 == MessageBoxService.create(new MessageBox(client.getUser(), (User) data, true))){
            sendToClient(client, CONFIG.SERVER_RESPONSE.SUCCESS);
            MessageBox messageBox = MessageBoxService.getMessageBoxBetween(client.getUser(), (User) data);
            if (messageBox != null){
                sendToClient(client, messageBox);
            } else {
                sendToClient(client, data);
            }
        } else {
            sendToClient(client, CONFIG.SERVER_RESPONSE.FAILED);
        }
    }
    private void handleGetFriendsListRequest(Object data){
        sendResposeType(client, CONFIG.RESPONSE_TYPE.FRIENDS_LIST);
        sendToClient(client, UserService.getAllFriendsOf(client.getUser()));
    }
    private void handleNewGroupRequest(Object data){
        String groupName = (String) data;
        List <User> members = (List <User>) UDPService.receive(datagramSocket);
        Group newGroup = GroupService.create(new Group(groupName));
        Map <Integer, Client> connectedClientsById = 
                ServerApp.mainController.connectionObserver.connectedClients
                        .values().stream()
                        .filter(c -> c.getUser() != null)
                        .collect(
                                Collectors.toMap(
                                        c -> c.getUser().getId(),
                                        Function.identity(),
                                        (c1, c2) -> c1
                                )
                        );
        boolean createNewGroupResult = MessageBoxService.create(members, newGroup) == members.size();
        for (User member:members){
            if (connectedClientsById.containsKey(member.getId())){
                Client target = connectedClientsById.get(member.getId());
                sendResposeType(target, CONFIG.RESPONSE_TYPE.NEW_GROUP_REQUEST_RESULT);
                if (createNewGroupResult){
                    sendToClient(target, CONFIG.SERVER_RESPONSE.SUCCESS);
                    sendToClient(target, newGroup);
                } else {
                    sendToClient(target, CONFIG.SERVER_RESPONSE.FAILED);
                }
            }
        }
    }
    
    @Override
    public void run() {
        sendMessageRepository();
        sendServerDatagramSocketPort();
        getClientDatagramSocketPort();
        listen();
    }
}
