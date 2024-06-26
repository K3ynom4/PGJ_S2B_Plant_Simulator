package com.pgj.s2bplantsimulator.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.pgj.s2bplantsimulator.S2BPlantSimulator;
import com.pgj.s2bplantsimulator.controller.TileMapHelper;
import com.pgj.s2bplantsimulator.model.Player;

import static com.pgj.s2bplantsimulator.common.constant.GameConstant.PPM;

public class MainGame implements Screen {
    public float stateTime;
    public S2BPlantSimulator game;
    public World world;
    public Player player;
    public TileMapHelper tileMapHelper;
    public TiledMap map = new TmxMapLoader().load("map.tmx");
    public OrthogonalTiledMapRenderer renderer;
    public Box2DDebugRenderer box2DDebugRenderer;
    public OrthographicCamera staticCamera;
    public OrthographicCamera playerCamera;

    public int[] Water = new int[] {0}, Grass = new int[]{1}, Dirt = new int[]{2}, Wood = new int[]{4}; // Lấy index của layer
    public MainGame(S2BPlantSimulator game){
        this.world = new World(new Vector2(0,0), false);
        this.game = game;
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        box2DDebugRenderer.setDrawBodies(false);
        box2DDebugRenderer.setDrawJoints(false);
        this.tileMapHelper = new TileMapHelper(this);
        this.renderer = tileMapHelper.setupMap();

    }
    @Override
    public void show() {
//        staticCamera = new OrthographicCamera(512, 360);
        game.camera = new OrthographicCamera(512, 360);

    }
    public void update(float dt){
        world.step(1/60f, 6, 2);

        Vector3 position = game.camera.position;
        position.x = player.body.getPosition().x * PPM * 10 / 10f;
        position.y = player.body.getPosition().y * PPM * 10 / 10f;
        game.camera.position.set(position);
//        staticCamera.position.set(position);
        if (game.camera.position.x < game.camera.viewportWidth / 2) {
            game.camera.position.x = game.camera.viewportWidth / 2;
        }
        if (game.camera.position.x > map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class) - game.camera.viewportWidth / 2) {
            game.camera.position.x = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class) - game.camera.viewportWidth / 2;
        }
        if (game.camera.position.y < game.camera.viewportHeight / 2) {
            game.camera.position.y = game.camera.viewportHeight / 2;
        }
        if (game.camera.position.y > map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class) - game.camera.viewportHeight / 2) {
            game.camera.position.y = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class) - game.camera.viewportHeight / 2;
        }
        player.update(dt);
        game.camera.update();
//        staticCamera.update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.setView(game.camera);
        // render map theo layer index
        renderer.render(Water);
        renderer.render(Grass);
        renderer.render(Dirt);
        box2DDebugRenderer.render(world, game.camera.combined.scl(PPM));
//        box2DDebugRenderer.render(world, staticCamera.combined.scl(PPM));

        stateTime += delta;

//        game.batch.setProjectionMatrix(staticCamera.combined);

        game.batch.begin();
        game.batch.setProjectionMatrix(game.camera.combined);
        this.update(delta);
        player.draw(game.batch);
        game.batch.end();
        renderer.render(Wood);


    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        box2DDebugRenderer.dispose();
    }
}
