/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.punchables.rainbowdad.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import java.awt.Point;

/**
 * Cartesian Coordinates
 * @author DrShmoogle
 */
public class Coord {

    private int x;
    private int y;

    public Coord(){
        this(0, 0);
    }

    public Coord(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Coord(Vector2 v){
        this.x = (int) v.x;
        this.y = (int) v.y;
    }
    
    public Coord(Point p){
        this.x = p.x;
        this.y = p.y;
    }

    public Coord(Coord c){
        this.x = c.x;
        this.y = c.y;
    }
    
    
//    public void move(int x, int y){
//        this.x = x;
//        this.y = y;
//    }
//    
//    public void translate(int dx, int dy) {
//        this.x += dx;
//        this.y += dy;
//    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coord) {
            Coord c = (Coord)obj;
            return (x == c.x) && (y == c.y);
        } else if (obj instanceof Point) {
            Point pt = (Point)obj;
            return (x == pt.x) && (y == pt.y);
        }
       
        return false;
        
    }
    public boolean equals(int x, int y) {
            return (this.x == x) && (this.y == y);
    }
    
    @Override
    public int hashCode(){
        int hash = 1;
        hash += x * 17;
        hash += y * 13;
        return hash;        
    }
    
    public void set(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public void set(Coord c){
        this.x = c.x;
        this.y = c.y;
    }
    
    /**
     * @return x
     */
    public int getX(){
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x){
        this.x = x;
    }

    /**
     * @return y
     */
    public int getY(){
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y){
        this.y = y;
    }
    
    public Coord getFromDir(Direction dir, int count){
        switch(dir){
            case NORTH:
                return getNorth(count);
            case SOUTH:
                return getSouth(count);
            case WEST:
                return getWest(count);
            case EAST:
                return getEast(count);
            default:
                return getNorth(count);
        }
    }
    
    public Coord getNorth(int count){
        return new Coord(this.x, this.y + count);
    }
    
    public Coord getSouth(int count){
        return new Coord(this.x, this.y - count);
    }
    
    public Coord getEast(int count){
        return new Coord(this.x + count, this.y);
    }
    
    public Coord getWest(int count){
        return new Coord(this.x - count, this.y);
    }
    
    
    public Coord norm(int tileSize){
        return new Coord(x / tileSize, y / tileSize);
    }
    
    public Coord scale(int scalar){
        return new Coord(x * scalar, y * scalar);
    }
    
    public Coord div(int divisor){
        return new Coord(x / divisor, y / divisor);
    }
    
    public String toString() {
        return "[x=" + x + ",y=" + y + "]";
    }

    
    public static int getDistanceSquared(Coord c1, Coord c2){
        return (int) (Math.pow(c1.x - c2.x, 2) + Math.pow(c1.y - c2.y, 2));
    }
    
}
