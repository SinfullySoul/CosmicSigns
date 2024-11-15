package com.github.sinfullysoul.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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
import com.github.sinfullysoul.blockentities.SignBlockEntity;
import com.github.sinfullysoul.network.packets.SignsEntityPacket;
import finalforeach.cosmicreach.items.screens.BaseItemScreen;
import finalforeach.cosmicreach.networking.client.ClientNetworkManager;
import finalforeach.cosmicreach.ui.UI;
import finalforeach.cosmicreach.ui.widgets.ItemSlotWidget;

import static com.github.sinfullysoul.ClientInitializer.setDisableKeyboardInput;

public class SignScreen extends BaseItemScreen {

    static TextField.TextFieldStyle style;
    static Label.LabelStyle styleTextSize;
    static final String baseText = "line ";
    static Drawable buttonRight;
    static Drawable buttonLeft;

    public SignBlockEntity entity;
    private String[] texts;

    public SignScreen(SignBlockEntity entity) {
        super(null);
        if(style == null) {
            style = new TextField.TextFieldStyle();
            style.font = CosmicReachFont.createCosmicReachFont();
            style.fontColor = Color.WHITE;
            style.font.getData().capHeight = -12f;
            style.background = new NinePatchDrawable(UI.containerBackground9Patch);
            styleTextSize = new Label.LabelStyle(CosmicReachFont.createCosmicReachFont(), Color.WHITE);
            buttonRight = new NinePatchDrawable(UI.containerBackground9Patch);
            buttonLeft = new NinePatchDrawable(UI.containerBackground9Patch);
        }

        this.entity = entity;
        this.texts = this.entity.getText();
        Actor background = new Image(UI.containerBackground9Patch);
        Stack stack = new Stack();
        Table mainPanel = new Table();
        Table middlePanel = new Table();
        Table leftPanel = new Table();
        Table leftButtons = new Table();

        TextField line1 = new TextField((texts[0]), new TextField.TextFieldStyle(style));
        line1.setMaxLength(36);//36 with smallest character is as large as the current sign can hold with smallest font 6
        line1.setAlignment(Align.center);
        line1.setMessageText(baseText + 1);
        line1.addListener(listener -> {
            int currentLength = this.texts[0].length();
            if(currentLength != line1.getText().length()) {
                //do updates only if the lengths arent equal
                if(currentLength < line1.getText().length()){ //if new text size is greater than we need to check if it reaches the max size
                    int addedLength = this.entity.isTextMaxSize(line1.getText());
                    if( addedLength > 0 ) {
                        line1.setText(this.texts[0]); //set to the last working string
                    }
                }
                entity.setTextToLine(0, line1.getText());
            }

            return false;
        });
        TextField line2 = new TextField((texts[1]), new TextField.TextFieldStyle(style));
        line2.setMaxLength(36);
        line2.setAlignment(Align.center);
        line2.setMessageText(baseText + 2);
        line2.addListener(listener -> {
            int currentLength = this.texts[1].length();
            if(currentLength != line2.getText().length()) {
                if (currentLength < line2.getText().length()) {
                    int addedLength = this.entity.isTextMaxSize(line2.getText());
                    if (addedLength > 0) {
                        line2.setText(this.texts[1]);
                    }
                }
                entity.setTextToLine(1, line2.getText());
            }

            return false;
        });
        TextField line3 = new TextField((texts[2]), new TextField.TextFieldStyle(style));
        line3.setMaxLength(36);
        line3.setAlignment(Align.center);
        line3.setMessageText(baseText + 3);
        line3.addListener(listener -> {
            int currentLength = this.texts[2].length();
            if(currentLength != line3.getText().length()) {
                if(currentLength < line3.getText().length()){
                    int addedLength = this.entity.isTextMaxSize(line3.getText());
                    if( addedLength > 0 ) {
                        line3.setText(this.texts[2]);
                    }
                }
                entity.setTextToLine(2, line3.getText());
            }

            return false;
        });

        Label fontSizeLabel = new Label("" + this.entity.getFontSize(), styleTextSize);
        fontSizeLabel.setAlignment(Align.center);
        fontSizeLabel.addAction(new Action() {
            @Override
            public boolean act(float v) {
                fontSizeLabel.setText(""+(int)entity.getFontSize());
                return false;
            }
        });
        ImageButton subButton = new ImageButton(buttonLeft);
        subButton.addListener(new ClickListener() {
            final SignBlockEntity entity = SignScreen.this.entity;
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(entity.getFontSize() > 7) {
                    entity.setFontSize(entity.getFontSize() - 1);
                    entity.runTexture = true;
                }
            }
        });
        ImageButton addButton = new ImageButton(buttonRight);
        addButton.addListener(new ClickListener() {
            final SignBlockEntity entity = SignScreen.this.entity;
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(entity.getFontSize() < 15) {
                    entity.setFontSize(entity.getFontSize() + 1);
                    entity.runTexture = true;
                }
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
                    entity.setTextColor(color.cpy());
                    entity.runTexture = true;
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
        this.entity.runTexture = true;
        setDisableKeyboardInput(false);
        if(ClientNetworkManager.isConnected()) ClientNetworkManager.sendAsClient(new SignsEntityPacket(this.entity));
        super.onRemove();
    }

    @Override
    public void onShow() {
        setDisableKeyboardInput(true);
        super.onShow();
    }
}
