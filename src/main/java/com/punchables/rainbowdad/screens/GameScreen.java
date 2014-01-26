/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.punchables.rainbowdad.screens;

import com.punchables.rainbowdad.utils.Art;
import com.punchables.rainbowdad.map.MapTile;
import com.punchables.rainbowdad.utils.Coord;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.punchables.rainbowdad.entity.Enemy;
import com.punchables.rainbowdad.entity.EnemyFactory;
import com.punchables.rainbowdad.entity.Player;
import com.punchables.rainbowdad.RainbowDad;
import com.punchables.rainbowdad.map.DungeonGen;
import com.punchables.rainbowdad.map.TileType;
import com.punchables.rainbowdad.utils.Collider;
import com.punchables.rainbowdad.utils.DebugDrawer;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.toDegrees;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author DrShmoogle
 */
public class GameScreen implements Screen, InputProcessor {
    
    final RainbowDad game;
    
    public FPSLogger fpsLogger = new FPSLogger();
    public OrthographicCamera camera;
    public BitmapFont font;
    public static DebugDrawer debugDraw = new DebugDrawer();

    public Player player = new Player(100, 100);
    public Rectangle playerRec = new Rectangle();
    public EnemyFactory enemyFac = new EnemyFactory();
    public Art artLoader;
    
    public Timer attackTimer = new Timer();
    public float attackLength = .25f;
    
    boolean playerCol = false;
    
    public HashSet<Integer> keysDown = new HashSet<>();
    
    private boolean angleException = false;
    private double attackPrecision = PI / 10;
    
    public static int tileSize = 64; //pixels
    
    DungeonGen dungeonGen;
    private int dungeonWidth = 300;
    private int dungeonHeight = 300;
   

    public GameScreen(final RainbowDad gam){
        Gdx.app.log("LOG", "Creating GameScreen.java");
        this.game = gam;
        camera = new OrthographicCamera();
        //ZOOM
        camera.zoom = 4;
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        player.setPos(new Vector2(100, 100).scl(tileSize));
        //sprite = new Sprite(sprite.getTexture());
        artLoader = new Art();
        artLoader.load();
        Gdx.input.setInputProcessor(this);
        
        dungeonGen = new DungeonGen(dungeonHeight, dungeonWidth);
        
    }

    public void update(float delta){
        
        delta *= .8f;
       
        //System.out.println(toDegrees(Math.atan2(player.getPos().x - 0, player.getPos().y - 0)));
        //UPDATING
        playerRec.setPosition(player.getPos().x, player.getPos().y);
        
        player.update(delta, dungeonGen.getMap());
        
        camera.position.set(player.getPos().x + player.getHitbox().radius, 
                                player.getPos().y + player.getHitbox().radius, 0);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        debugDraw.setProjectionMatrix(camera.combined);
        
        for(Enemy ene : enemyFac.getEnemyList()){
            ene.update(delta);
            double playerAngleToEnemy = Math.atan2(player.getPos().y - ene.getPos().y, player.getPos().x - ene.getPos().x);
            if(playerAngleToEnemy < 0){
                //convert to actual radians
                playerAngleToEnemy = (2*PI) + playerAngleToEnemy;
            }
            //System.out.println(playerAngleToEnemy);
            
            //Utils.isBetween(playerAngleToEnemy, player.getAttackAngle())
            //(playerAngleToEnemy > player.getAttackAngle().x || playerAngleToEnemy < player.getAttackAngle().y)
            if(player.isAttacking()){
                double angle1 = player.getAttackAngle() + attackPrecision;
                double angle2 = player.getAttackAngle() - attackPrecision;
                if(angle2 < 0){
                    angle2 = (2*PI) + angle2;
                    angleException = true;
                } else {
                    angleException = false;
                }
                //System.out.println(angle1 + ", " + angle2);
                //System.out.println(player.getAttackAngle());
//                if(Utils.isBetween(playerAngleToEnemy, angle2, angle1)){
                if((playerAngleToEnemy > angle2 && playerAngleToEnemy < angle1) || 
                        (angleException && playerAngleToEnemy > angle2 || playerAngleToEnemy < angle1)){
                    if(Intersector.overlaps(ene.getHitbox(), player.getHitbox())){
                        System.out.println("ENEMY KILLED");
                        //destroy enemy
                        enemyFac.getEnemyList().removeValue(ene, true);
                    }
                }

            }
        }
    }
    
    public void draw(){
        Gdx.gl.glClearColor(.9f, .9f, .9f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        
        
        game.batch.begin();
        //game.batch.draw(artLoader.getAssetByID(2), 0, 0);
        //drawing tiles
        ConcurrentHashMap<Coord, MapTile> dungeonMap = dungeonGen.getMap();
//        for(Entry<Coord, MapTile> entry : dungeonMap.entrySet()){
//            Coord coord = entry.getKey();
//            MapTile tile = entry.getValue();
            
        if(!dungeonMap.isEmpty()){
            for(int x = dungeonWidth - 1, i = 0; x >= 0; x--){
                for(int y = dungeonHeight - 1; y >= 0; y--){
                    MapTile tile = dungeonMap.get(new Coord(x, y));
                    
//                    if(tile.get().isOverlay()){
//                        continue;
//                    }
                    
                    if((inCameraFrustum(tile.getPos().getX() * tileSize, 
                                tile.getPos().getY() * tileSize, 100))){
                        
                        game.batch.draw(dungeonGen.getTileTexture(tile),
                                tile.getPos().getX() * tileSize, tile.getPos().getY() * tileSize);

                    }
                }
                
            }
        }
        
        game.batch.draw(player.getTexture(), player.getPos().x, player.getPos().y);
        
        
        
        //background
        
        //playerRec.draw(game.batch);     
        for(Enemy ene : enemyFac.getEnemyList()){
            game.batch.draw(artLoader.getAssetByID(1), ene.getPos().x, ene.getPos().y);
        }

        
        game.batch.end();
        
        //shaper.setProjectionMatrix(camera.combined);
        debugDraw.draw();
    }
    
    public void render(float delta){
        fpsLogger.log();
        
        handleInput();
        
        update(delta);
        
        draw();
        
    }

    public Rectangle getTileRect(Coord tileCoord){
        //CHANGE TO RECT LATER
        return new Rectangle(tileCoord.getX() * tileSize, tileCoord.getY() * tileSize, tileSize, tileSize);
        //return new Circle(tileCoord.getX(), tileCoord.getY(), tileSize);
    }
    
    public boolean checkPlayerCollision(MapTile tile, Coord tilePos){
        //Intersector intersector = new Intersector();
        
        return Intersector.overlaps(player.getHitbox(), getTileRect(tilePos));
    }
    
    public boolean inCameraFrustum(float x, float y, int dist){
        boolean inFrustum = false;
        inFrustum = (camera.frustum.pointInFrustum(new Vector3(x + dist, y + dist, 0)) ||
                (camera.frustum.pointInFrustum(new Vector3(x + dist, y, 0))) ||
                (camera.frustum.pointInFrustum(new Vector3(x, y + dist, 0))) ||
                (camera.frustum.pointInFrustum(new Vector3(x - dist, y - dist, 0)) ||
                (camera.frustum.pointInFrustum(new Vector3(x - dist, y, 0)) ||
                (camera.frustum.pointInFrustum(new Vector3(x, y - dist, 0))))));
        return inFrustum;
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

    public void handleInput(){
        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            player.move(1, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            player.move(-1, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            player.move(0, 1);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)){
            player.move(0, -1);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT_BRACKET)){
            camera.zoom += .1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT_BRACKET)){
            camera.zoom -= .1;
        }
    }
    
    @Override
    public boolean keyDown(int keycode){
        if(keycode == Keys.C){
            Gdx.app.log("Player Collision", "" + playerCol);
            //Gdx.app.log("Player Position", "" + player.getPos());
        }
        if(keycode == Keys.ESCAPE){
            Gdx.app.exit();
        }
        if(keycode == Keys.E){
            enemyFac.spawnEnemy(200, 200);
            //System.out.println("ASdasd");
        }
        if(keycode == Keys.O){
            player.setPos(new Vector2());
            //System.out.println("ASdasd");
        }
        if(keycode == Keys.G){
            dungeonGen.generateDungeon();
            //dungeonGen.printDungeon();
            //mapRenderer = new OrthogonalTiledMapRenderer(map, 4f);
            //mapRender = true;
            //System.out.println("ASdasd");
        }
        if(keycode == Keys.UP){
            keysDown.add(Keys.UP);
            player.setAttacking(true);
            player.setTexture("up64.png");
            player.setAttackAngle(3*PI/2);
            attackTimer.scheduleTask(new Timer.Task() {

                @Override
                public void run(){
                    keyUp(Keys.UP);
                }
            }, attackLength);
        }
        if(keycode == Keys.DOWN){
            keysDown.add(Keys.DOWN);
            player.setAttacking(true);
            player.setTexture("down64.png");
            player.setAttackAngle(PI/2);
            attackTimer.scheduleTask(new Timer.Task() {

                @Override
                public void run(){
                    keyUp(Keys.DOWN);
                }
            }, attackLength);
        } 
        if(keycode == Keys.LEFT){
            keysDown.add(Keys.LEFT);
            player.setAttacking(true);
            player.setTexture("left64.png");
            player.setAttackAngle(0);
            attackTimer.scheduleTask(new Timer.Task() {

                @Override
                public void run(){
                    keyUp(Keys.LEFT);
                }
            }, attackLength);
        } 
        if(keycode == Keys.RIGHT){
            keysDown.add(Keys.RIGHT);
            player.setAttacking(true);
            player.setTexture("right64.png");
            player.setAttackAngle(PI);
            attackTimer.scheduleTask(new Timer.Task() {

                @Override
                public void run(){
                    keyUp(Keys.RIGHT);
                }
            }, attackLength);
            
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode){
        if(keycode == Keys.UP){
            keysDown.remove(Keys.UP);
            System.out.println("UP RELEASED!");
        }
        if(keycode == Keys.DOWN){
            keysDown.remove(Keys.DOWN);
        }
        if(keycode == Keys.LEFT){
            keysDown.remove(Keys.LEFT);
        }
        if(keycode == Keys.RIGHT){
            keysDown.remove(Keys.RIGHT);
        }
        if(!keysDown.contains(Keys.UP) &&
                !keysDown.contains(Keys.DOWN) &&
                !keysDown.contains(Keys.LEFT) &&
                !keysDown.contains(Keys.RIGHT)){
            //not attacking
            player.setAttacking(false);
            player.setTexture(player.getDefaultTexture());
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character){
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button){
        return false;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button){
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer){
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY){
        return false;
    }

    @Override
    public boolean scrolled(int amount){
        return false;
    }
    
}
