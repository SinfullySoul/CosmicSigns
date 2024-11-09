package com.github.sinfullysoul.network.packets;

import com.github.sinfullysoul.api.ISignBlockEntity;
import finalforeach.cosmicreach.blockentities.BlockEntity;
import finalforeach.cosmicreach.networking.GamePacket;
import finalforeach.cosmicreach.networking.NetworkIdentity;
import finalforeach.cosmicreach.networking.packets.blocks.BlockEntityDataPacket;
import finalforeach.cosmicreach.networking.server.ServerSingletons;
import finalforeach.cosmicreach.savelib.IByteArray;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.world.Zone;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class SignsEntityPacket extends GamePacket {

    CRBinDeserializer deserial = new CRBinDeserializer();
    BlockEntity entity;
    int x;
    int y;
    int z;

    public SignsEntityPacket(){}

    public SignsEntityPacket(ISignBlockEntity entity){
        this.entity = (BlockEntity) entity;
        this.x = this.entity.getGlobalX();
        this.y = this.entity.getGlobalY();
        this.z = this.entity.getGlobalZ();
    }

    @Override
    public void receive(ByteBuf in) {
        this.x = this.readInt(in);
        this.y = this.readInt(in);
        this.z = this.readInt(in);
        this.deserial.prepareForRead(in.nioBuffer());
    }

    @Override
    public void write() {
        this.writeInt(this.x);
        this.writeInt(this.y);
        this.writeInt(this.z);
        CRBinSerializer serial = new CRBinSerializer();
        this.entity.write(serial);
        IByteArray arr = serial.toByteArray();
        this.writeByteArray(arr);
    }

    @Override
    public void handle(NetworkIdentity identity, ChannelHandlerContext ctx) {
        if (identity.isServer()) {
            Zone zone = identity.getZone();
            BlockEntity entity = zone.getBlockEntity(this.x, this.y, this.z);
            if (entity != null) {
                entity.read(this.deserial);
                ServerSingletons.SERVER.broadcast(zone, new BlockEntityDataPacket(entity));
            }
        }
    }
}
