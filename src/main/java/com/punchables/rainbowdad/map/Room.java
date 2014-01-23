/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.punchables.rainbowdad.map;

import com.punchables.rainbowdad.utils.Coord;
import static com.punchables.rainbowdad.utils.Utils.isBetween;
import java.util.ArrayList;

/**
 *
 * @author DrShmoogle
 */
public class Room {
    
    private int height, width;
    private Coord pos, center;
    
    public Room(int width, int height, Coord pos){
        this.width = width;
        this.height = height;
        this.pos = pos;       
        this.center = new Coord();
        this.recalcCenter();
    }
    
    public void recalcCenter(){
        this.getCenter().set((getPos().getX() + getWidth()) / 2, (getPos().getY() + getHeight()) / 2);
    }
    
    /**
     * Generates walls for the given room
     * 
     * @return An array containing an ArrayList of all of the walls in the room
     * at index of 0, and all of the non-diagonal walls in the room at index of 1 
     */
    public ArrayList[] genWalls(){
        
        ArrayList<Coord> nondiagonalWallList = new ArrayList<>((2 * width) + (2 * (height - 2)));
        ArrayList<Coord> wallList = new ArrayList<>((2 * width) + (2 * (height - 2)));
        
        ArrayList[] listsOfWalls = {wallList, nondiagonalWallList};
        
        Coord wallPos = new Coord(pos.getX() - 1, pos.getY() - 1);
        int wallWidth = width + 2;
        int wallHeight = height + 2;
        
        for(int y = wallPos.getY(), i = 0, j = 0; y < wallPos.getY() + wallHeight; y += 1){
            for(int x = wallPos.getX(); x < wallPos.getX() + wallWidth; x += 1){
                if(!isBetween(x, pos.getX(), pos.getX() + width - 1)
                        || !isBetween(y, pos.getY(), pos.getY() + height - 1)){
                    
                    wallList.add(i, new Coord(x, y));

                    //check for diagonals
                    Coord currentCoord = new Coord(x,y);
                    if(
                            !currentCoord.equals(wallPos.getX(), 
                                    wallPos.getY()) &&
                            !currentCoord.equals(wallPos.getX() + wallWidth - 1, 
                                    wallPos.getY()) &&
                            !currentCoord.equals(wallPos.getX(), 
                                    wallPos.getY() + wallHeight - 1) &&
                            !currentCoord.equals(wallPos.getX() + wallWidth - 1, 
                                    wallPos.getY() + wallHeight - 1)                            
                            ){
                        nondiagonalWallList.add(j, new Coord(x, y));
                        
                        j++;
                    }
                    i++;
                }
            }
        }
        return listsOfWalls;
    }

    public Coord[] getListOfTileCoords(){
        Coord[] listOfTiles = new Coord[height * width];
        
        for(int y = 0, i = 0; y < height; y++){
            for(int x = 0; x < width; x++, i++){
                listOfTiles[i] = new Coord(pos.getX() + x, pos.getY() + y);
            }
        }
        
        return listOfTiles;
    }
    
    public boolean equals(Room room){
        return (room.pos.equals(pos) && 
                room.height == height && 
                room.width == width);
    }
    
    /**
     * @return the height
     */
    public int getHeight(){
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height){
        this.height = height;
    }

    /**
     * @return the width
     */
    public int getWidth(){
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width){
        this.width = width;
    }

    /**
     * @return the pos
     */
    public Coord getPos(){
        return pos;
    }

    /**
     * @param pos the pos to set
     */
    public void setPos(Coord pos){
        this.pos = pos;
    }

    /**
     * @return the center
     */
    public Coord getCenter(){
        return center;
    }
    
}
