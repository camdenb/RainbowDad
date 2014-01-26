/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.punchables.rainbowdad.entity;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.punchables.rainbowdad.map.MapTile;
import com.punchables.rainbowdad.screens.GameScreen;
import com.punchables.rainbowdad.utils.Collider;
import com.punchables.rainbowdad.utils.Coord;
import static java.lang.Math.abs;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author DrShmoogle
 */
public abstract class DynamicGameObject extends GameObject{

    private Vector2 vel;
    private Vector2 accel;
    private float moveAccel;
    private float maxVel;
    private float friction;
    private float terrainMod;
    private float collisionOffset = .1f;
    private ConcurrentHashMap<Coord, MapTile> map = new ConcurrentHashMap<>();

    public DynamicGameObject(float x, float y, float width, float height){
        super(x, y, width, height);
        this.vel = new Vector2();
        this.accel = new Vector2();
    }

    public void updateMovement(float delta){
    //System.out.println(getAccel());
        //diagonal speed fix; sqrt(2) / 2 is about .707
        if(abs(getAccel().x) > 0 && abs(getAccel().y) > 0){
            getAccel().mul(.707f);
        }
        //getAccel().y += -6000;
        //System.out.println(getVel());
        getVel().x += getAccel().x * delta;
        getPos().x = resolveCollision_x(delta);
        
        getVel().y += getAccel().y * delta;
        getPos().y = resolveCollision_y(delta);
        

        //if(abs(getAccel().x) == 0 && abs(getAccel().y) == 0){
        getVel().x *= getFriction();
        getVel().y *= getFriction();
        //}
        
        //System.out.println("Accel: " + getAccel() + " Velocity: " + getVel());

        if(abs(getVel().x) < 0.05){
            getVel().x = 0;
        }
        if(abs(getVel().y) < 0.05){
            getVel().y = 0;
        }

        if(abs(getVel().y) > getMaxVel()){
            getVel().y = getMaxVel() * (getVel().y / abs(getVel().y));
        }
        if(abs(getVel().x) > getMaxVel()){
            getVel().x = getMaxVel() * (getVel().x / abs(getVel().x));
        }
        
        setAccel(new Vector2(0, 0));
    }
    
    public void updateTileMap(ConcurrentHashMap<Coord, MapTile> map){
        this.map = map;
    }

    /*RESOLVE COLLISIONS IN THIS CLASS*/

    public float resolveCollision_x(float delta){
        
        float oldX = getPos().x;
        float newX = getPos().x + getVel().x * delta;

        for(Map.Entry<Coord, MapTile> entry : map.entrySet()){
            Coord coord = entry.getKey();
            MapTile tile = entry.getValue();
            float[] collisionValues = Collider.checkCollision(new Circle(newX, getHitbox().y, getHitbox().radius), tile, GameScreen.tileSize, true);
            if(abs(collisionValues[0]) > 0 && collisionValues[2] == 1){
                //oldX += getHitbox().radius - collisionValues[0];// * (collisionValues[0] / abs(collisionValues[0]));
                return oldX;
            }
        }
        return newX;
    }
    
    public float resolveCollision_y(float delta){
        
        float oldY = getPos().y;
        float newY = getPos().y + getVel().y * delta;

        for(Map.Entry<Coord, MapTile> entry : map.entrySet()){
            Coord coord = entry.getKey();
            MapTile tile = entry.getValue();
            float[] collisionValues = Collider.checkCollision(new Circle(getHitbox().x, newY, getHitbox().radius), tile, GameScreen.tileSize, true);
            if(abs(collisionValues[1]) > 0 && collisionValues[2] == 1){
                //int dir = (int) (collisionValues[1] / abs(collisionValues[1]));
                //oldY = oldY + (getHitbox().radius - collisionValues[1] * dir) * dir;
                return oldY;
            }
        }
        return newY;
    }
    
    public void move(float x, float y){       
        getAccel().add(x * moveAccel, y * moveAccel);
    }

    /**
     * @return the vel
     */
    public Vector2 getVel(){
        return vel;
    }

    /**
     * @param vel the vel to set
     */
    public void setVel(Vector2 vel){
        this.vel = vel;
    }

    /**
     * @return the accel
     */
    public Vector2 getAccel(){
        return accel;
    }

    /**
     * @param accel the accel to set
     */
    public void setAccel(Vector2 accel){
        this.accel = accel;
    }

    /**
     * @return the moveAccel
     */
    public float getMoveAccel(){
        return moveAccel;
    }

    /**
     * @param moveSpeed the moveAccel to set
     */
    public void setMoveSpeed(float moveSpeed){
        this.moveAccel = moveSpeed;
    }

    /**
     * @return the maxVel
     */
    public float getMaxVel(){
        return maxVel;
    }

    /**
     * @param maxVel the maxVel to set
     */
    public void setMaxVel(float maxVel){
        this.maxVel = maxVel;
    }

    /**
     * @return the friction
     */
    public float getFriction(){
        return friction;
    }

    /**
     * @param friction the friction to set
     */
    public void setFriction(float friction){
        this.friction = friction;
    }

    /**
     * @return the terrainMod
     */
    public float getTerrainMod(){
        return terrainMod;
    }

    /**
     * @param terrainMod the terrainMod to set
     */
    public void setTerrainMod(float terrainMod){
        this.terrainMod = terrainMod;
    }

}
