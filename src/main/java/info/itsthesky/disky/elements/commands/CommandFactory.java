package info.itsthesky.disky.elements.commands;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.StringMode;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Utils;
import ch.njol.util.NonNullPair;
import ch.njol.util.StringUtils;
import info.itsthesky.disky.api.DiSkyType;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandFactory {

    private static final CommandFactory INSTANCE = new CommandFactory();
    private final Method PARSE_I;
    private final Pattern commandPattern = Pattern.compile("(?i)^(on )?discord command (\\S+)(\\s+(.+))?$");
    private final Pattern argumentPattern = Pattern.compile("<\\s*(?:(.+?)\\s*:\\s*)?(.+?)\\s*(?:=\\s*(" + SkriptParser.wildcard + "))?\\s*>");
    private final Pattern escape = Pattern.compile("[" + Pattern.quote("(|)<>%\\") + "]");
    private final String listPattern = "\\s*,\\s*|\\s+(and|or|, )\\s+";

    public HashMap<CommandData, CommandObject> commandMap = new HashMap<>();
    public static List<Argument<?>> currentArguments;

    private CommandFactory() {

        Method _PARSE_I = null;
        try {
            _PARSE_I = SkriptParser.class.getDeclaredMethod("parse_i", String.class);
            _PARSE_I.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Skript.error("Skript's 'parse_i' method could not be resolved.");
        }
        PARSE_I = _PARSE_I;

    }

    public static CommandFactory getInstance() {
        return INSTANCE;
    }

    private String escape(final String s) {
        return "" + escape.matcher(s).replaceAll("\\\\$0");
    }

    public boolean parseArguments(String args, CommandObject command, Event event) {
        SkriptParser parser = new SkriptParser(args, SkriptParser.PARSE_LITERALS, ParseContext.COMMAND);
        SkriptParser.ParseResult res = null;
        try {
            res = (SkriptParser.ParseResult) PARSE_I.invoke(parser, command.getPattern());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        List<Argument<?>> arguments = command.getArguments();
        if (res == null)
            return false;

        /* if (res == null)
        {
            // try custom parser
            final String[] rawArgs = args.split(" ");
            for (Argument<?> argument : arguments) {
                final ClassInfo<?> info = argument.getTypeInfo();
                if (info.getParser() == null || !info.getParser().canParse(ParseContext.COMMAND))
                    return false;

                final Object value = info.getParser().parse(rawArgs[argument.getIndex()], ParseContext.COMMAND);
                if (value == null && !argument.isOptional())
                {
                    argument.setToDefault(event);
                }
                else if (value != null)
                {
                    argument.set(event, new Object[] { value });
                }
                else {
                    // try using custom DiSky parser
                    if (info instanceof DiSkyType.DiSkyTypeWrapper) {
                        final DiSkyType<?> type = ((DiSkyType.DiSkyTypeWrapper<?>) info).getDiSkyType();
                        if (type.getRestParser() != null) {
                            final RestAction<?> restAction = type.getRestParser().apply(rawArgs[argument.getIndex()]);
                            if (restAction == null)
                                return false;

                            final Object newValue = restAction.complete();
                            if (newValue == null && !argument.isOptional())
                            {
                                argument.setToDefault(event);
                            }
                            else if (newValue != null)
                            {
                                argument.set(event, new Object[] { newValue });
                            }
                            else
                                return false;
                        }
                    }
                }
            }

            return true;
        }
        This needs an overall rework of the command system to work ASYNC, but discord commands will be less and less used, so does it worth it?
         */

        assert arguments.size() == res.exprs.length;
        for (int i = 0; i < res.exprs.length; i++) {
            if (res.exprs[i] == null) {
                arguments.get(i).setToDefault(event);
            } else {
                arguments.get(i).set(event, res.exprs[i].getArray(event));
            }
        }
        return true;
    }

    public ArrayList<ChannelType> parsePlaces(String[] places) {
        ArrayList<ChannelType> types = new ArrayList<>();
        for (String place : places) {
            if (info.itsthesky.disky.core.Utils.equalsAnyIgnoreCase(place, "server", "guild")) {
                types.addAll(Arrays.asList(ChannelType.TEXT, ChannelType.NEWS));
            } else if (info.itsthesky.disky.core.Utils.equalsAnyIgnoreCase(place, "dm", "pm", "direct message", "private message")) {
                types.add(ChannelType.PRIVATE);
            } else if (info.itsthesky.disky.core.Utils.equalsAnyIgnoreCase(place, "thread", "threads")) {
                types.addAll(Arrays.asList(ChannelType.GUILD_PUBLIC_THREAD, ChannelType.GUILD_PRIVATE_THREAD));
            } else if (info.itsthesky.disky.core.Utils.equalsAnyIgnoreCase(place, "voice")) {
                types.add(ChannelType.VOICE);
            } else {
                Skript.error("'executable in' should be any of ['guild', 'dm', 'voice', 'threads'], but found '" + place + "'");
                return null;
            }
        }
        return types;
    }

    public CommandObject add(SectionNode node) {

        String command = node.getKey();
        if (command == null) {
            return null;
        }

        command = ScriptLoader.replaceOptions(command);
        Matcher matcher = commandPattern.matcher(command);
        if (!matcher.matches()) {
            return null;
        }

        int level = 0;
        for (int i = 0; i < command.length(); i++) {
            if (command.charAt(i) == '[') {
                level++;
            } else if (command.charAt(i) == ']') {
                if (level == 0) {
                    Skript.error("Invalid placement of [optional brackets]");
                    return null;
                }
                level--;
            }
        }
        if (level > 0) {
            Skript.error("Invalid amount of [optional brackets]");
            return null;
        }

        command = matcher.group(2);

        String arguments = matcher.group(4);
        if (arguments == null) {
            arguments = "";
        }

        final StringBuilder pattern = new StringBuilder();

        List<Argument<?>> currentArguments = this.currentArguments = new ArrayList<>();
        Matcher m = argumentPattern.matcher(arguments);
        int lastEnd = 0;
        int optionals = 0;

        for (int i = 0; m.find(); i++) {
            pattern.append(escape("" + arguments.substring(lastEnd, m.start())));
            optionals += StringUtils.count(arguments, '[', lastEnd, m.start());
            optionals -= StringUtils.count(arguments, ']', lastEnd, m.start());

            lastEnd = m.end();

            ClassInfo<?> c;
            c = Classes.getClassInfoFromUserInput("" + m.group(2));
            final NonNullPair<String, Boolean> p = Utils.getEnglishPlural("" + m.group(2));
            if (c == null) {
                Skript.error("Unknown type '" + m.group(2) + "'");
                return null;
            }
            final Parser<?> parser = c.getParser();
            if (parser == null || !parser.canParse(ParseContext.COMMAND)) {
                Skript.error("Can't use " + c + " as argument of a command");
                return null;
            }

            final Argument<?> arg = Argument.newInstance(
                    m.group(1),
                    c,
                    m.group(3),
                    i,
                    !p.getSecond(),
                    optionals > 0);
            if (arg == null)
                return null;
            currentArguments.add(arg);

            if (arg.isOptional() && optionals == 0) {
                pattern.append('[');
                optionals++;
            }
            pattern.append("%" + (arg.isOptional() ? "-" : "") + Utils.toEnglishPlural(c.getCodeName(), p.getSecond()) + "%");
        }

        pattern.append(escape("" + arguments.substring(lastEnd)));
        optionals += StringUtils.count(arguments, '[', lastEnd);
        optionals -= StringUtils.count(arguments, ']', lastEnd);
        for (int i = 0; i < optionals; i++)
            pattern.append(']');

        node.convertToEntries(0);
        if (!CommandRegistry.commandStructure.validate(node)) {
            return null;
        }

        if (!(node.get("trigger") instanceof SectionNode)) {
            return null;
        }

        SectionNode trigger = (SectionNode) node.get("trigger");

        Expression<String> description = parseExpression(ScriptLoader.replaceOptions(node.get("description", "")));
        String permMessage = ScriptLoader.replaceOptions(node.get("permission message", ""));
        Expression<String> usage = parseExpression(ScriptLoader.replaceOptions(node.get("usage", "")));
        String category = ScriptLoader.replaceOptions(node.get("category", ""));

        Timespan cooldownGuild = Timespan.parse(ScriptLoader.replaceOptions(node.get("guild cooldown", "0 second")));
        if (cooldownGuild == null) {
            Skript.error("Cannot parse a non-timespan cooldown for the discord command. Input: '"+ScriptLoader.replaceOptions(node.get("guild cooldown", ""))+"'");
            return null;
        }
        //Timespan cooldownGuild = Timespan.parse(ScriptLoader.replaceOptions(node.get("guild cooldown", "")));
        String cooldownMessage = ScriptLoader.replaceOptions(node.get("cooldown message", ""));

        String permList = ScriptLoader.replaceOptions(node.get("permissions", ""));
        List<String> perms = permList.isEmpty() ? new ArrayList<>() : Arrays.asList(permList.split(listPattern));

        String aliasesString = ScriptLoader.replaceOptions(node.get("aliases", ""));
        List<String> aliases = aliasesString.isEmpty() ? null : Arrays.asList(aliasesString.split(listPattern));

        List<Expression<String>> prefixes = new ArrayList<>();
        String rawPrefixes = ScriptLoader.replaceOptions(node.get("prefixes", ""));
        if (rawPrefixes.isEmpty()) {
            if (command.length() == 1) {
                prefixes.add(new SimpleLiteral<>("", false));
            } else {
                prefixes.add(new SimpleLiteral<>(String.valueOf(command.charAt(0)), false));
                command = command.substring(1);
            }
        } else {
            for (String prefix : rawPrefixes.split(listPattern)) {
                if (prefix.startsWith("\"") && prefix.endsWith("\"")) {
                    prefix = prefix.substring(1, prefix.length() - 1);
                }
                Expression<String> prefixExpr = VariableString.newInstance(prefix, StringMode.MESSAGE);
                try {
                    if (((VariableString) prefixExpr).isSimple()) {
                        prefixExpr = new SimpleLiteral<>(prefix, false);
                    }
                } catch (NullPointerException ignored) { }
                prefixes.add(prefixExpr);
            }
        }

        String roleString = ScriptLoader.replaceOptions(node.get("roles", ""));
        List<String> roles = roleString.isEmpty() ? null : Arrays.asList(roleString.split(listPattern));

        String botString = ScriptLoader.replaceOptions(node.get("bots", ""));
        List<String> bots = botString.isEmpty() ? null : Arrays.asList(botString.split(listPattern));

        List<ChannelType> places = parsePlaces(ScriptLoader.replaceOptions(node.get("executable in", "guild, dm")).split(listPattern));

        if (places == null) {
            return null;
        }

        CommandObject commandObject;
        CommandFactory.currentArguments = currentArguments;
        commandObject = new CommandObject(
                command, pattern.toString(), currentArguments,
                prefixes, aliases, description, usage, roles, places, bots, SkriptUtils.loadCode(trigger, CommandEvent.class),
                perms, permMessage, category,
                cooldownGuild, cooldownMessage
        );
        this.commandMap.put(new CommandData(command, commandObject), commandObject);
        return commandObject;

    }

    private Expression<String> parseExpression(String text) {
        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1);
        }
        Expression<String> expr = VariableString.newInstance(text, StringMode.MESSAGE);
        try {
            if (((VariableString) expr).isSimple()) {
                expr = new SimpleLiteral<>(text, false);
            }
        } catch (NullPointerException ignored) { }
        return expr;
    }

    public boolean remove(String name) {
        for (CommandData commandData : commandMap.keySet()) {
            CommandObject commandObject = commandData.getCommand();
            if (commandObject.getName().equalsIgnoreCase(name)) {
                commandMap.remove(commandData);
                return true;
            }
        }
        return false;
    }

    public Collection<CommandData> getCommands() {
        return commandMap.keySet();
    }
}
