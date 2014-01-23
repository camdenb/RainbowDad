/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.punchables.rainbowdad.entity;

import com.punchables.rainbowdad.utils.Utils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;

/**
 *
 * @author DrShmoogle
 */
public class EnemyFactory{

    private Array<Enemy> enemyList = new Array<>();
    
    public EnemyFactory(){
    }

    public void spawnEnemy(float x, float y){
        //Enemy enemy = new Enemy(x, y, 128, 128);
        
        Enemy enemy = new Enemy(Utils.randomi(100, 800), Utils.randomi(100, 600), 64, 64);
        enemy.setTexture("enemy128.png");
        getEnemyList().add(enemy);
    }

    /**
     * @return the enemyList
     */
    public Array<Enemy> getEnemyList(){
        return enemyList;
    }

    /**
     * @param enemyList the enemyList to set
     */
    public void setEnemyList(Array<Enemy> enemyList){
        this.enemyList = enemyList;
    }

}
