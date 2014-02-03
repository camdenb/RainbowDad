/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.punchables.rainbowdad.entity;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.punchables.rainbowdad.map.MapTile;
import com.punchables.rainbowdad.map.Room;
import com.punchables.rainbowdad.map.TileType;
import com.punchables.rainbowdad.screens.GameScreen;
import com.punchables.rainbowdad.utils.Collider;
import com.punchables.rainbowdad.utils.Coord;
import static java.lang.Math.abs;
import java.util.ArrayList;
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
    private Room currentRoom = new Room(100, 100, new Coord(100, 100));
    private boolean colliding = true;
    private boolean isMapNull = true;
    private ArrayList<MapTile> collidableTiles = new ArrayList<>();
    private float collisionOffset = .1f;
    
    private float lastGoodXPos = 0;
    private float lastGoodYPos = 0;
    
    private ConcurrentHashMap<Coord, MapTile> map = new ConcurrentHashMap<>();

    public DynamicGameObject(float x, float y, float width, float height){
        super(x, y, width, height);
        this.vel = new Vector2();
        this.accel = new Vector2();
    }
    
    public void updateAll(float delta, ConcurrentHashMap<Coord, MapTile> map){
        updateTileMap(map);
        updateMovement(delta);
        updateFields(delta);
        if(!isMapNull){
            if(isColliding()){
                refreshCollidableTiles(96);
            } else {
                if(!collidableTiles.isEmpty()){
                    collidableTiles.clear();
                }
            }
        }
        //ensureNoCollisions();
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
        if(map != null){
            isMapNull = false;
        }
    }

    public ArrayList refreshCollidableTiles(int offset){
        
        collidableTiles.clear();
        
        Coord initPos = new Coord((int) getPos().x - offset, 
                (int) getPos().y - offset).div(GameScreen.tileSize);
        Coord endPos = new Coord((int) getPos().x + 2 * offset, 
                (int) getPos().y + 2 * offset).div(GameScreen.tileSize);
        
        for(int x = initPos.getX(); x < endPos.getX(); x += 1){
            for(int y = initPos.getY(); y < endPos.getY(); y += 1){
                if(!isMapNull && x > 0 && y > 0){
                    collidableTiles.add(getMap().get(new Coord(x, y)));
                    //map.get(new Coord(x, y)).set(TileType.NONE);
                }
            }
        }
        
        return collidableTiles;
        
    }
    
    public ArrayList getCollidableTiles(int offset){       
        return refreshCollidableTiles(offset);
    }

    public float resolveCollision_x(float delta){
        
        float oldX = getPos().x;
        float newX = getPos().x + getVel().x * delta;

        if(!isMapNull){
            for(MapTile tile : collidableTiles){
                float[] collisionValues = Collider.checkCollision(new Circle(newX, getHitbox().y, getHitbox().radius), tile, GameScreen.tileSize, true);
                //GameScreen.debugDraw.addCircle(tile.getPos().getX() * GameScreen.tileSize, tile.getPos().getY() * GameScreen.tileSize, 32);
                if(abs(collisionValues[0]) > 0 && collisionValues[2] == 1){
                    //oldX += getHitbox().radius - collisionValues[0];// * (collisionValues[0] / abs(collisionValues[0]));
                    lastGoodXPos = oldX;
                    return oldX;
                }
            }
        }
        return newX;
    }
    
    public float resolveCollision_y(float delta){

        float oldY = getPos().y;
        float newY = getPos().y + getVel().y * delta;
        if(!isMapNull){
            for(MapTile tile : collidableTiles){
                float[] collisionValues = Collider.checkCollision(new Circle(getHitbox().x, newY, getHitbox().radius), tile, GameScreen.tileSize, true);
                if(abs(collisionValues[1]) > 0 && collisionValues[2] == 1){
                //int dir = (int) (collisionValues[1] / abs(collisionValues[1]));
                    //oldY = oldY + (getHitbox().radius - collisionValues[1] * dir) * dir;
                    lastGoodYPos = oldY;
                    return oldY;
                }
            }
        }
        return newY;
    }

    public void ensureNoCollisions(){
        if(!isMapNull){
            for(MapTile tile : collidableTiles){
                float[] collisionValues = Collider.checkCollision(new Circle(getHitbox().x, getHitbox().y, getHitbox().radius), tile, GameScreen.tileSize, true);
                if(collisionValues[2] == 1){
                    if(collisionValues[0] >= collisionValues[1]){
                        getPos().x = lastGoodXPos;
                    } else if(collisionValues[1] > collisionValues[0]){
                        getPos().y = lastGoodYPos;
                    }
                }
            }
        }
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

    /**
     * @return the colliding
     */
    public boolean isColliding(){
        return colliding;
    }

    /**
     * @param colliding the colliding to set
     */
    public void setColliding(boolean colliding){
        this.colliding = colliding;
    }

    /**
     * @return the map
     */
    public ConcurrentHashMap<Coord, MapTile> getMap(){
        return map;
    }

    /**
     * @return the currentRoom
     */
    public Room getCurrentRoom(){
        return currentRoom;
    }

    /**
     * @param currentRoom the currentRoom to set
     */
    public void setCurrentRoom(Room currentRoom){
        this.currentRoom = currentRoom;
    }

}
