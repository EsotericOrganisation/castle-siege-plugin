package net.slqmy.castle_siege_plugin.events.bukkit;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.slqmy.castle_siege_plugin.events.base.AbstractEvent;
import net.slqmy.castle_siege_plugin.game.data.player.TeamPlayer;
import net.slqmy.castle_siege_plugin.game.data.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public final class AsyncChatListener extends AbstractEvent implements ChatRenderer.ViewerUnaware {
    private final static MiniMessage MM = MiniMessage.miniMessage();
    public AsyncChatListener() {
        super();
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (!plugin.getGameManager().hasOngoingGame(event.getPlayer().getWorld()))
            return;

        event.renderer(ChatRenderer.viewerUnaware(this));
    }

    @Override
    public @NotNull Component render(@NotNull Player player, @NotNull Component playerName, @NotNull Component message) {
        TeamPlayer teamPlayer = TeamPlayer.getFrom(player);
        if (teamPlayer == null) return message;

        Team team = teamPlayer.getTeam();
        String teamName = team.getName();
        NamedTextColor teamColor = team.getBaseData().color();

        return Component.text()
            .append(MM.deserialize("<dark_gray>["))
            .append(MM.deserialize(teamName))
            .append(MM.deserialize("<dark_gray>] "))
            .append(playerName.color(teamColor))
            .append(MM.deserialize("<white>: "))
            .append(message).build();
    }
}
