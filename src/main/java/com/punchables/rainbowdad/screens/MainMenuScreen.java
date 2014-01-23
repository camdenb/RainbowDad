/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.punchables.rainbowdad.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.punchables.rainbowdad.RainbowDad;

/**
 *
 * @author DrShmoogle
 */
public class MainMenuScreen implements Screen {
    
    final RainbowDad game;
    OrthographicCamera camera;
		
    public MainMenuScreen(final RainbowDad gam){
        Gdx.app.log("LOG", "Creating MainMenuScreen.java");
        
        game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 960, 800);
        
        
        
    }

    public void render(float delta){
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Welcome to Rainbow Dad! ", 100, 150);
        game.font.draw(game.batch, "Click anywhere to begin!", 100, 100);
        game.batch.end();

        if(Gdx.input.isTouched()){
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    public void resize(int width, int height){
    }

    public void show(){
    }

    public void hide(){
    }

    public void pause(){
    }

    public void resume(){
    }

    public void dispose(){
    }
    
}
