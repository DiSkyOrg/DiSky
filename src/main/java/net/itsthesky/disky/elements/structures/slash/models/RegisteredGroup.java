package net.itsthesky.disky.elements.structures.slash.models;

import net.dv8tion.jda.api.entities.User;
import net.itsthesky.disky.DiSky;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a registered command group on Discord.
 * Each RegisteredGroup corresponds to a single slash command with its subcommands.
 */
public class RegisteredGroup {

    private final CommandGroup commandGroup;
    private final long commandId;
    private final String botName;
    private final String guildId; // null for global commands

    // Map to handle cooldowns per user and command path
    private final Map<String, Map<User, Long>> cooldowns = new HashMap<>();

    public RegisteredGroup(CommandGroup commandGroup, long commandId, String botName, String guildId) {
        this.commandGroup = commandGroup;
        this.commandId = commandId;
        this.botName = botName;
        this.guildId = guildId;
    }

    /**
     * Checks if a command is in cooldown for a user
     * @param user The user to check
     * @param commandPath The full command path (e.g. "set color" or "set setting color")
     * @return true if the command is in cooldown
     */
    public boolean isInCooldown(User user, String commandPath) {
        Map<User, Long> commandCooldowns = cooldowns.get(commandPath);
        if (commandCooldowns == null) return false;
        Long cooldownEnd = commandCooldowns.get(user);
        return cooldownEnd != null && cooldownEnd > System.currentTimeMillis();
    }

    /**
     * Gets the remaining cooldown time for a user and command
     * @param user The user
     * @param commandPath The full command path
     * @return The remaining cooldown time in milliseconds, or 0 if not in cooldown
     */
    public long getCooldown(User user, String commandPath) {
        Map<User, Long> commandCooldowns = cooldowns.get(commandPath);
        if (commandCooldowns == null) return 0L;
        Long cooldownEnd = commandCooldowns.get(user);
        return cooldownEnd != null ? Math.max(0, cooldownEnd - System.currentTimeMillis()) : 0L;
    }

    /**
     * Sets a cooldown for a user and command
     * @param user The user
     * @param commandPath The full command path
     * @param cooldownDuration The cooldown duration in milliseconds
     */
    public void setCooldown(User user, String commandPath, long cooldownDuration) {
        cooldowns.computeIfAbsent(commandPath, k -> new HashMap<>())
                .put(user, System.currentTimeMillis() + cooldownDuration);
    }

    /**
     * Finds a command in the group by its full path
     * @param commandPath The full command path (e.g. "set" or "set color" or "set setting color")
     * @return The parsed command, or null if not found
     */
    public ParsedCommand findCommand(String commandPath) {
        String[] parts = commandPath.split(" ");
        
        // Single command (e.g., "set")
        if (parts.length == 1) {
            if (commandGroup.getType() == CommandType.SINGLE) {
                return commandGroup.getSingleCommand();
            }
            return null;
        }
        
        // Subcommand (e.g., "set color")
        if (parts.length == 2) {
            return commandGroup.getSubCommands().get(parts[1]);
        }
        
        // Subcommand in group (e.g., "set setting color")
        if (parts.length == 3) {
            CommandGroup subGroup = commandGroup.getSubGroups().get(parts[1]);
            if (subGroup == null) {
                DiSky.debug("Subgroup '" + parts[1] + "' not found in command group '" + commandGroup.getName() + "'");
                return null;
            }
            ParsedCommand cmd = subGroup.getSubCommands().get(parts[2]);
            if (cmd == null) {
                DiSky.debug("Command '" + parts[2] + "' not found in subgroup '" + parts[1] + "'");
            }
            return cmd;
        }
        
        DiSky.debug("Invalid command path: " + commandPath + " (too many parts)");
        return null;
    }

    /**
     * Clears all cooldowns for this group
     */
    public void clearCooldowns() {
        cooldowns.clear();
    }

    // Getters
    public CommandGroup getCommandGroup() {
        return commandGroup;
    }

    public long getCommandId() {
        return commandId;
    }

    public String getBotName() {
        return botName;
    }

    public String getGuildId() {
        return guildId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisteredGroup that = (RegisteredGroup) o;
        return commandId == that.commandId &&
                Objects.equals(botName, that.botName) &&
                Objects.equals(guildId, that.guildId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandId, botName, guildId);
    }

    @Override
    public String toString() {
        return "RegisteredGroup{" +
                "name='" + commandGroup.getName() + '\'' +
                ", commandId=" + commandId +
                ", botName='" + botName + '\'' +
                ", guildId='" + guildId + '\'' +
                '}';
    }
}