/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.DTO;

import java.io.Serializable;

/**
 *
 * @author ADMIN
 */
public abstract class MessageAddress implements Serializable{
    private static final long serialVersionUID = 1L;
    protected int id;
    protected String name;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    } 
}
