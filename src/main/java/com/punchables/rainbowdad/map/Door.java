/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.punchables.rainbowdad.map;

/**
 *
 * @author DrShmoogle
 */
public class Door {
    
    private boolean open = false;
    private boolean locked = true;
    
    public Door(boolean open, boolean locked){
        this.open = open;
        this.locked = locked;
    }

    /**
     * @return the open
     */
    public boolean isOpen(){
        return open;
    }

    /**
     * @param open the open to set
     */
    public void setOpen(boolean open){
        this.open = open;
    }

    /**
     * @return the locked
     */
    public boolean isLocked(){
        return locked;
    }

    /**
     * @param locked the locked to set
     */
    public void setLocked(boolean locked){
        this.locked = locked;
    }
    
}
