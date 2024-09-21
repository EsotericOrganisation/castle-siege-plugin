package net.slqmy.castle_siege_plugin.npc;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerPlayer;
import net.slqmy.castle_siege_plugin.game.Game;
import net.slqmy.castle_siege_plugin.npc.shop.shops.BlacksmithShop;
import net.slqmy.castle_siege_plugin.util.NMSUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public final class NPCBehaviour {
    private final NPC npc;
    private final Game game;

    public NPCBehaviour(NPC npc, Game game) {
        this.npc = npc;
        this.game = game;
    }

    public void onTick() {
        lookAtPlayer();
    }

    public void blacksmithOnInteract(Player player) {
        new BlacksmithShop().show(player);
    }

    private void lookAtPlayer() {
        ServerPlayer handle = npc.getHandle();
        ServerPlayer closestPlayer = getClosestPlayer(npc);

        if (closestPlayer != null) {
            handle.lookAt(EntityAnchorArgument.Anchor.EYES, closestPlayer, EntityAnchorArgument.Anchor.EYES);
        } else {
            Location initialLoc = npc.getData().getLocation();
            npc.setYawAndPitch(initialLoc.getYaw(), initialLoc.getPitch());
        }

        npc.sendRotationPacketsToPlayers();
    }

    @Nullable
    private ServerPlayer getClosestPlayer(NPC npc) {
        Player bukkitPlayer = npc.getHandle().getBukkitEntity();

        List<Player> nearbyPlayers = bukkitPlayer
            .getNearbyEntities(8.0, 4.0, 8.0).stream()
            .filter(entity -> entity instanceof Player)
            .map(entity -> (Player) entity)
            .toList();

        return nearbyPlayers.stream()
            .min((player1, player2) -> {
                double distance1 = player1.getLocation().distanceSquared(bukkitPlayer.getLocation());
                double distance2 = player2.getLocation().distanceSquared(bukkitPlayer.getLocation());

                return Double.compare(distance1, distance2);
            })
            .map(NMSUtil::toNMSPlayer)
            .orElse(null);
    }
}
