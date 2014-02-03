/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.punchables.rainbowdad.utils;

/**
 *
 * @author DrShmoogle
 */
public class Tags {
    
    public static enum Elements {
        FIRE,
        ICE;
    }
    
    public static enum DungeonRoomSize {
        TINY(5, 10, 7, 3),
        SMALL(5, 20, 12, 5),
        NORMAL(10, 30, 20, 10),
        LARGE(15, 50, 35, 15),
        HUGE(15, 100, 50, 35);
        
        private int minRoomSize, maxRoomSize, avgRoomSize, stdDev;
        
        private DungeonRoomSize(int minRoom, int maxRoom, int avgRoom, int stdDev){
            this.minRoomSize = minRoom;
            this.maxRoomSize = maxRoom;
            this.avgRoomSize = avgRoom;
            this.stdDev = stdDev;
        }

        /**
         * @return the minRoomSize
         */
        public int getMinRoomSize(){
            return minRoomSize;
        }

        /**
         * @return the maxRoomSize
         */
        public int getMaxRoomSize(){
            return maxRoomSize;
        }

        /**
         * @return the avgRoomSize
         */
        public int getAvgRoomSize(){
            return avgRoomSize;
        }

        /**
         * @return the stdDev
         */
        public int getStdDev(){
            return stdDev;
        }
        
    }
    
    public static enum Features {
        
    }
    
    
}
