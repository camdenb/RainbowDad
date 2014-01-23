/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.punchables.rainbowdad.map;

import com.badlogic.gdx.math.Rectangle;
import com.punchables.rainbowdad.utils.Coord;

/**
 *
 * @author DrShmoogle
 */
public class MapTile{
    private TileType type;
    private Rectangle hitbox;
    private Coord pos;
    
    public MapTile(){
        this.type = TileType.NONE;
    }
    
    public MapTile(TileType type){
        this.type = type;
    }
    
    public MapTile(TileType type, Coord pos){
        this.type = type;
        this.pos = pos;
    }
    
    public TileType get(){
        return type;
    }
    
    public void set(TileType type){
        this.type = type;
    }
    
    public Coord getPos(){
        return pos;
    }
    
    public void setPos(Coord pos){
        this.pos = pos;
    }
    
    public boolean is(TileType type){
        return (this.type == type);
    }
    
    public String toString(){
        return getClass().getName() + " type: " + type;
        
    }
    
}


