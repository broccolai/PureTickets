package broccolai.tickets.bukkit;

import broccolai.tickets.api.model.user.OnlineSoul;
import broccolai.tickets.api.service.message.MessageService;
import broccolai.tickets.api.service.user.UserService;
import broccolai.tickets.bukkit.inject.BukkitModule;
import broccolai.tickets.bukkit.listeners.PlayerJoinListener;
import broccolai.tickets.bukkit.model.BukkitOnlineSoul;
import broccolai.tickets.bukkit.service.BukkitUserService;
import broccolai.tickets.core.PureTickets;
import broccolai.tickets.core.inject.platform.PluginPlatform;
import broccolai.tickets.core.utilities.ArrayHelper;
import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.logging.Level;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

@SuppressWarnings("unused")
public final class BukkitPlatform extends JavaPlugin implements PluginPlatform {

    private static final Class<? extends Listener>[] LISTENERS = ArrayHelper.create(
            PlayerJoinListener.class
    );

    private @MonotonicNonNull PureTickets pureTickets;

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onEnable() {
        this.getDataFolder().mkdirs();

        Injector injector = Guice.createInjector(new BukkitModule(this, this));

        this.pureTickets = injector.getInstance(PureTickets.class);
        this.pureTickets.load();

        try {
            CommandManager<OnlineSoul> commandManager = this.commandManager(
                    injector.getInstance(UserService.class),
                    injector.getInstance(MessageService.class)
            );
            this.pureTickets.commands(commandManager, COMMANDS);
        } catch (final Exception e) {
            this.getLogger().log(Level.SEVERE, "Could not initiate Command Manager", e);
        }

        for (final Class<? extends Listener> listenerClass : LISTENERS) {
            Listener listener = injector.getInstance(listenerClass);
            this.getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    @Override
    public void onDisable() {
        this.pureTickets.unload();
    }

    private CommandManager<OnlineSoul> commandManager(
            final @NonNull UserService userService,
            final @NonNull MessageService messageService
    ) throws Exception {
        BukkitUserService bukkitUserService = (BukkitUserService) userService;

        PaperCommandManager<@NonNull OnlineSoul> cloudManager = new PaperCommandManager<>(
                this,
                AsynchronousCommandExecutionCoordinator.<OnlineSoul>newBuilder().withAsynchronousParsing().build(),
                sender -> {
                    if (sender instanceof ConsoleCommandSender) {
                        return userService.console();
                    }

                    Player player = (Player) sender;
                    return bukkitUserService.player(player);
                },
                soul -> {
                    BukkitOnlineSoul bukkitSoul = (BukkitOnlineSoul) soul;
                    return bukkitSoul.sender();
                }
        );

        if (cloudManager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            cloudManager.registerAsynchronousCompletions();
        }

        this.pureTickets.defaultCommandManagerSettings(cloudManager);
        return cloudManager;
    }

    @Override
    public ClassLoader loader() {
        return this.getClassLoader();
    }

}
