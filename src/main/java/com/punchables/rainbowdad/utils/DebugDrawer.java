/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.punchables.rainbowdad.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import static com.punchables.rainbowdad.screens.GameScreen.debugDraw;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author DrShmoogle
 */
public class DebugDrawer extends ShapeRenderer{
    
    private ArrayList<Circle> circleList = new ArrayList<>();
    private ArrayList<Vector2> lineListA = new ArrayList<>();
    private ArrayList<Vector2> lineListB = new ArrayList<>();
    
    public DebugDrawer() {}
    
    public void draw(){
        this.begin(ShapeType.Filled);
        setColor(Color.BLUE);
        for(Circle circle : circleList){
            circle(circle.x, circle.y, circle.radius);
        }
        
        //circle(1000, 1000, 100);
        
        this.end();
        
        this.begin(ShapeType.Line);
        for(int i = 0; i < lineListA.size(); i++){
            line(lineListA.get(i), lineListB.get(i));
        }
        this.end();
    }
    
    public void addCircle(float x, float y, float radius){
        circleList.add(new Circle(x, y, radius));
    }
    
    public void addLine(Vector2 aPt, Vector2 bPt){
        lineListA.add(aPt);
        lineListB.add(bPt);
    }
    
    public void clearDrawings(){
        circleList.clear();
        lineListA.clear();
        lineListB.clear();
    }
    
}

