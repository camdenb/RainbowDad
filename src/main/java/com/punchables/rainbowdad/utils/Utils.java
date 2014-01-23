/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.punchables.rainbowdad.utils;

import com.badlogic.gdx.math.Vector2;
import java.awt.Color;
import java.util.Random;

/**
 *
 * @author DrShmoogle
 */
public class Utils{
    
    public Utils(){}
    
    public static double randomf(double min, double max){
        return (min + Math.random() * ((max - min + 1)));
    }

    public static Color randomColor(){
        return new Color(randomi(0, 255), randomi(0, 255), randomi(0, 255));
    }
    
    public static Color randomColor(double saturation){
        return new Color((int) clamp(randomi(0, 255) * saturation, 0, 255), (int) clamp(randomi(0, 255) * saturation, 0, 255), (int) clamp(randomi(0, 255) * saturation, 0, 255));
    }
    
    public static Color randomColor(int min, int max){
        return new Color(randomi(min, max), randomi(min, max), randomi(min, max));
    }

    public static double randoma(double[] arr){
        double max = arr.length - 1;
        double min = 0;
        return (min + Math.random() * ((max - min + 1)));
    }

    public static double randomf(Vector2 vect){
        //returns value between points of vector
        double max = vect.y;
        double min = vect.x;
        return (min + Math.random() * ((max - min + 1)));
    }

    public static int randomi(double min, double max){
        return (int) (min + Math.random() * ((max - min + 1)));
    }
    
    public static int randomFlip(){
        return randomi(0,1) * 2 - 1;
    }
    
    public static boolean randomBool(){
        int num = randomi(0,1) * 2 - 1;
        if(num == 1){
        	return true;
        } else {
        	return false;
        }
    }
    
    
    public static double clampMax(double num, double max){
        if(num > max){
            return max;
        } else {
            return num;
        }
    }
    
    public static double clampMin(double num, double min){
        if(num < min){
            return min;
        } else {
            return num;
        }
    }
    
    public static double clamp(double num, double min, double max){
        if(num > max){
            return max;
        } else if(num < min){
            return min;
        } else {
            return num;
        }
    }
 
    public static boolean isBetween(double num, double min, double max){
        if(num >= min && num <= max){
            return true;
        } else {
            return false;
        }
        
    }
    
    public static boolean isBetween(double num, Vector2 bounds){
        if(num >= bounds.x && num <= bounds.y){
            return true;
        } else {
            return false;
        }
        
    }
}
