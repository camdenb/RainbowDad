package com.punchables.rainbowdad.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.punchables.rainbowdad.utils.Coord;
import org.lwjgl.util.vector.Vector2f;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author DrShmoogle
 */
public abstract class GameObject{
    
    private Vector2 pos;
    private Circle hitbox;
    private String texture;
    private float width;
    private float height;
    
    
    public GameObject(float x, float y, float width, float height){
        this.pos = new Vector2(x, y);
        //this.hitbox = new Rectangle(x - width / 2, y - height / 2, width, height);
        this.hitbox = new Circle(x, y, width / 2);
        this.height = height;
        this.width = width;
        
    }
    
    public void updateFields(float delta){
        this.hitbox = new Circle(pos.x, pos.y, width / 2);
    }

    /**
     * @return the pos
     */
    public Vector2 getPos(){
        return pos;
    }
    
    public Coord getPosCoord(){
        return new Coord(pos.x, pos.y);
    }

    /**
     * @param pos the pos to set
     */
    public void setPos(Vector2 pos){
        this.pos = pos;
    }
    
    public void setPos(Coord pos){
        this.pos.x = pos.getX();
        this.pos.y = pos.getY();
    }

    /**
     * @return the hitbox
     */
    public Circle getHitbox(){
        return hitbox;
    }

    /**
     * @param hitbox the hitbox to set
     */
    public void setHitbox(Circle hitbox){
        this.hitbox = hitbox;
    }

    /**
     * @return the texture
     */
    public Texture getTexture(){
        return new Texture(Gdx.files.internal(texture));
    }

    /**
     * @param texture the texture to set
     */
    public void setTexture(String texture){
        this.texture = texture;
    }
    
}
