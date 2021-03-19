package broccolai.tickets.bukkit.service;

import broccolai.tickets.api.model.user.PlayerSoul;
import broccolai.tickets.bukkit.model.BukkitPlayerSoul;
import broccolai.tickets.core.service.user.SimpleUserService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import net.kyori.adventure.platform.AudienceProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.Collection;
import java.util.UUID;

@Singleton
public final class BukkitUserService extends SimpleUserService {

    @Inject
    public BukkitUserService(final @NonNull AudienceProvider audienceProvider) {
        super(audienceProvider);
    }

    @Override
    public @NonNull PlayerSoul player(@NonNull final UUID uuid) {
        return new BukkitPlayerSoul(uuid, this.audienceProvider.player(uuid));
    }

    @Override
    public @NonNull Collection<PlayerSoul> players() {
        Collection<PlayerSoul> players = new ArrayList<>();

        for (final Player player : Bukkit.getOnlinePlayers()) {
            players.add(this.player(player.getUniqueId()));
        }

        return players;
    }

    @Override
    public @NonNull String name(@NonNull final UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NonNull UUID uuidFromName(@NonNull final String name) {
        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }

    @Override
    protected boolean isOnline(@NonNull final UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).isOnline();
    }

}
