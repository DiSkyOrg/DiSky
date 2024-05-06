package info.itsthesky.disky.elements.structures.slash.models;

public class RegisteredCommand extends ParsedCommand {

    private final long commandId;
    private final String botName;
    private final String guildId;

    public RegisteredCommand(ParsedCommand parsedCommand,
                             long commandId,
                             String botName,
                             String guildId) {
        setArguments(parsedCommand.getArguments());
        setDescription(parsedCommand.getDescription());
        setName(parsedCommand.getName());
        setDescriptionLocalizations(parsedCommand.getDescriptionLocalizations());
        setEnabledFor(parsedCommand.getEnabledFor());
        setNameLocalizations(parsedCommand.getNameLocalizations());
        setDisabledByDefault(parsedCommand.isDisabledByDefault());
        setTrigger(parsedCommand.getTrigger());
        setBot(parsedCommand.getBot());

        this.commandId = commandId;
        this.botName = botName;
        this.guildId = guildId;
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
}
