/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package AStar;

import com.punchables.rainbowdad.utils.Coord;
import java.awt.Point;
import java.util.Comparator;

/**
 *
 * @author DrShmoogle
 */
public class Node {
    private Node parent;
    private Coord pos = new Coord();
    private int f_value, g_value, h_value;
    
    public Node(){
    }
    
    public Node(Coord pos){
        this.pos = pos;
    }
    
    public Node(Node parent){
        this.parent = parent;
    }
    
    public Node(Coord pos, Node parent){
        this.pos = pos;
        this.parent = parent;
    }

    /**
     * @return the parent
     */
    public Node getParent(){
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Node parent){
        this.parent = parent;
    }

    /**
     * @return the pos
     */
    public Coord getPos(){
        return pos;
    }

    /**
     * @param pos the pos to set
     */
    public void setPos(Coord pos){
        this.pos = pos;
    }

    /**
     * @return the f_value
     */
    public int getF(){
        return f_value;
    }

    /**
     * @param f_value the f_value to set
     */
    public void setF(int f_value){
        this.f_value = f_value;
    }

    /**
     * @return the g_value
     */
    public int getG(){
        return g_value;
    }

    /**
     * @param g_value the g_value to set
     */
    public void setG(int g_value){
        this.g_value = g_value;
    }

    /**
     * @return the h_value
     */
    public int getH(){
        return h_value;
    }

    /**
     * @param h_value the h_value to set
     */
    public void setH(int h_value){
        this.h_value = h_value;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node n = (Node)obj;
            return (getPos().x == n.getPos().x) && (getPos().y == n.getPos().y) &&
                    getH() == n.getH() && getG() == n.getG() && getF() == n.getF();
        }       
        return false;
        
    }
    
    @Override
    public int hashCode(){
        int hash = 1;
        hash += getPos().x * 17;
        hash += getPos().y * 13;
        hash += getH() * 7;
        hash += getG() * 23;
        hash += getF() * 29;
        return hash;        
    }
    
    public String toString(){
        return "{" + getPos().x + ", " + getPos().y + " | H:" + h_value + " G:" + g_value + " F:" + f_value + "}";
    }
    
}

class NodeFValueComparator implements Comparator<Node> {

    @Override
    public int compare(Node o1, Node o2){
        return o1.getF() - o2.getF();
    }
    
}
