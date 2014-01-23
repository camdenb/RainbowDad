package com.punchables.rainbowdad.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author DrShmoogle
 */
public class Art{
    
    public Texture player;
    public Texture enemy;
    public Texture map;
    public Texture tile;
    public HashMap<Integer, Texture> assetList = new HashMap<>();
    
    public Art(){
        load();
        assetList.put(0, player);
        assetList.put(1, enemy);
        assetList.put(2, map);
        assetList.put(3, tile);
    }
    
    public void load(){
        player = new Texture("circledude64.png");
        enemy = new Texture("enemy64.png");
        map = new Texture("sc_map.png");
        tile = new Texture("64/empty64.png");
    }
    
    public Texture getAssetByID(int id){
        return assetList.get(id);
    }
    
}
