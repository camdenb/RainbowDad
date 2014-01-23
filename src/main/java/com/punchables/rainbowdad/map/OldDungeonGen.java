/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.punchables.rainbowdad.map;

import com.punchables.rainbowdad.utils.Coord;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.punchables.rainbowdad.utils.Direction;
import static com.punchables.rainbowdad.utils.Utils.clampMax;
import static com.punchables.rainbowdad.utils.Utils.isBetween;
import static com.punchables.rainbowdad.utils.Utils.randomi;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import com.punchables.rainbowdad.map.TileType;
import com.punchables.rainbowdad.utils.SeededRandom;
import com.punchables.rainbowdad.utils.Utils;
import static com.punchables.rainbowdad.utils.Utils.randomf;
import java.util.Random;


/**
 *
 * @author DrShmoogle
 */
public class OldDungeonGen{
    /*
    goal algo:
    
     1. Fill the map with unused "earth" tiles.
     2. Start in the middle, and dig out a random sized room (the outside edges of the room are wall tiles, and the inside of the room is filled with floor tiles.
     3. Add the new room to a list of all created rooms.
     4. Pick a random room from the list of all created rooms.
     5. Pick a random wall tile from the selected room.
     6. Generate a new random sized room.
     7. See if there is space for the new room next to the selected wall tile of the selected room.
     8. If yes, continue. If no, go back to step 4.
     9. Dig out the new room to add it to part of the dungeon, and add it to list of completed rooms.
     10. Turn the wall tile picked in step 5 into a door way to make our new room accessible.
     11. Go back to step 4 until the dungeon is complete.
     12. Add the up and down staircases inside random rooms of the dungeon.
     13. Finally, add some monsters, items, and gold in random areas of the dungeon.

    http://breinygames.blogspot.com/2011/07/random-map-generation.html
    
    
    */
    
    //USE A HASHMAP INSTEAD
    //private int[][] dungeonMap;
    private ConcurrentHashMap<Coord, MapTile> dungeonMap = new ConcurrentHashMap<>();
    private ArrayList<Coord> openDoors = new ArrayList<>();
    private ArrayList<Coord> openWalls = new ArrayList<>();
    private int height, width, normHeight, normWidth;
    Texture tile0 = new Texture(Gdx.files.internal("empty16.png"));
    Texture tile1 = new Texture(Gdx.files.internal("tile16.png"));
    Texture tile2 = new Texture(Gdx.files.internal("wall16.png"));
    private int tileSize = 16;
    private int minRoomSize = 8;
    private int maxRoomSize = 10;

    private boolean randomSeed = false;
    private long seed = 1;
    private SeededRandom rand = new SeededRandom(seed);
    
    /*
    
     Cell types:
     0 = empty
     1 = floor
    
    
     */
    public OldDungeonGen(int h, int w){
        this.height = h;
        this.width = w;
        normHeight = h / tileSize;
        normWidth = w / tileSize;

        rand.setSeed(seed);
        //dungeonMap = new int[h][w];
        
    }

    public void generateDungeon(){
//        map = new TiledMap();
//        MapLayers layers = map.getLayers();
//        TextureRegion[][] splitTiles = TextureRegion.split(tile, 32, 32);
//        for(int l = 0; l < 20; l++){
//            TiledMapTileLayer layer = new TiledMapTileLayer(150, 100, 32, 32);
//            for(int x = 0; x < 150; x++){
//                for(int y = 0; y < 100; y++){
//                    int ty = (int) (Math.random() * splitTiles.length);
//                    int tx = (int) (Math.random() * splitTiles[ty].length);
//                    Cell cell = new Cell();
//                    cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
//                    layer.setTile(x, y, cell);
//                }
//            }
//            layers.add(layer);
//        }
//        return map;
        
        long startTime = System.currentTimeMillis();
        
        //gen random seed
        //with the same seed, each first dungeon is the same, 
            //second dungeon same, etc
        if(randomSeed){
            seed = (long) randomf(0, 100000);
            rand.setSeed(seed);
        }
        //System.out.println("Seed: " + seed);
        
        int numberRooms = 1;
        
        System.out.println("");
        //fill entire map with floor
        for(int y = 0; y < height; y += tileSize){
            for(int x = 0; x < width; x += tileSize){
                //System.out.println("x: " + x + ", y: " + y);
                setTile(x, y, TileType.DIRT);
            }
        }
        System.out.println("width = " + width + " height = " + height);
        
        
        //make the rooms
        makeRandomRoom(1);
        System.out.println("GEN'D FIRST DOOR");
        Coord newDoor = openWalls.get(rand.randomi(0, openWalls.size() - 1));
        dungeonMap.get(newDoor).set(TileType.FLOOR);
        for(int i = 0; i < numberRooms; i++){
            boolean success = false;
            while(!success){
                success = makeRoomFromWall(newDoor);
            }
            System.out.println("GEN'D SECOND DOOR");
            openWalls.remove(newDoor);
        }
        
        
        long now = System.currentTimeMillis();
        System.out.println("Time to generate dungeon: " + (now - startTime) + " ms");
    }

    public Texture getTileTexture(MapTile tile){
        TileType tileType = tile.get();
        Texture text;
        switch(tileType){
            case FLOOR:
                text = tile0;
                break;
            case DIRT:
                text = tile1;
                break;
            case WALL:
                text = tile2;
                break;
            default:
                text = tile0;
                System.out.println("NULL TILE");
                break;
        }
        return text;
    }
    
    public void setTile(int x, int y, TileType tileType){
        dungeonMap.put(new Coord(x,y), new MapTile(tileType));
    }

    public ConcurrentHashMap getMap(){
        return dungeonMap;
    }
    
    
    
    public Direction getWallDir(Coord wallPos){
        if(dungeonMap.get(new Coord(wallPos.getX() - 
                tileSize, wallPos.getY())).is(TileType.DIRT)){
            return Direction.EAST;
        } else if(dungeonMap.get(new Coord(wallPos.getX() + 
                tileSize, wallPos.getY())).is(TileType.DIRT)){
            return Direction.WEST;
        } else if(dungeonMap.get(new Coord(wallPos.getX(), 
                wallPos.getY() - tileSize)).is(TileType.DIRT)){
            return Direction.NORTH;
        } else if(dungeonMap.get(new Coord(wallPos.getX(), 
                wallPos.getY() + tileSize)).is(TileType.DIRT)){
            return Direction.SOUTH;
        } else {
            System.out.println(wallPos);
            Gdx.app.log("ERROR", "getWallDir() => not a wall");
            return Direction.NORTH;
        }
    }
    
    public boolean makeRoomFromWall(Coord wallPos){
        
        boolean success = false;
        
        Coord normWallPos = wallPos.norm(tileSize);
        
        Direction wallDir;
        
        float newRoomWidth = rand.randomi(minRoomSize, maxRoomSize) * tileSize;
        float newRoomHeight = rand.randomi(minRoomSize, maxRoomSize) * tileSize;
        
        wallDir = getWallDir(wallPos);
        System.out.println(wallDir);
        switch(wallDir){
            case EAST:
                success = makeRoomFromCoords(false, wallPos.getX() + tileSize, wallPos.getY()
                        + rand.randomi(0, newRoomHeight), 
                        newRoomHeight, newRoomWidth);
                break;
            case WEST:
                success = makeRoomFromCoords(false, wallPos.getX() - tileSize, wallPos.getY()
                        + rand.randomi(0, newRoomHeight), 
                        newRoomHeight, newRoomWidth);
                break;
            case NORTH:
                success = makeRoomFromCoords(false, wallPos.getX() + rand.randomi(0, newRoomWidth), 
                        wallPos.getY(), 
                        newRoomHeight, newRoomWidth);
                break;
            case SOUTH:
                success = makeRoomFromCoords(false, wallPos.getX() - rand.randomi(0, newRoomWidth), 
                        wallPos.getY(), 
                        newRoomHeight, newRoomWidth);
                break;
        }
        
        return success;
    }
    
    public boolean validateRoomPos(Coord roomPos, float roomWidth, float roomHeight){
        
        if(roomPos.getX() > width - roomWidth * tileSize || roomPos.getX() < 0
                || roomPos.getY() > height || roomPos.getY() < roomHeight * tileSize){
            return false;
        } else {
            return true;
        }
        
    }
    
    public boolean makeRandomRoom(int count){
        for(int i = 0; i < count; i++){
           
            float newRoomWidth = rand.randomi(minRoomSize, maxRoomSize);
            float newRoomHeight = rand.randomi(minRoomSize, maxRoomSize);
            
            int xPos = rand.randomi(2, normWidth - newRoomWidth - 2);
            int yPos = rand.randomi(2, normHeight - newRoomHeight - 2);
            
            //System.out.println("xPos = " + xPos + " yPos = " + yPos);
            makeRoom(false, xPos, yPos, newRoomHeight, newRoomWidth);
        }
        return true;
    }
    
    public boolean makeRoomFromCoords(boolean initialRoom, int xPos, int yPos, float h, float w){
        int f = tileSize;
        return makeRoom(initialRoom, xPos / f, yPos / f, h / f, w / f);
    }
    
    //takes normalized (smaller) units, not exact coords
    public boolean makeRoom(boolean initialRoom, int xPos, int yPos, float h, float w){
        boolean generateWalls = true;
        boolean generateDoor = false;
        
        int roomXPos = xPos * tileSize;
        int roomYPos = yPos * tileSize;
        float roomWidth = w * tileSize;
        float roomHeight = h * tileSize;
        
        int floorXPos = (xPos - 1) * tileSize;
        int floorYPos = (yPos - 1) * tileSize;
        float floorWidth = (w + 2) * tileSize;
        float floorHeight = (h + 2) * tileSize;
        
        if(!validateRoomPos(new Coord(xPos, yPos), h, w)){
            return false;
        }
        
        //creates room
        for(int y = roomYPos; y < roomYPos + roomHeight; y += tileSize){
            for(int x = roomXPos; x < roomXPos + roomWidth; x += tileSize){
                setTile(x, y, TileType.FLOOR);
                //System.out.println("x = " + x + ", y = " + y);
            }
        }
        
        Coord[] walls = new Coord[(int)((2 * (w + 2)) + (2 * h))];
        
        //System.out.println(walls.length);
        
        //creates walls
        if(generateWalls){
            for(int y = floorYPos, i = 0; y < floorYPos + floorHeight; y += tileSize){
                for(int x = floorXPos; x < floorXPos + floorWidth; x += tileSize){
                    if(!isBetween(x, roomXPos, roomXPos + roomWidth - tileSize)
                            || !isBetween(y, roomYPos, roomYPos + roomHeight - tileSize)){
                        //if a floor already exists, don't replace it with a wall
                        if(dungeonMap.get(new Coord(x,y)) == null){
                            break;
                        } else if(!dungeonMap.get(new Coord(x, y)).is(TileType.FLOOR)){
                            setTile(x, y, TileType.WALL);
                            walls[i] = new Coord(x, y);
                            openWalls.add(new Coord(x, y));
                            i++;
                        } else {
                            //setTile(x, y, 0);
                            walls[i] = new Coord(x, y);
                            openWalls.add(new Coord(x, y));
                            i++;
                        }
                    }
                }
            }
        }
        Coord doorCoord = new Coord();
        if(generateDoor){
            int doorTile = rand.randomi(1, walls.length - 2);
            if(!initialRoom){
                
                int tries = 0;
                //check if corner, by position in array of corners
                while(doorTile == 0 || doorTile == w + 1 ||
                        doorTile == walls.length - 2 - w || doorTile == walls.length - 1 ){
                    System.out.println("Generating new door");
                    
                    doorTile = rand.randomi(1, walls.length - 2);
                    if(tries > 12){
                        System.out.println("Could not generate door");
                        continue;
                    }
                    tries++;
                }
                //System.out.println(doorTile);
                
            } else {
                //doorTile = doorPos;
            }
            doorCoord = new Coord(walls[doorTile].getX(), walls[doorTile].getY());
            System.out.println("Door tile: " + doorTile);
            setTile(walls[doorTile].getX(), walls[doorTile].getY(), TileType.DIRT);
            openDoors.add(doorCoord);
        }
        
        
        return true;
    }

    public boolean printDungeon(){
        for(int y = height; y > 0; y -= tileSize){
            for(int x = width; x > 0; x -= tileSize){
                MapTile currentTile = dungeonMap.get(new Coord(x,y));
                //System.out.println("x = " + x + ", y = " + y);
                if(currentTile == null){
                    System.out.println("Tile at " + x + ", " + y + " does not exist.");
                    //return false;
                }
                if(currentTile.is(TileType.DIRT)){
                    System.out.print("o");
                } else {
                    System.out.print("*");
                }
            }
            System.out.println("");
        }
        return true;
    }
    
}
