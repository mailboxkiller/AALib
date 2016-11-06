package xyz.aadev.aalib.common.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class NetworkExample extends NetworkWrapperBase {
    private static NetworkExample instance = new NetworkExample();

    public NetworkExample() {
        super("ModId");
    }

    public static NetworkExample getInstance() {
        return instance;
    }

    public static void registerPackets() {
        // Register Packets here
    }

    public static void sendPacket(Entity player, Packet<?> packet) {
        if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).connection != null) {
            ((EntityPlayerMP) player).connection.sendPacket(packet);
        }
    }

    public static void sendToAll(PacketBase packet) {
        getInstance().network.sendToAll(packet);
    }

    public static void sendTo(PacketBase packet, EntityPlayerMP player) {
        getInstance().network.sendTo(packet, player);
    }

    public static void sendToAllAround(PacketBase packet, NetworkRegistry.TargetPoint point) {
        getInstance().network.sendToAllAround(packet, point);
    }

    public static void sendToDimension(PacketBase packet, int dimensionId) {
        getInstance().network.sendToDimension(packet, dimensionId);
    }

    public static void sendToServer(PacketBase packet) {
        getInstance().network.sendToServer(packet);
    }

    public static void sendToClients(WorldServer worldServer, BlockPos blockPos, PacketBase packet) {
        Chunk chunk = worldServer.getChunkFromBlockCoords(blockPos);
        for (EntityPlayer player : worldServer.playerEntities) {
            if (!(player instanceof EntityPlayerMP))
                continue;

            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            if (worldServer.getPlayerChunkMap().isPlayerWatchingChunk(playerMP, chunk.xPosition, chunk.zPosition)) {
                NetworkExample.sendTo(packet, playerMP);
            }
        }
    }
}
