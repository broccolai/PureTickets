package broccolai.tickets.ticket;

import broccolai.tickets.configuration.Config;
import broccolai.tickets.exceptions.TicketClosed;
import broccolai.tickets.exceptions.TicketOpen;
import broccolai.tickets.exceptions.TooManyOpenTickets;
import broccolai.tickets.storage.SQLManager;
import broccolai.tickets.storage.TimeAmount;
import com.google.common.collect.Lists;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class TicketManager {
    private final Config config;
    private final SQLManager sqlManager;

    public TicketManager(Config config, SQLManager sqlManager) {
        this.config = config;
        this.sqlManager = sqlManager;
    }

    public @Nullable Ticket get(int id) {
        return sqlManager.getTicket().select(id);
    }

    public List<Ticket> getAll(UUID uuid, TicketStatus status) {
        return sqlManager.getTicket().selectAll(uuid, status);
    }

    public Ticket getLatestTicket(UUID uuid, TicketStatus... statuses) {
        return sqlManager.getTicket().selectLastTicket(uuid, statuses);
    }

    public List<Integer> getIds(UUID uuid, TicketStatus status) {
        return sqlManager.getTicket().selectIds(uuid, status);
    }

    public Boolean exists(Integer id) {
        return sqlManager.getTicket().exists(id);
    }

    public Integer count(UUID uuid, TicketStatus status) {
        return sqlManager.getTicket().count(uuid, status);
    }

    public Integer count(TicketStatus status) {
        return sqlManager.getTicket().count(status);
    }

    public EnumMap<TicketStatus, Integer> stats(UUID uuid) {
        return sqlManager.getTicket().selectTicketStats(uuid);
    }

    public List<Ticket> all(TicketStatus status) {
        return sqlManager.getTicket().selectAll(status);
    }

    public List<String> allNames(TicketStatus status) {
        return sqlManager.getTicket().selectNames(status);
    }

    public HashMap<UUID, Integer> highscores(TimeAmount span) {
        return sqlManager.getTicket().highscores(span);
    }

    public Ticket createTicket(Player player, Message message) throws TooManyOpenTickets {
        if (count(player.getUniqueId(), TicketStatus.OPEN) >= config.LIMIT__OPEN_TICKETS + 1) {
            throw new TooManyOpenTickets(config);
        }

        UUID uuid = player.getUniqueId();
        Location location = player.getLocation();

        Integer id = sqlManager.getTicket().insert(uuid, TicketStatus.OPEN, null, location);
        Ticket ticket = new Ticket(id, uuid, Lists.newArrayList(message), location, TicketStatus.OPEN, null);

        sqlManager.getMessage().insert(ticket, message);

        return ticket;
    }

    public Ticket update(Ticket ticket, Message message) throws TicketClosed {
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new TicketClosed();
        }

        return addMessageAndUpdate(ticket, message);
    }

    public Ticket pick(UUID uuid, Ticket ticket) throws TicketClosed {
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new TicketClosed();
        }

        Message message = new Message(MessageReason.PICKED, LocalDateTime.now(), uuid);

        ticket.setStatus(TicketStatus.PICKED);
        ticket.setPickerUUID(uuid);

        return addMessageAndUpdate(ticket, message);
    }

    public Ticket yield(UUID uuid, Ticket ticket) throws TicketOpen {
        if (ticket.getStatus() == TicketStatus.OPEN) {
            throw new TicketOpen();
        }

        Message message = new Message(MessageReason.REOPENED, LocalDateTime.now(), uuid);

        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPickerUUID(uuid);

        return addMessageAndUpdate(ticket, message);
    }

    public Ticket close(UUID uuid, Ticket ticket) throws TicketClosed {
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new TicketClosed();
        }

        Message message = new Message(MessageReason.CLOSED, LocalDateTime.now(), uuid);

        ticket.setStatus(TicketStatus.CLOSED);

        return addMessageAndUpdate(ticket, message);
    }

    public Ticket done(UUID uuid, Ticket ticket) throws TicketClosed {
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new TicketClosed();
        }

        Message message = new Message(MessageReason.DONE_MARKED, LocalDateTime.now(), uuid);

        ticket.setStatus(TicketStatus.CLOSED);

        return addMessageAndUpdate(ticket, message);
    }

    public Ticket reopen(UUID uuid, Ticket ticket) throws TicketOpen {
        if (ticket.getStatus() == TicketStatus.OPEN) {
            throw new TicketOpen();
        }

        Message message = new Message(MessageReason.REOPENED, LocalDateTime.now(), uuid);

        ticket.setStatus(TicketStatus.OPEN);

        return addMessageAndUpdate(ticket, message);
    }

    public Ticket note(UUID uuid, Ticket ticket, String input) {
        Message message = new Message(MessageReason.NOTE, LocalDateTime.now(), input, uuid);

        return addMessageAndUpdate(ticket, message);
    }

    private Ticket addMessageAndUpdate(Ticket ticket, Message message) {
        ticket.getMessages().add(message);

        sqlManager.getMessage().insert(ticket, message);
        sqlManager.getTicket().update(ticket);

        return ticket;
    }
}