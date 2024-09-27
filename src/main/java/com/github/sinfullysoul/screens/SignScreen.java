package com.github.sinfullysoul.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.github.puzzle.game.ui.font.CosmicReachFont;
import com.github.puzzle.game.ui.screens.BasePuzzleScreen;
import com.github.sinfullysoul.block_entities.SignBlockEntity;
import finalforeach.cosmicreach.Threads;
import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.items.containers.SlotContainer;
import finalforeach.cosmicreach.ui.UI;
import finalforeach.cosmicreach.ui.widgets.ItemSlotWidget;
import finalforeach.cosmicreach.world.Zone;

import static com.github.sinfullysoul.CosmicSigns.setDisableKeyboardInput;

public class SignScreen extends BasePuzzleScreen {

    static TextField.TextFieldStyle style;
    static Label.LabelStyle styleTextSize;
    static final String baseText = "line ";
    static Drawable buttonRight;
    static Drawable buttonLeft;


    public SignBlockEntity entity;
    private String[] texts;

    public SignScreen(){

    }

    public SignScreen(SignBlockEntity entity) {
        Threads.runOnMainThread(() -> {
            if(style.background == null) {
                style.background = new NinePatchDrawable(UI.containerBackground9Patch);
                buttonRight = new NinePatchDrawable(UI.containerBackground9Patch);
                buttonLeft = new NinePatchDrawable(UI.containerBackground9Patch);
            }
        });

        this.entity = entity;
        this.texts = this.entity.getText();
        Actor background = new Image(UI.containerBackground9Patch);
        Stack stack = new Stack();
        Table mainPanel = new Table();
        Table middlePanel = new Table();
        Table leftPanel = new Table();
        Table leftButtons = new Table();

        TextField line1 = new TextField((texts[0]), new TextField.TextFieldStyle(style));
        line1.setMaxLength(24);
        line1.setAlignment(Align.center);
        line1.setMessageText(baseText + 1);
        line1.addListener(listener -> {
            this.texts[0] = line1.getText();
            return false;
        });
        TextField line2 = new TextField((texts[1]), new TextField.TextFieldStyle(style));
        line2.setMaxLength(24);
        line2.setAlignment(Align.center);
        line2.setMessageText(baseText + 2);
        line2.addListener(listener -> {
            this.texts[1] = line2.getText();
            return false;
        });
        TextField line3 = new TextField((texts[2]), new TextField.TextFieldStyle(style));
        line3.setMaxLength(24);
        line3.setAlignment(Align.center);
        line3.setMessageText(baseText + 3);
        line3.addListener(listener -> {
            this.texts[2] = line3.getText();
            return false;
        });

        Label fontSizeLabel = new Label("" + this.entity.textSize, styleTextSize);
        fontSizeLabel.setAlignment(Align.center);
        fontSizeLabel.addAction(new Action() {
            @Override
            public boolean act(float v) {
                fontSizeLabel.setText(""+(int)entity.textSize);
                return false;
            }
        });
        ImageButton subButton = new ImageButton(buttonLeft);
        subButton.addListener(new ClickListener() {
            final SignBlockEntity entity = SignScreen.this.entity;
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(entity.textSize > 6) entity.textSize -= 1;
            }
        });
        ImageButton addButton = new ImageButton(buttonRight);
        addButton.addListener(new ClickListener() {
            final SignBlockEntity entity = SignScreen.this.entity;
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(entity.textSize < 14) entity.textSize += 1;
            }
        });

        leftButtons.add(subButton).height(10).width(10);
        leftButtons.add(addButton).height(10).width(10).padLeft(3);

        leftPanel.add(fontSizeLabel).expandX().padBottom(3).row();
        leftPanel.add(leftButtons);

        middlePanel.add(line1).height(30).row();
        middlePanel.add(line2).height(30).padTop(5).row();
        middlePanel.add(line3).height(30).padTop(5);

        mainPanel.add(leftPanel).expand().fill().center();
        mainPanel.add(middlePanel).width(200).center();
        mainPanel.add(genColorButtons()).expand().center();
        mainPanel.setFillParent(true);

        ((Actor)background).setWidth(320 + 8.0F);
        ((Actor)background).setHeight(300 + 8.0F);
        stack.add(background);
        stack.add(mainPanel);
        this.slotActor = stack;
        this.slotWidgets = new ItemSlotWidget[0];
    }

    private Table genColorButtons() {
        Table table = new Table();
        Color[] colors = new Color[] {Color.BLACK, Color.WHITE, Color.RED, Color.BLUE, Color.CYAN, Color.GOLD, Color.GREEN, Color.MAGENTA, Color.PURPLE, Color.ORANGE};
        for (int i=0; i < colors.length; i++) {
            Color color = colors[i];
            Pixmap pm = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
            pm.setColor(color);
            pm.fill();

            ImageButton button = new ImageButton(new TextureRegionDrawable(new Texture(pm)));
            button.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    entity.fontcolor = color;
                }
            });
            if(i > 0 && i % 4 == 0) table.row();
            table.add(button).height(10).width(10).pad(2);
            pm.dispose();
        }
        return table;
    }

    @Override
    public void onRemove() {
        super.onRemove();
        this.entity.runTexture = true;
        setDisableKeyboardInput(false);
    }

    @Override
    public void OnOpen(Player player, Zone zone, BlockEntity blockEntity) {
        SignScreen screen = new SignScreen((SignBlockEntity) blockEntity);
        UI.addOpenBaseItemScreen(new SlotContainer(0), screen);
        setDisableKeyboardInput(true);
    }

    static {
        Threads.runOnMainThread(() -> {
            style = new TextField.TextFieldStyle();
            style.font = CosmicReachFont.createCosmicReachFont();
            style.fontColor = Color.WHITE;
            style.font.getData().capHeight = -12f;
            BitmapFont fontsize = CosmicReachFont.createCosmicReachFont();
            styleTextSize = new Label.LabelStyle(fontsize, Color.WHITE);
        });
    }
}
