package com.github.sinfullysoul.blockentities;

import com.badlogic.gdx.graphics.Color;
import com.github.sinfullysoul.Constants;
import com.github.sinfullysoul.api.ISignBlockEntity;
import com.github.sinfullysoul.mixins.BlockEntityInterface;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.blockentities.BlockEntityCreator;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.networking.packets.blocks.BlockEntityDataPacket;
import finalforeach.cosmicreach.networking.server.ServerSingletons;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.world.Zone;

public class ServerSignBlockEntity extends BlockEntity implements ISignBlockEntity {

    public final static Identifier id = Identifier.of(Constants.MOD_ID, "sign_entity");

    public String[] texts = new String[]{"", "", ""};
    public Color textColor = Color.BLACK.cpy();
    public float textSize = 8f;

    public static void register() {
        BlockEntityCreator.registerBlockEntityCreator(id.toString(), (blockState, zone, x, y, z) -> {
            return new ServerSignBlockEntity(zone, x, y, z);
        });
    }

    public ServerSignBlockEntity(Zone zone, int globalX, int globalY, int globalZ) {
        super(zone, globalX, globalY, globalZ);
    }

    @Override
    public void onInteract(Player player, Zone zone) {
        GameSingletons.openBlockEntityScreen(player, zone, this);
    }

    @Override
    public void read(CRBinDeserializer deserial) {
        super.read(deserial);
        this.texts = deserial.readStringArray("lines");
        this.textSize = deserial.readFloat("textsize", 8f);
        Color.rgba8888ToColor(this.textColor, deserial.readInt("textcolor", Color.rgba8888(Color.BLACK)));
    }

    @Override
    public void write(CRBinSerializer serial) {
        super.write(serial);
        serial.writeStringArray("lines", texts);
        serial.writeFloat("textsize", this.textSize);
        serial.writeInt("textcolor", Color.rgba8888(this.textColor));
    }

    @Override
    public void onLoad() {
        ServerSingletons.SERVER.broadcast(((BlockEntityInterface) this).getZone(), new BlockEntityDataPacket(this));
        super.onLoad();
    }

    @Override
    public void onUnload() {
        super.onUnload();
        this.loaded = false;
    }

    @Override
    public String getBlockEntityId() {
        return id.toString();
    }
}
