/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.punchables.rainbowdad.entity;

import com.badlogic.gdx.math.Vector2;
import static java.lang.Math.abs;

/**
 *
 * @author DrShmoogle
 */
public class Enemy extends DynamicGameObject {

    public Enemy(float x, float y, float width, float height){
        super(x, y, width, height);
    }
    
    public void update(float delta){
        updateFields(delta);
        updateMovement(delta);
    }
    
}
