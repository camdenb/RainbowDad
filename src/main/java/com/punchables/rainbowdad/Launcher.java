package com.punchables.rainbowdad;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

/**
 * Hello world!
 *
 */
public class Launcher 
{
    public static void main(String[] args) {
        // create the listener that will receive the application events
        ApplicationListener listener = new RainbowDad();
        // define the window's title
        String title = "Rainbow Dad!";

        // define the window's size
        int width = 960, height = 800;

        // whether to use OpenGL ES 2.0
        boolean useOpenGLES2 = true;

        // create the game
        new LwjglApplication(listener, title, width, height, useOpenGLES2);

    }
}
