/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.DTO;

import model.DTO.MessageAddress;
import java.io.Serializable;

/**
 *
 * @author ADMIN
 */
public class User extends MessageAddress implements Serializable{
    private static final long serialVersionUID = 11L;
    private String username;
    private String password;

    public User(){};
    
    public User(int id){
        this.id = id;
    }
    public User(int id, String username, String password, String name) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
    }
    
    public User(int id, String username, String name) {
        this.id = id;
        this.username = username;
        this.name = name;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
