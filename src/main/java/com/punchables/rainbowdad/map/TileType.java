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
public enum TileType {
    NONE(true),
    DEBUG(true),
    DEBUG2(true),
    DIRT(false),
    FLOOR(false),
    DOOR(false),
    SOLIDWALL(true),
    WALLTALL(true),
    CEILING(true),
    WALL(true);
    
    private final boolean solid;
    
    private TileType(boolean solid, boolean overlay){
        this.solid = solid;
    }
    
    private TileType(boolean solid){
        this.solid = solid;
    }
    
    public boolean isSolid() {
        return solid;
    }
    
}
