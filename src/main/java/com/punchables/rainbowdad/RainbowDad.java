package com.punchables.rainbowdad;

import com.punchables.rainbowdad.screens.MainMenuScreen;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.awt.Font;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author DrShmoogle
 */
public class RainbowDad extends Game{

    public SpriteBatch batch;
   
    public BitmapFont font;

    @Override
    public void create(){
        Gdx.app.log("LOG", "Creating RainbowDad.java...");
        batch = new SpriteBatch();
        
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        font.setScale(2, 2);
        this.setScreen(new MainMenuScreen(this));
        
        //Gdx.input.setInputProcessor(this);
        
        
        
    }


}
