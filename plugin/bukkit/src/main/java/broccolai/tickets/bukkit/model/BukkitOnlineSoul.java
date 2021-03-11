package broccolai.tickets.bukkit.model;

import broccolai.tickets.api.model.user.OnlineSoul;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface BukkitOnlineSoul extends OnlineSoul {

    @NonNull CommandSender sender();

}