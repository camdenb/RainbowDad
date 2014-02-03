/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package AStar;

import com.punchables.rainbowdad.map.MapTile;
import com.punchables.rainbowdad.map.TileType;
import com.punchables.rainbowdad.screens.GameScreen;
import com.punchables.rainbowdad.utils.Coord;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author DrShmoogle
 */
public class AStarMain {
    
    private ArrayList<Node> openNodes = new ArrayList<>();
    private ArrayList<Node> closedNodes = new ArrayList<>();
    private ConcurrentHashMap<Coord, MapTile> map = new ConcurrentHashMap<Coord, MapTile>();
    
    private int orthoCost = 10;
    private int diagCost = 10;
    
    private Node currentTarget;
    
    public AStarMain(ConcurrentHashMap<Coord, MapTile> map){
        this.map = map;
    }
    
    public ArrayList<Node> calcNewPathFromCoord(Coord startNode, Coord targetNode){
        System.out.println("A*: Calling calcNewPath...");
        return calcNewPath(new Node(startNode.div(GameScreen.tileSize)), new Node(targetNode.div(GameScreen.tileSize)));
    }
    
    public ArrayList<Node> calcNewPath(Node startNode, Node targetNode){
        System.out.println("A*: Starting path gen...");
        long startTime = System.currentTimeMillis();
        
        currentTarget = targetNode;
        openNodes.add(startNode);
        Node currentNode = startNode;
        ArrayList<Node> newOpenNodes = new ArrayList<>();
        
        int passNumber = 1;
        
        while(!openNodes.isEmpty()){
            ArrayList<Node> newAdjNodes = addAdjacentNodes(currentNode);
            
            for(Node adjNode : newAdjNodes){
                if(adjNode.equals(targetNode)){
                    break;
                } else {
                    System.out.println(adjNode + " != " + targetNode);
                }
                if(openNodes.contains(adjNode)){
                    //Node newNode = new Node(adjNode.getPos(), currentNode.getParent());
                    //get the movement cost going from the current node to the adj node
                    int newGValue = (isNodeOrthoToParent(adjNode)) ? currentNode.getG() + orthoCost : currentNode.getG() + diagCost;
                    if(adjNode.getG() > newGValue) {
                        //checking if this new path is better than the old one
                        adjNode.setParent(currentNode);
                        calcAndAssignValues(adjNode);
                    }
                } else {
                    newOpenNodes.add(adjNode);
                }
            }
            openNodes.remove(currentNode);
            closedNodes.add(currentNode);
            for(Node node : newOpenNodes){
                calcAndAssignValues(node);
                openNodes.add(node);
                newOpenNodes.remove(node);
            }
            //System.out.println("A*: Pass #" + passNumber);
            currentNode = getLowestFValue(openNodes);
            passNumber++;
        }
        System.out.println("A*: Path generation complete.");
        System.out.println("A*: Saving path...");
        //save path
        Node currentSavingNode = targetNode;
        ArrayList<Node> path = new ArrayList<>();
        while(!currentSavingNode.equals(startNode)){
            Node currentParent = currentSavingNode.getParent();
            path.add(currentParent);
            currentSavingNode = currentParent;
        }
        System.out.println("A*: Path saving complete.");
        long now = System.currentTimeMillis();
        System.out.println("A*: Total elapsed time: " + (now - startTime) + "ms.");
        return path;
        
    }
    
    public ArrayList<Node> addAdjacentNodes(Node node){
        ArrayList<Node> adjNodes = new ArrayList<>();
        for(int x = node.getPos().getWest(1).x; x <= node.getPos().getEast(1).x; x++){
            for(int y = node.getPos().getSouth(1).y; y <= node.getPos().getNorth(1).y; y++){
                Node currentNode = new Node(new Coord(x,y), node.getParent());
                
                //System.out.println(map.get(new Coord(10496,11008)));
                if(!map.get(currentNode.getPos()).get().isSolid() &&
                        !openNodes.contains(new Node(new Coord(x,y), node))){
                    //map.get(currentNode.getPos()).set(TileType.DEBUG);
                    //System.out.println(currentNode);
                    openNodes.add(new Node(new Coord(x,y), node));
                    adjNodes.add(new Node(new Coord(x,y), node));
                }
                
            }
        }
        return adjNodes;
    }
    
    public Node getLowestFValue(ArrayList<Node> list){
        Collections.sort(list, new NodeFValueComparator());
        return list.get(0);
    }
    
    public void calcAndAssignValues(Node node){
        int g_value = calcG(node);
        node.setG(g_value);
        
        int h_value = getEuclideanHeuristic(node);
        node.setH(h_value);
        
        node.setF(h_value + g_value);
        
    }
    
    public int calcG(Node node){
        int g_value;
        if(isNodeOrthoToParent(node)){
            g_value = orthoCost;
        } else {
            g_value = diagCost;
        }
        return g_value;
    }

    
    public int getEuclideanHeuristic(Node node){
        //assuming diag and ortho cost are the same
        int dx = abs(node.getPos().x - currentTarget.getPos().x);
        int dy = abs(node.getPos().y - currentTarget.getPos().y);
        return (int) (orthoCost * sqrt(dx * dx + dy * dy));
    }
    
    public boolean isNodeOrthoToParent(Node node){
        if(node.getPos().getNorth(1) == node.getParent().getPos()){
            return true;
        } 
        if(node.getPos().getSouth(1) == node.getParent().getPos()){
            return true;
        }
        if(node.getPos().getEast(1) == node.getParent().getPos()){
            return true;
        }
        if(node.getPos().getWest(1) == node.getParent().getPos()){
            return true;
        }
        return false;
    }
    
    
}
