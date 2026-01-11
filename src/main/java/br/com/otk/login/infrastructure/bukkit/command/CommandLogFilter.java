package br.com.otk.login.infrastructure.bukkit.command;

import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.LogEvent;

public class CommandLogFilter extends AbstractFilter {

    @Override
    public Result filter(LogEvent event) {
        if (event == null || event.getMessage() == null) {
            return Result.NEUTRAL;
        }

        String message = event.getMessage().getFormattedMessage();

        if (message.contains("issued server command: /login")
                || message.contains("issued server command: /register")) {
            return Result.DENY;
        }

        return Result.NEUTRAL;
    }
}
