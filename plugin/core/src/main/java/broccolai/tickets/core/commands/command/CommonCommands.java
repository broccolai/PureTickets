package broccolai.tickets.core.commands.command;

import broccolai.tickets.core.locale.Message;
import broccolai.tickets.core.model.user.UserAudience;
import broccolai.tickets.core.ticket.Ticket;
import broccolai.tickets.core.ticket.TicketStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.Template;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class CommonCommands implements BaseCommand {

    protected final void processShow(final @NonNull UserAudience soul, final @NonNull Ticket ticket) {
        Template[] templates = ticket.templates();

        TextComponent.Builder show = Component.text()
                .append(
                        Message.SHOW__SENDER.use(templates),
                        Message.SHOW__MESSAGE.use(templates),
                        Message.SHOW__LOCATION.use(templates)
                );

        if (ticket.getStatus() != TicketStatus.PICKED) {
            show.append(Message.SHOW__UNPICKED.use(templates));
        } else {
            show.append(Message.SHOW__PICKER.use(templates));
        }

        soul.sendMessage(show);
    }

    protected final void processLog(final @NonNull UserAudience soul, final @NonNull Ticket ticket) {
        TextComponent.Builder component = Component.text()
                .append(Message.TITLE__TICKET_LOG.use(ticket.templates()));

        ticket.getMessages().forEach(message -> {
            HoverEvent<Component> event;

            if (message.getData() != null) {
                event = HoverEvent.showText(
                        Component.text(message.getData())
                );
            } else {
                event = null;
            }

            component.append(Component.newline(), Message.FORMAT__LOG.use(message.templates()).hoverEvent(event));
        });

        soul.sendMessage(component);
    }

}