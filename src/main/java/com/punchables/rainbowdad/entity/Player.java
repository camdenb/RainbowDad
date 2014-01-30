/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.punchables.rainbowdad.entity;

import com.badlogic.gdx.math.Vector2;
import com.punchables.rainbowdad.map.MapTile;
import com.punchables.rainbowdad.utils.Coord;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author DrShmoogle
 */
public class Player extends DynamicGameObject {

    private boolean attacking = false;
    private double attackAngle = 0;
    private String defaultTexture = "circledude64.png";

    public Player(float x, float y){
        super(x, y, 64, 64);
        setMoveSpeed(8000f);
        setMaxVel(8000);
        setFriction(.8f);
        setTerrainMod(1f);
        setTexture(defaultTexture);
    }

    public void update(float delta, ConcurrentHashMap<Coord, MapTile> map){
        updateAll(delta, map);
    }

    public void setAttacking(boolean bool){
        this.attacking = bool;
    }

    public boolean isAttacking(){
        return attacking;
    }

    /**
     * @return the defaultTexture
     */
    public String getDefaultTexture(){
        return defaultTexture;
    }

    /**
     * @param defaultTexture the defaultTexture to set
     */
    public void setDefaultTexture(String defaultTexture){
        this.defaultTexture = defaultTexture;
    }

    /**
     * @return the attackAngle
     */
    public double getAttackAngle(){
        return attackAngle;
    }

    /**
     * @param attackAngle the attackAngle to set
     */
    public void setAttackAngle(double attackAngle){
        this.attackAngle = attackAngle;
    }

}
