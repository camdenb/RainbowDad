/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.punchables.rainbowdad.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.punchables.rainbowdad.utils.Coord;
import com.punchables.rainbowdad.utils.Direction;
import com.punchables.rainbowdad.utils.SeededRandom;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 *
 * @author DrShmoogle
 */
public class DungeonGen{
    
    private ConcurrentHashMap<Coord, MapTile> dungeonMap = new ConcurrentHashMap<>();
    private ArrayList<Coord> openWalls = new ArrayList<>();
    private ArrayList<Coord> wallSeeds = new ArrayList<>();
    private ArrayList<Coord> wallsList = new ArrayList<>();
    private ArrayList<Room> roomList = new ArrayList<>();
    private int dungeonHeight, dungeonWidth;
    Texture tile_debugtall = new Texture(Gdx.files.internal("64/debug64-tall.png"));
    Texture tile_debug = new Texture(Gdx.files.internal("16/debug2-16.png"));
    Texture tile_floor = new Texture(Gdx.files.internal("64/floor64.png"));
    Texture tile_dirt = new Texture(Gdx.files.internal("64/dirt64.png"));
    Texture tile_empty = new Texture(Gdx.files.internal("64/empty64.png"));
    Texture tile_walltall = new Texture(Gdx.files.internal("64/wall-tall.png"));
    Texture tile_wall = new Texture(Gdx.files.internal("64/wall64.png"));
    Texture tile_solidwall = new Texture(Gdx.files.internal("64/wall-tall2.png"));
    Texture tile_door = new Texture(Gdx.files.internal("16/door16.png"));
    Texture tile_ceiling = new Texture(Gdx.files.internal("64/ceiling64.png"));
    
    private int totalDungeons = 0;
    //number of tries to generate a dungeon
    private int newDungeons = 0;
    private int maxDungeonTries = 3;
    
    private int maxRooms = 20;
    private int minRoomSize = 10;
    private int maxRoomSize = 20;
    private int meanRoomSize = 15;
    private int stdRoomDev = 5;
    
    //max difference between height and length
    private int roomOblongness = 10;
            
    //must be at least 2 less than than minRoomSize
    private int doorSize = 4;
    private int wallOffset = 1;
    
    //times to try to generate a new room
    private int maxTries = 100;
    
    private boolean randomSeed = false;
    private long seed = 2239;
    private SeededRandom rand;
    
    public DungeonGen(int h, int w){
        this.dungeonHeight = h;
        this.dungeonWidth = w;

        setMapToTile(TileType.DIRT);
        clearOldDungeon();
        
        if((h*w) < maxRooms * ((minRoomSize + maxRoomSize) / 2) + 100){
            Gdx.app.log("WARNING", "Beware of infinite dungeon generation.");
        }
        
        rand = new SeededRandom(seed);     
    }
    
    public void generateDungeon(){
        
        //*******
        //Step 0: clear remnants of last dungeon
        //      a) clear openWalls
        //      b) choose new seed (if randomSeed = true)
        //      c) set dungeon start time
        //*******
        
        //dungeonMap = new ConcurrentHashMap<>();
        setMapToTile(TileType.DIRT);
        clearOldDungeon();
        
        totalDungeons++;
        Gdx.app.log("DungeonGen", "Starting generation of dungeon " + totalDungeons + ".");
        long startTime = System.currentTimeMillis();
        
        openWalls = new ArrayList<Coord>();
        wallSeeds = new ArrayList<>();
        if(randomSeed){
            seed = System.currentTimeMillis() * (long) rand.randomf(0, 1000);
        }
        
        //*******
        //Step 1: Fill dungeon with dirt
        //*******
        
        for(int y = 0; y < dungeonHeight; y += 1){
            for(int x = 0; x < dungeonWidth; x += 1){
                //System.out.println("x: " + x + ", y: " + y);
                setTile(x, y, TileType.DIRT);
            }
        }
        
        Gdx.app.log("DungeonGen", "Dungeon filled with dirt.");
        
        //*******
        //Step 2: Place room 
        //      a): add all non-diagonal walls to list of walls
        //          (done within room generation)
        //*******
        
        //random size
        int newW = (int) rand.randomGaussian(minRoomSize, maxRoomSize, 
                                            meanRoomSize, stdRoomDev);
        int newH = (int) rand.randomGaussian(minRoomSize, maxRoomSize, 
                                            meanRoomSize, stdRoomDev);
        //random coords, near center
        int newX = rand.randomi(0.25 * dungeonWidth, 0.55 * dungeonWidth);
        int newY = rand.randomi(0.25 * dungeonHeight, 0.55 * dungeonHeight);
        attemptRoom(new Room(newW, newH, new Coord(newX, newY)));
        roomList.add(new Room(newW, newH, new Coord(newX, newY)));
        Gdx.app.log("DungeonGen", "First room generated.");
        

        //*******
        //Step 3: Choose a wall to base the new room off of
        //      a): also start the loop to generate new rooms
        //*******
        
        //each pass of this loop generates a new room
        for(int i = 1; i < maxRooms; i++){
            Direction wallSeedDirection = Direction.NORTH;
            Coord wallSeed = new Coord();
            
            int tries = 0;
            boolean success = false;
            
            while(tries <= maxTries && !success){
                if(openWalls.size() > 0){
                    wallSeed = openWalls.get(rand.randomi(0, openWalls.size() - 1));
                }
                Gdx.app.log("DungeonGen", "Obtained new wall seed");
                //setTile(wallSeed, TileType.FLOOR);
        
        //*******
        //Step 4: Find direction of the new wall
        //      a) check if wall can fit a door
        //*******
        
                Direction wallDir = getWallDir(wallSeed);
           
                //if tile to either side of the wallSeed doorSize spaces away is not a wall, choose a new wall
                if(dungeonMap.get(
                        wallSeed.getFromDir(wallDir.getClockwise(), doorSize + 1)) != null
                        && dungeonMap.get(
                                wallSeed.getFromDir(wallDir.getCounterClockwise(), doorSize + 1)) != null){
                    if(!wallsList.contains(wallSeed.getFromDir(wallDir.getClockwise(), doorSize + 1))
                            || !wallsList.contains(wallSeed.getFromDir(wallDir.getCounterClockwise(), doorSize + 1))){
                        //setTile(wallSeed, TileType.WALL);
                        openWalls.remove(wallSeed);
                        tries++;
                        continue;
                    }
                }

        
        //*******
        //Step 5: Generate new room size/pos
        //      a) check for room space
        //      b) create the new room
        //      c) add the new room to the list of rooms
        //*******

                //int nextRoomWidth = rand.randomi(minRoomSize, maxRoomSize);
                //int nextRoomHeight = rand.randomi(minRoomSize, maxRoomSize);
                int nextRoomWidth = (int) rand.randomGaussian(minRoomSize, maxRoomSize, 
                                            meanRoomSize, stdRoomDev);
                int nextRoomHeight = (int) rand.randomGaussian(minRoomSize, maxRoomSize, 
                                            meanRoomSize, stdRoomDev);
                Coord nextRoomPos;

                switch(wallDir){
                    case NORTH:
                        nextRoomPos = new Coord(wallSeed.getX() - rand.randomi(0, nextRoomWidth - doorSize - 1),
                                wallSeed.getY() + 2);
                        break;
                    case SOUTH:
                        nextRoomPos = new Coord(wallSeed.getX() - rand.randomi(0, nextRoomWidth - doorSize - 1),
                                wallSeed.getY() - nextRoomHeight - 1);
                        break;
                    case WEST:
                        nextRoomPos = new Coord(wallSeed.getX() - nextRoomWidth - 1,
                                wallSeed.getY() - rand.randomi(0, nextRoomHeight - doorSize - 1));
                        break;
                    case EAST:
                        nextRoomPos = new Coord(wallSeed.getX() + 2,
                                wallSeed.getY() - rand.randomi(0, nextRoomHeight - doorSize - 1));
                        break;
                    default:
                        nextRoomPos = new Coord();
                        break;
                }

                Room nextRoom = new Room(nextRoomWidth, nextRoomHeight, nextRoomPos);
                                
                //if room is outside the dungeon or a room exists, choose a new wall tile               
                if(!isRoomValid(nextRoom)){
                    tries++;
                    //setTile(wallSeed, TileType.WALL);
                    Gdx.app.log("DungeonGen", "Room discarded.");
                    openWalls.remove(wallSeed);
                } else {
                    attemptRoom(nextRoom);
                    setTile(wallSeed, TileType.FLOOR);
                    success = true;
                    wallSeeds.add(wallSeed);
                    roomList.add(nextRoom);
                    Gdx.app.log("DungeonGen", "Room generated.");
                    wallSeedDirection = wallDir;
                    Gdx.app.log("DungeonGen", "Wall Direction is " + wallDir);
                    //break;
                }
            }
            //check if the while loop broke because of try limit
            if(tries > maxTries){
                Gdx.app.log("DungeonGen", "Room generation failed. Generating new dungeon...");
                newDungeons++;
                if(newDungeons > maxDungeonTries){
                    Gdx.app.log("ERROR", "Could not add more rooms.");
                    break;
                } else {
                    generateDungeon();
                    break;
                }
            }

        //*******
        //Step 6: Refresh openWall list
        //      
        //*******
            openWalls.remove(wallSeed);
            refreshOpenWalls();
        
        //*******
        //Step 7: Clear doorways
        //*******
        
            //clearDoors(wallSeed, wallSeedDirection);
            setTile(wallSeed, TileType.FLOOR);
            
        //*******
        //Step 4: 
        //*******
        
        //*******
        //Step 4: 
        //*******
        
        
        }
        
        //go through all of the wall seeds
        for(Coord c : wallSeeds){
            //setTile(c, TileType.FLOOR);
            clearDoors(c, getWallDir(c));
        }
        for(Coord c : wallsList){
            if(dungeonMap.get(c.getSouth(1)).is(TileType.FLOOR)){
                setTile(c.getSouth(1), TileType.WALL);
            }
        }
        
        
        newDungeons = 0;
        
        long now = System.currentTimeMillis();
        Gdx.app.log("DungeonGen", "Time to generate dungeon: " + (now - startTime) + "ms");
        
        
        
    }
    
    public void clearOldDungeon(){
        dungeonMap.clear();
        wallsList.clear();
        wallSeeds.clear();
        openWalls.clear();
        roomList.clear();
    }

    public boolean isRoomValid(Room nextRoom){
        if(isRoomOutsideDungeon(nextRoom)){
            Gdx.app.log("DungeonGen", "Room outside dungeon, trying again...");
            return false;
        } else if(isRoomOverlapping(nextRoom)){
            Gdx.app.log("DungeonGen", "Room overlapping another, trying again...");
            return false;

        } else {
            return true;
        }
    }
    
    public boolean attemptRoom(Room newRoom){
     
        int roomXPos = newRoom.getPos().getX();
        int roomYPos = newRoom.getPos().getY();
        int roomWidth = newRoom.getWidth();
        int roomHeight = newRoom.getHeight();
        
        
        for(int y = roomYPos; y < roomYPos + roomHeight; y++){
            for(int x = roomXPos; x < roomXPos + roomWidth; x++){
                setTile(x, y, TileType.FLOOR);
                //System.out.println("x = " + x + ", y = " + y);
            }
        }
        
        ArrayList[] roomWalls = newRoom.genWalls();
        
        ArrayList<Coord> currentRoomWalls = roomWalls[0];
        ArrayList<Coord> currentRoomNonDiagWalls = roomWalls[1];
        
        wallsList.addAll(currentRoomWalls);
        openWalls.addAll(currentRoomNonDiagWalls);
        
//        for(Coord c : currentRoomWalls){
//            if(c.getY() == roomYPos + roomHeight){
//                setTile(c, TileType.NONE);
//                System.out.println(c.getY() + "==" + (roomYPos + roomHeight));
//            } else {
//                //System.out.println(c.getY() + "==" + (roomYPos + roomHeight));
//                setTile(c, TileType.WALL);
//            }
//        }
//        
        for(int i = 0; i < currentRoomWalls.size(); i++){
            Coord currentCoord = currentRoomWalls.get(i);
            setTile(currentCoord, TileType.CEILING);
        }
        
        return false;
    }
    
    public boolean attemptDoor(Coord tile1, Coord tile2){
        return false;
    }
    
    public boolean checkSpace(Coord wallPos, int roomWidth, int roomHeight){
        return false;
    }
    
    public void refreshOpenWalls(){
        
    }
    
    /**
     * Clears a doorway given one wall and its direction, and also adds a door
     * @param doorPos
     * @param wallDir
     * @return
     * true if a door is generated
     */
    public boolean clearDoors(Coord doorPos, Direction doorDir){

        //System.out.println("RECEIVED doorPos: " + doorPos);
        
        Coord newDoorPos = new Coord();
        Coord[] newDoors = new Coord[doorSize * 2];
        
//        switch(doorDir){
//            case NORTH:
//                newDoors[0] = doorPos.getNorth(1);
//                break;
//            case SOUTH:
//                newDoors[0] = doorPos.getSouth(1);
//                break;
//            case WEST:
//                newDoors[0] = doorPos.getWest(1);
//                break;
//            case EAST:
//                newDoors[0] = doorPos.getEast(1);
//                break;
//            default:
//                Gdx.app.log("DungeonGen", "Default condition met for clearing doors.");
//                newDoorPos = doorPos.getNorth(1);
//                break;
//        }
        
        //newDoors = getArrayOfDoors(doorPos, doorDir);
        
        //setTile(newDoorPos, TileType.FLOOR);
        setTile(doorPos, TileType.FLOOR);
        
        for(Coord c : getArrayOfDoors(doorPos, doorDir)){
            wallsList.remove(c);
            setTile(c, TileType.FLOOR);
        }
        
        return true;
    }
    
    public Coord[] getArrayOfDoors(Coord doorPos, Direction doorDir){
        
        Coord[] newDoors = new Coord[doorSize * 2];
        Coord curCoord = new Coord(doorPos);
        
        int yLimit, xLimit;
        int yIncr = 1, xIncr = 1;
        
        switch(doorDir){
            case NORTH:
                yLimit = curCoord.getY() + 2;
                xLimit = curCoord.getX() + doorSize;
                break;
            case SOUTH:
                curCoord = curCoord.getSouth(1);
                yLimit = curCoord.getY() + 2;
                xLimit = curCoord.getX() + doorSize;
                break;
            case WEST:
                curCoord = curCoord.getWest(1);
                //curCoord = curCoord.getNorth(1);
                //shifted north 1 to account for 3/4 persp.
                yLimit = curCoord.getY() + doorSize;
                xLimit = curCoord.getX() + 2;
                break;
            case EAST:
                //curCoord = curCoord.getEast(1);
                //curCoord = curCoord.getNorth(1);
                //shifted north 1 to account for 3/4 persp.
                yLimit = curCoord.getY() + doorSize;
                xLimit = curCoord.getX() + 2;
                break;
            default:
                yLimit = 0;
                xLimit = 0;
                break;
        }
                
        
        
        int count = 0;
        for(int y = curCoord.getY(); y < yLimit; y += yIncr){
            for(int x = curCoord.getX(); x < xLimit; x+= xIncr){
                newDoors[count] = new Coord(x,y);
//                System.out.println("Coord! " +  new Coord(x,y));
//                System.out.println("Count : " + count);
                count++;
            }
            
        }
        
        return newDoors;
    }
    
    public Direction getWallDir(Coord wallPos) throws NullPointerException {
        
        try{
        if(dungeonMap.get(wallPos.getNorth(1)).is(TileType.FLOOR)){
            return Direction.SOUTH;
        } else if(dungeonMap.get(wallPos.getSouth(1)).is(TileType.FLOOR)){
            return Direction.NORTH;
        } else if(dungeonMap.get(wallPos.getWest(1)).is(TileType.FLOOR)){
            return Direction.EAST;
        } else if(dungeonMap.get(wallPos.getEast(1)).is(TileType.FLOOR)){
            return Direction.WEST;
        } else {
            Gdx.app.log("ERROR", "Could not determine wall direction");
            return Direction.NORTH;
        }
        } catch(NullPointerException npe){
            Gdx.app.log("ERROR", "NPE at GetWallDir()");
            return Direction.NORTH;
        }
    }
    
    public boolean isRoomOutsideDungeon(Room room){
        if(
                room.getPos().getX() < 1 + wallOffset ||
                room.getPos().getX() + room.getWidth() > dungeonWidth - wallOffset + 1 ||
                room.getPos().getY() < 1 + wallOffset ||
                room.getPos().getY() + room.getHeight() > dungeonHeight - wallOffset - 1
                ){
            return true;
        }
        return false;
    }
    
    /**
     * Checks if a new room is overlapping a previous room
     * @param room
     * @return
     */
    public boolean isRoomOverlapping(Room room){
        for(Coord c : room.getListOfTileCoords()){
            //if a floor exists
            if(dungeonMap.get(c).is(TileType.FLOOR) || 
                    dungeonMap.get(c).is(TileType.CEILING)){
                return true;
            }
        }
        
        return false;
    }
    
    public ConcurrentHashMap getMap(){
        return dungeonMap;
    }
    
    public void setMapToTile(TileType tileType){
        for(Entry<Coord, MapTile> entry : dungeonMap.entrySet()){
            Coord coord = entry.getKey();
            
            setTile(coord, tileType);
            
        }
    }
    
    public Texture getTileTexture(MapTile tile){
        TileType tileType = tile.get();
        Texture text;
        switch(tileType){
            case FLOOR:
                text = tile_floor;
                break;
            case DIRT:
                text = tile_dirt;
                break;
            case CEILING:
                text = tile_ceiling;
                break;
            case WALL:
                text = tile_wall;
                break;
            case WALLTALL:
                text = tile_walltall;
                break;
            case SOLIDWALL:
                text = tile_solidwall;
                break;
            case DOOR:
                text = tile_door;
                break;
            case DEBUG:
                text = tile_debug;
                break;
            case DEBUG2:
                text = tile_debugtall;
                break;
            default:
                text = tile_empty;
                //System.out.println("NULL TILE");
                break;
        }
        return text;
    }
    
    public void setTile(int x, int y, TileType tileType){
        Coord newCoord = new Coord(x,y);
        dungeonMap.put(newCoord, new MapTile(tileType, newCoord));
    }
    
    public void setTile(Coord coordinate, TileType tileType){
        dungeonMap.put(coordinate, new MapTile(tileType, coordinate));
    }
    
}
