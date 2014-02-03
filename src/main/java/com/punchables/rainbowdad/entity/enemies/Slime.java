/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.punchables.rainbowdad.entity.enemies;

import com.badlogic.gdx.Gdx;
import com.punchables.rainbowdad.entity.Enemy;
import com.punchables.rainbowdad.entity.State;
import com.punchables.rainbowdad.map.MapTile;
import com.punchables.rainbowdad.screens.GameScreen;
import com.punchables.rainbowdad.utils.Coord;
import static com.punchables.rainbowdad.utils.Utils.randomi;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author DrShmoogle
 */
public class Slime extends Enemy {

    long lastNewWanderTargetTime = 0;
    Coord wanderTarget = new Coord();
    
    public Slime(float x, float y, float width, float height){
        super(x, y, width, height);
    }
    
    public void update(float delta, ConcurrentHashMap<Coord, MapTile> map){
        updateAll(delta, map);
        handleState(getFSM().getState());
    }
    
    public void handleState(State state){
        
        
        
        switch(state){
            case WANDER:
                //WANDER actions
                long now = System.currentTimeMillis();
                
                long refreshTime = randomi(4000, 7000); //seconds
                
                //System.out.println("--------------\n" + (now - lastNewWanderTargetTime));
                
                if(now - lastNewWanderTargetTime > refreshTime){
                    wanderTarget = chooseNewWanderTarget();
                    lastNewWanderTargetTime = now;
                    //System.out.println("NEW WANDER");
                }
                
                steerTo(wanderTarget.scale(GameScreen.tileSize), 10, true);
                
                break;
            case ATTACK:
                //ATTACK actions
                break;
            case PATROL:
                //PATROL actions
                break;
            case SPECIAL:
                //SPECIAL actions
                break;            
        }
    }

    public Coord chooseNewWanderTarget(){
        Coord curRoomPos = getCurrentRoom().getPos();
        
        int wallOffset = 2;
        
        int newX = randomi(curRoomPos.x + wallOffset, curRoomPos.x + getCurrentRoom().getWidth() - wallOffset);
        int newY = randomi(curRoomPos.y + wallOffset, curRoomPos.y + getCurrentRoom().getHeight() - wallOffset);
        //System.out.println(newX + " " + newY);
        Coord newWanderTarget = new Coord(newX, newY);

        return newWanderTarget;

    }

}
