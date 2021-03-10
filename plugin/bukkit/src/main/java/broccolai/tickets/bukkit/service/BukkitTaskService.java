package broccolai.tickets.bukkit.service;

import broccolai.tickets.api.model.task.Task;
import broccolai.tickets.api.service.tasks.TaskService;
import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class BukkitTaskService implements TaskService {

    private final Plugin plugin;

    @Inject
    public BukkitTaskService(final @NonNull Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void async(@NonNull final Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }

    @Override
    public void schedule(@NonNull final Task task) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, task, task.delay(), task.repeat());
    }

    @Override
    public void clear() {
        Bukkit.getScheduler().cancelTasks(this.plugin);
    }

}
