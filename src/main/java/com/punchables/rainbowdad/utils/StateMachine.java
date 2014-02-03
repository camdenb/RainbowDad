/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.punchables.rainbowdad.utils;

import com.punchables.rainbowdad.entity.State;

/**
 *
 * @author DrShmoogle
 */
public class StateMachine {
    
    private State currentState, previousState;
    
    public StateMachine(State initialState) {
        currentState = initialState;
        previousState = initialState;
    }
    
    public State getState(){
        return currentState;
    }

    public void setState(State state){
        previousState = currentState;
        this.currentState = state;
    }

    public State getPreviousState(){
        return previousState;
    }

    
    
    
    
}
