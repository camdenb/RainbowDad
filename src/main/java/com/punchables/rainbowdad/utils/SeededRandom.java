/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.punchables.rainbowdad.utils;

import com.badlogic.gdx.math.Vector2;
import static com.punchables.rainbowdad.utils.Utils.clamp;
import java.awt.Color;
import java.util.Random;

/**
 *
 * @author DrShmoogle
 */
public class SeededRandom {
    /*****************************************************
     * SEEDED RANDOMS
     * 
     *****************************************************/
    
    private long seed;
    private Random rng;
    
    public SeededRandom(long seed){
        this.seed = seed;
        this.rng = new Random(seed);
    }
    
    public Color randomColor(){
        return new Color(randomi(0, 255), randomi(0, 255), randomi(0, 255));
    }
    
    public Color randomColor(double saturation){
        return new Color((int) clamp(randomi(0, 255) * saturation, 0, 255), 
                (int) clamp(randomi(0, 255) * saturation, 0, 255), 
                (int) clamp(randomi(0, 255) * saturation, 0, 255));
    }
    
    public Color randomColor(int min, int max){
        return new Color(randomi(min, max), 
                randomi(min, max), 
                randomi(min, max));
    }

    public double randoma(double[] arr){
        double max = arr.length - 1;
        double min = 0;
        double randomValue = min + (max - min) * rng.nextDouble();
        return randomValue;
    }

    public double randomf(double min, double max){
        double randomValue = min + (max - min) * rng.nextDouble();
        return randomValue;
    }
    
    public double randomf(Vector2 vect){
        //returns value between points of vector
        double max = vect.y;
        double min = vect.x;
        double randomValue = min + (max - min) * rng.nextDouble();
        return randomValue;
    }

    public int randomi(double min, double max){
        return (int) (rng.nextInt((int)(max - min) + 1) + min);
    }
    
    public double randomGaussian(double min, double max, double mean, double stdDev){
        double number = min - 1;
        while(number < min || number > max){
            number = rng.nextGaussian() * stdDev + mean;
        }
        return number;
    }
    
    public int randomFlip(){
        return randomi(0,1) * 2 - 1;
    }
    
    public boolean randomBool(){
        int num = randomi(0,1) * 2 - 1;
        if(num == 1){
        	return true;
        } else {
        	return false;
        }
    }

    /**
     * @return the seed
     */
    public long getSeed(){
        return seed;
    }

    /**
     * @param seed the seed to set
     */
    public void setSeed(long seed){
        this.seed = seed;
    }
}
