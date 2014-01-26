/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.punchables.rainbowdad.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.punchables.rainbowdad.entity.DynamicGameObject;
import com.punchables.rainbowdad.map.MapTile;
import com.punchables.rainbowdad.map.TileType;
import com.punchables.rainbowdad.screens.GameScreen;
import static java.lang.Math.abs;
import java.util.ArrayList;

/**
 *
 * @author DrShmoogle
 */
public class Collider {
    
    public Collider() {}
    
    /**
     * Take two DynamicGameObjects
     * @param object
     * @param tile
     * @param tileSize
     * @return
     * true if a collision occurred, false if a collision didn't occur
     */
    public static boolean handleTileCollision(DynamicGameObject object, ArrayList<MapTile> collidingTiles, int tileSize){
        
        Vector2 forceVector = new Vector2();
        int offset = 0;
        int errorMargin = 0;
        
        float maxCollisionVector = 0;
        boolean isMaxCollisionVectorX = false;
        MapTile collidingTile = new MapTile();
        int dir_x = 0;
        int dir_y = 0;
                
        
        for(MapTile tile : collidingTiles){
            
            float[] collisionValues = checkCollision(object.getHitbox(), tile, tileSize, true);
            
            //System.out.println(collisionValues[0]);
            
            if(collisionValues[0] > maxCollisionVector){
                collidingTile = tile;
                
                maxCollisionVector = collisionValues[0];
                isMaxCollisionVectorX = true;
                dir_x = (int) (collisionValues[0] / abs(collisionValues[0]));
            }
            if(collisionValues[1] > maxCollisionVector){
                collidingTile = tile;
                
                maxCollisionVector = collisionValues[1];
                isMaxCollisionVectorX = false;
                dir_y = (int) (collisionValues[1] / abs(collisionValues[1]));
            }

            
            //if collision has occured
//            if(collisionValues[2] == 1
//                    && (abs(collisionValues[0]) > errorMargin || abs(collisionValues[1]) > errorMargin)){
//
//                int dir_x = (int) (collisionValues[0] / abs(collisionValues[0]));
//                int dir_y = (int) (collisionValues[1] / abs(collisionValues[1]));
//
//                //check for the greater offset
//                if(collisionValues[0] >= collisionValues[1]){
//                    forceVector.set(-collisionValues[0] - offset * dir_x, 0);
//                    //forceVector.x = -collisionValues[0] + offset * dir_x;
//
//                    object.getVel().x = 0;
//                    System.out.println("X COLLISION! " + System.currentTimeMillis());
//                    //object.getAccel().x = 0;
//
//                } else if(collisionValues[0] < collisionValues[1]){
//                    forceVector.set(0, -collisionValues[1] - offset * dir_y);
//                    //forceVector.y = -collisionValues[1] + offset * dir_y;
//
//                    object.getVel().y = 0;
//                    System.out.println("Y COLLISION! " + System.currentTimeMillis());
//                    //object.getAccel().y = 0;
//                }
//            //System.out.println("x: " + collisionValues[0] + " y: " + collisionValues[1]);
//                //System.out.println(forceVector);
//
//            }
            
        }

        
        
        if(isMaxCollisionVectorX && maxCollisionVector > 0){
            forceVector.set(-maxCollisionVector - offset * dir_x, 0);
            //forceVector.x = -collisionValues[0] + offset * dir_x;

            //object.getVel().x = 0;
            System.out.println("X COLLISION! " + System.currentTimeMillis());
            //object.getAccel().x = 0;

        } else if (!isMaxCollisionVectorX && maxCollisionVector > 0) {
            forceVector.set(0, -maxCollisionVector - offset * dir_y);
            //forceVector.y = -collisionValues[1] + offset * dir_y;

            //object.getVel().y = 0;
            System.out.println("Y COLLISION! " + System.currentTimeMillis());
            //object.getAccel().y = 0;
        } else {
            return false;
        }
        
        //collidingTile.set(TileType.DEBUG);
        
        object.getPos().add(forceVector);
        
        
        
        return true;
        
        //return true;
    }
    
    
    /**
     * Checks for collision between circle and a tile.
     *
     * @param circle
     * @param tile
     * @param tileSize
     * @param checkForSolid
     * @return
     * array containing results of collision check:
     * 0 is if x collides, 1 is if y collides, 2 is if both collide
     * 
     */
    public static float[] checkCollision(Circle circle, MapTile tile,
            int tileSize, boolean checkForSolid){

        //collision values: 0 is diff_x, 1 is diff_y, 2 is boolean collides
        float[] collisionValues = {0, 0, 0};
        
        if(checkForSolid && !tile.get().isSolid()){
            collisionValues[2] = 0;
            return collisionValues;
        }
        
        boolean collidesX = false;
        boolean collidesY = false;
        
        float circleRadius = circle.radius;
        Vector2 circleCenter = new Vector2(circle.x + circleRadius, circle.y + circleRadius);
        
        float tileNorth = tile.getPos().getY() * tileSize + tileSize;
        float tileSouth = tile.getPos().getY() * tileSize;
        float tileWest = tile.getPos().getX() * tileSize;
        float tileEast = tile.getPos().getX() * tileSize + tileSize;
        
        
        //closest points on the rectangle to the circle's center
        float closestX = (float) Utils.clamp(circleCenter.x, tileWest, tileEast);
        
        float closestY = (float) Utils.clamp(circleCenter.y, tileSouth, tileNorth);
        //System.out.println(tileSouth + " " + tileNorth);
        //float closestX = (float) Utils.clampMin(circleCenter.x, tileEast);
        //float closestY = (float) Utils.clampMin(circleCenter.y, tileNorth);
        
        //System.out.println(Utils.clamp(10, 15, 100));
        
        //calculates the distance between the circle's center and the closest point on the rectangle
        float distanceX = circleCenter.x - closestX;
        float distanceY = circleCenter.y - closestY;
        
        //if the distance is less than the circle's radisu, an intersection occurs
        float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
        boolean collides = distanceSquared < (circleRadius * circleRadius);
        
        //System.out.println(distanceX);
        
        //DEBUGGING
//        GameScreen.debugDraw.clearDrawings();
//        GameScreen.debugDraw.addCircle(circleCenter.x, circleCenter.y, 2);
//        GameScreen.debugDraw.addCircle(closestX, closestY, 2);
//        GameScreen.debugDraw.addLine(circleCenter, new Vector2(closestX, closestY));
        
        
        if(collides){
            //tile.set(TileType.DEBUG);
            collisionValues[2] = 1;
            //tile.set(TileType.DEBUG2);
            
        } else {
            collisionValues[2] = 0;
        }
        
        collisionValues[0] = distanceX;
        collisionValues[1] = distanceY;
        
        return collisionValues;
    }
    
    /**
     * Checks for collision between circle and a tile.
     *
     * @param object
     * @param tile
     * @param tileSize
     * @param checkForSolid
     * @return
     * array containing results of collision check:
     * 0 is diff_x, 1 is diff_y, 2 is boolean collides
     * 
     */
    public static float[] OLDcheckCollision(DynamicGameObject object, MapTile tile,
            int tileSize, boolean checkForSolid){

        //collision values: 0 is diff_x, 1 is diff_y, 2 is boolean collides
        float[] collisionValues = {0, 0, 0};
        
        if(checkForSolid && !tile.get().isSolid()){
            collisionValues[2] = 0;
            return collisionValues;
        }
        
        boolean collidesX = false;
        boolean collidesY = false;
        
        float objRadius = object.getHitbox().radius;
        Vector2 objCenter = new Vector2(object.getPos().x + objRadius, object.getPos().y + objRadius);
        
        float objNorth = objCenter.y + objRadius;
        float objSouth = objCenter.y - objRadius;
        float objWest = objCenter.x - objRadius;
        float objEast = objCenter.x + objRadius;
        
        float tileNorth = tile.getPos().getY() * tileSize + tileSize;
        float tileSouth = tile.getPos().getY() * tileSize;
        float tileWest = tile.getPos().getX() * tileSize;
        float tileEast = tile.getPos().getX() * tileSize + tileSize;
        
        //length of collision vector
        float diff_x = 0;
        float diff_y = 0;
       
        if((objEast >= tileWest) && (objEast <= tileEast)){
            diff_x = objEast - tileWest;
            collidesX = true;
        }
        if((objWest <= tileEast) && (objWest >= tileWest)){
            diff_x = objWest - tileEast;
            collidesX = true;
        }
        if((objNorth >= tileSouth) && (objNorth <= tileNorth)){
            diff_y = objNorth - tileSouth;
            collidesY = true;
        }
        if((objSouth <= tileNorth) && (objSouth >= tileSouth)){
            diff_y = objSouth - tileNorth;
            collidesY = true;
        }

        if(collidesX && collidesY){
            //System.out.println(tile.get());
        }
        
        if(collidesX && collidesY){
            collisionValues[2] = 1;
            tile.set(TileType.DEBUG);
            
        } else {
            collisionValues[2] = 0;
        }
        
        collisionValues[0] = diff_x;
        collisionValues[1] = diff_y;
        
        return collisionValues;
    }
    
}
