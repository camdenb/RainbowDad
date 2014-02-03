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
import com.punchables.rainbowdad.utils.StateMachine;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author DrShmoogle
 */
public class Enemy extends DynamicGameObject {

    private StateMachine fsm = new StateMachine(State.WANDER);

    public Enemy(){
        super(0, 0, 64, 64);
    }
    
    public Enemy(float x, float y, float width, float height){
        super(x, y, width, height);
        setMaxVel(300);
    }
    
    public void update(float delta, ConcurrentHashMap<Coord, MapTile> map){
        updateAll(delta, map);
    }

    public StateMachine getFSM(){
        return fsm;
    }
 
    public void steerToVector2(Vector2 target, int radius, boolean avoidTiles){
        steerTo(new Coord(target), radius, avoidTiles);
    }
    
    public void steerTo(Coord target, int radius, boolean avoidTiles){
        //System.out.println(new Vector2(pos.x - getPos().x, pos.y - getPos().y));
        //setAccel(new Vector2(pos.x - getPos().x, pos.y - getPos().y).scl(200));
        
        boolean withinRadius = abs(target.x - getPos().x) <= radius && abs(target.y - getPos().y) <= radius;
        
        if(!withinRadius){
            Vector2 desiredVel = new Vector2(target.x - getPos().x, target.y - getPos().y).nor().scl(getMaxVel());
            Vector2 steeringVel = desiredVel.sub(getVel());
        //System.out.println(desiredVel + " " + steeringVel);
            
            if(avoidTiles){
                int maxAvoidForce = 64;
                ArrayList<MapTile> collidableTiles = refreshCollidableTiles(128);
                MapTile closestTile = null;
                int closestTileDist = Coord.getDistanceSquared(new Coord(getPos()), collidableTiles.get(0).getPos());
                for(MapTile tile : collidableTiles){
                    //float[] collideArray = Collider.checkCollision(new Circle(ahead, 1), tile, GameScreen.tileSize, true);
                    int distSquared = Coord.getDistanceSquared(new Coord(getPos()), tile.getPos());
                    if(distSquared < closestTileDist){
                        closestTileDist = distSquared;
                        closestTile = tile;
                    }
                }
                
                Vector2 avoidanceVelocity = new Vector2();
                if(closestTile != null){
                    avoidanceVelocity = new Vector2(closestTile.getPos().x - getPos().x, closestTile.getPos().y - getPos().y).nor();
                    avoidanceVelocity.scl(maxAvoidForce);
                } else {
                    avoidanceVelocity = new Vector2();
                    System.out.println("no close tiles");
                }
                
                steeringVel.add(avoidanceVelocity.scl(-1));
                
            }
            
            getVel().add(steeringVel);
        }
        
    }

}
