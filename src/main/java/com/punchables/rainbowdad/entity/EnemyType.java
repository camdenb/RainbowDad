/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.punchables.rainbowdad.entity;

/**
 *
 * @author DrShmoogle
 */
public enum EnemyType {
    SLIME("enemies/slime.png");
    
    private String textureString;
    
    EnemyType(String texture){
        this.textureString = texture;
    }

    public String getTextureString(){
        return textureString;
    }
    
}
