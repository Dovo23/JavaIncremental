package com.example.incrementalgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.example.incrementalgame.assets.Assets;
import com.example.incrementalgame.config.GameConfig;
import com.example.incrementalgame.entities.Building;
import com.example.incrementalgame.entities.GameButton;
import com.example.incrementalgame.entities.Player;

public class IncrementalGame extends ApplicationAdapter {
    private Assets assets;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private Player player;
    private GameButton buyMinerButton, buyBakeryButton, buyFactoryButton, prestigeButton;
    private Building miner, bakery, factory;

    public static int gold = 4000;
    private float goldAccumulator = 0;
    private int prestigeLevel = 0;
    private double nextPrestigeRequirement = 5000;

    @Override
    public void create() {
       assets = new Assets();
       batch = new SpriteBatch();
       camera = new OrthographicCamera();
       viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);

       player = new Player(400 - 32, 20, 64, 64);
       miner = new Building("Miner", 10, 1);
       bakery = new Building("Bakery", 100, 10);
       factory = new Building("Factory", 500, 100);

       buyMinerButton = new GameButton(650, 360, GameConfig.BUTTON_WIDTH, GameConfig.BUTTON_HEIGHT, "Buy Miner " + miner.getIncomePerSecondBase() + " gold/s");
       buyBakeryButton = new GameButton(650, 290, GameConfig.BUTTON_WIDTH, GameConfig.BUTTON_HEIGHT, "Buy Bakery " + bakery.getIncomePerSecondBase() + " gold/s");
       buyFactoryButton = new GameButton(650, 220, GameConfig.BUTTON_WIDTH, GameConfig.BUTTON_HEIGHT, "Buy Factory " + factory.getIncomePerSecondBase() + " gold/s");
       prestigeButton = new GameButton(650, 150, GameConfig.BUTTON_WIDTH, GameConfig.BUTTON_HEIGHT, "Prestige");
    }


    @SuppressWarnings("static-access")
    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        handleInput();

        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(assets.groundTexture, 0, 0, GameConfig.WORLD_WIDTH, 50);
        batch.draw(assets.playerTexture, player.getBounds().x, player.getBounds().y, player.getBounds().width, player.getBounds().height);

        buyMinerButton.draw(batch, assets.font, assets.buttonTexture);
        buyBakeryButton.draw(batch, assets.font, assets.buttonTexture);
        buyFactoryButton.draw(batch, assets.font, assets.buttonTexture);

        //prestige button appears if requirement met
        if (gold >= nextPrestigeRequirement) {
            prestigeButton.draw(batch, assets.font, assets.buttonTexture);
        }

        float totalGoldPerSecond = miner.getIncomePerSecond() + bakery.getIncomePerSecond() + factory.getIncomePerSecond();
        assets.font.draw(batch, "Gold: " + gold, 10, GameConfig.WORLD_HEIGHT - 20);
        assets.font.draw(batch, totalGoldPerSecond + " gold/s", 10, GameConfig.WORLD_HEIGHT - 40);
        assets.font.draw(batch, "Prestige Level: " + prestigeLevel, 10, GameConfig.WORLD_HEIGHT - 60);
        assets.font.draw(batch, "Required gold till next prestige: " + (int) nextPrestigeRequirement, 10, GameConfig.WORLD_HEIGHT - 80);

        assets.font.draw(batch, "Miner Cost: " + miner.getCost(), buyMinerButton.getBounds().x, buyMinerButton.getBounds().y + buyMinerButton.getBounds().height + 20);
        assets.font.draw(batch, "Bakery Cost: " + bakery.getCost(), buyBakeryButton.getBounds().x, buyBakeryButton.getBounds().y + buyBakeryButton.getBounds().height + 20);
        assets.font.draw(batch, "Factory Cost: " + factory.getCost(), buyFactoryButton.getBounds().x, buyFactoryButton.getBounds().y + buyFactoryButton.getBounds().height + 20);
    
        batch.end();

        updateIncome(Gdx.graphics.getDeltaTime());
    }

   private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            float x = touchPos.x;
            float y = touchPos.y;
            if (buyMinerButton.getBounds().contains(x, y)) {
                miner.buy();
            } else if (buyBakeryButton.getBounds().contains(x, y)) {
                bakery.buy();
            } else if (buyFactoryButton.getBounds().contains(x, y)) {
                factory.buy();
            } else if (prestigeButton.getBounds().contains(x, y) && gold >= nextPrestigeRequirement) {
                performPrestige();
            }
        }
    }

    private void updateButtonLabels() {
        buyMinerButton.setText("Buy Miner " + miner.getIncomePerSecondBase() + " gold/s");
        buyBakeryButton.setText("Buy Bakery " + bakery.getIncomePerSecondBase() + " gold/s");
        buyFactoryButton.setText("Buy Factory " + factory.getIncomePerSecondBase() + " gold/s");
    }

    private void performPrestige() {
    gold = 100;
    miner.resetWithMultiplier();
    bakery.resetWithMultiplier();
    factory.resetWithMultiplier();
    prestigeLevel++;
    nextPrestigeRequirement *= 2; // Double the requirement for the next prestige
    updateButtonLabels(); //used to update labels
}
    
    private void updateIncome(float delta) {
        goldAccumulator += delta;
        if (goldAccumulator >= 1) {
            gold += miner.getIncomePerSecond() + bakery.getIncomePerSecond() + factory.getIncomePerSecond();
            goldAccumulator -= 1; // Reset accumulator
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(GameConfig.WORLD_WIDTH / 2, GameConfig.WORLD_HEIGHT / 2, 0);
        camera.update();
    }

    @Override
    public void dispose() {
        assets.dispose();
        batch.dispose();
    }
}
