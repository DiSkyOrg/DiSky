package net.itsthesky.disky.elements.structures.slash.models;

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.itsthesky.disky.DiSky;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandGroup {

    private final String name;
    private final CommandType type;

    private final Map<String, ParsedCommand> subCommands;
    private final Map<String, CommandGroup> subGroups;
    private ParsedCommand singleCommand;  // For SINGLE type groups

    private String description;
    private Map<DiscordLocale, String> descriptionLocalizations;
    private Map<DiscordLocale, String> nameLocalizations;

    public CommandGroup(String name, CommandType type) {
        this.name = name;
        this.type = type;
        this.subCommands = new HashMap<>();
        this.subGroups = new HashMap<>();
        this.descriptionLocalizations = new HashMap<>();
        this.nameLocalizations = new HashMap<>();
        this.description = "Commands for " + name;
    }

    public void setSingleCommand(ParsedCommand command) {
        if (type != CommandType.SINGLE) {
            throw new IllegalStateException("Cannot set single command on a " + type + " group");
        }
        this.singleCommand = command;
        this.description = command.getDescription();
        this.descriptionLocalizations = command.getDescriptionLocalizations();
        this.nameLocalizations = command.getNameLocalizations();
    }

    public void addSubCommand(ParsedCommand command) {
        String[] parts = command.getName().split(" ");
        String originalName = command.getName();

        if (parts.length == 1) {
            // Single command - convert to a group with single command
            setSingleCommand(command);
            return;
        }

        if (!Objects.equals(parts[0], this.name)) {
            throw new IllegalArgumentException("Command name must start with the group name: " + command.getName());
        }

        if (parts.length == 2) {
            // Single level subcommand (e.g., "set color")
            String subCommandName = parts[1];
            ParsedCommand subCommand = cloneCommandWithNewName(command, subCommandName);
            subCommand.setOriginalName(originalName);
            subCommands.put(subCommandName, subCommand);
        } else if (parts.length == 3) {
            // Two level subcommand with group (e.g., "set setting color")
            String groupName = parts[1];
            String subCommandName = parts[2];

            // Get or create subgroup
            CommandGroup subGroup = subGroups.computeIfAbsent(groupName, name -> {
                CommandGroup group = new CommandGroup(name, CommandType.GROUP);
                group.setDescription("Commands for " + name);
                return group;
            });

            ParsedCommand subCommand = cloneCommandWithNewName(command, subCommandName);
            subCommand.setOriginalName(originalName);
            subGroup.getSubCommands().put(subCommandName, subCommand);
        } else {
            DiSky.debug("Command name has too many parts (max 3 levels): " + command.getName());
        }
    }

    private ParsedCommand cloneCommandWithNewName(ParsedCommand original, String newName) {
        ParsedCommand clone = new ParsedCommand();
        clone.setName(newName);
        clone.setDescription(original.getDescription());
        clone.setDescriptionLocalizations(new HashMap<>(original.getDescriptionLocalizations()));
        clone.setNameLocalizations(new HashMap<>(original.getNameLocalizations()));
        clone.setArguments(new ArrayList<>(original.getArguments()));
        clone.setEnabledFor(new ArrayList<>(original.getEnabledFor()));
        clone.setDisabledByDefault(original.isDisabledByDefault());
        clone.setRawBot(original.getRawBot());
        clone.setTrigger(original.getTrigger());
        clone.setCooldown(original.getCooldown());
        clone.setOnCooldown(original.getOnCooldown());
        return clone;
    }

    public SlashCommandData buildCommandData() {
        SlashCommandData commandData = Commands.slash(name, getDescription());

        // Add localizations
        if (!nameLocalizations.isEmpty()) {
            commandData.setNameLocalizations(nameLocalizations);
        }
        if (!descriptionLocalizations.isEmpty()) {
            commandData.setDescriptionLocalizations(descriptionLocalizations);
        }

        if (type == CommandType.SINGLE) {
            // Add options for single command
            if (singleCommand != null) {
                for (ParsedArgument arg : singleCommand.getArguments()) {
                    commandData.addOptions(createOptionData(arg));
                }

                if (singleCommand.isDisabledByDefault() && singleCommand.getEnabledFor().isEmpty())
                    commandData.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
                else if (!singleCommand.getEnabledFor().isEmpty())
                    commandData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(singleCommand.getEnabledFor()));
            }

            return commandData;
        }

        // Add direct subcommands
        for (ParsedCommand subCommand : subCommands.values()) {
            SubcommandData subCommandData = createSubcommandData(subCommand);
            commandData.addSubcommands(subCommandData);
        }

        // Add subcommand groups
        for (Map.Entry<String, CommandGroup> groupEntry : subGroups.entrySet()) {
            CommandGroup subGroup = groupEntry.getValue();
            SubcommandGroupData groupData = new SubcommandGroupData(
                    groupEntry.getKey(),
                    subGroup.getDescription()
            );

            for (ParsedCommand groupCommand : subGroup.getSubCommands().values()) {
                SubcommandData subCommandData = createSubcommandData(groupCommand);
                groupData.addSubcommands(subCommandData);
            }

            commandData.addSubcommandGroups(groupData);
        }

        return commandData;
    }

    private OptionData createOptionData(ParsedArgument arg) {
        OptionData optionData = new OptionData(
                arg.getType(),
                arg.getName(),
                arg.getDescription(),
                arg.isRequired()
        );

        if (arg.hasChoices()) {
            for (Map.Entry<String, Object> choice : arg.getChoices().entrySet()) {
                if (choice.getValue() instanceof String) {
                    optionData.addChoice(choice.getKey(), (String) choice.getValue());
                } else if (choice.getValue() instanceof Number) {
                    if (arg.getType() == OptionType.INTEGER) {
                        optionData.addChoice(choice.getKey(), ((Number) choice.getValue()).longValue());
                    } else {
                        optionData.addChoice(choice.getKey(), ((Number) choice.getValue()).doubleValue());
                    }
                }
            }
        }

        if (arg.isAutoCompletion()) {
            optionData.setAutoComplete(true);
        }

        return optionData;
    }

    private SubcommandData createSubcommandData(ParsedCommand command) {
        SubcommandData subCommandData = new SubcommandData(
                command.getName(),
                command.getDescription() != null ? command.getDescription() : "No description provided");

        for (ParsedArgument arg : command.getArguments()) {
            subCommandData.addOptions(createOptionData(arg));
        }

        return subCommandData;
    }

    /**
     * Find a command in this group by its full command path
     * @param fullCommandPath The full command path (e.g. "set color" or "set setting color")
     * @return The ParsedCommand if found, null otherwise
     */
    public ParsedCommand findCommand(String fullCommandPath) {
        String[] parts = fullCommandPath.split(" ");

        if (parts.length == 0 || !parts[0].equals(name)) {
            return null;
        }

        if (parts.length == 1) {
            // Request for the base command
            if (type == CommandType.SINGLE) {
                return singleCommand;
            }
            return null;
        }

        if (parts.length == 2) {
            // Looking for a direct subcommand
            return subCommands.get(parts[1]);
        }

        if (parts.length == 3) {
            // Looking for a subcommand in a group
            CommandGroup subGroup = subGroups.get(parts[1]);
            if (subGroup == null) return null;
            return subGroup.getSubCommands().get(parts[2]);
        }

        return null; // Too many parts
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public CommandType getType() {
        return type;
    }

    public String getDescription() {
        return description != null ? description : "Commands for " + name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<DiscordLocale, String> getDescriptionLocalizations() {
        return descriptionLocalizations;
    }

    public void setDescriptionLocalizations(Map<DiscordLocale, String> descriptionLocalizations) {
        this.descriptionLocalizations = descriptionLocalizations;
    }

    public Map<DiscordLocale, String> getNameLocalizations() {
        return nameLocalizations;
    }

    public void setNameLocalizations(Map<DiscordLocale, String> nameLocalizations) {
        this.nameLocalizations = nameLocalizations;
    }

    public Map<String, ParsedCommand> getSubCommands() {
        return subCommands;
    }

    public Map<String, CommandGroup> getSubGroups() {
        return subGroups;
    }

    public ParsedCommand getSingleCommand() {
        return singleCommand;
    }

    @Override
    public String toString() {
        return "CommandGroup{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", subCommandsCount=" + subCommands.size() +
                ", subGroupsCount=" + subGroups.size() +
                ", hasSingleCommand=" + (singleCommand != null) +
                '}';
    }
}