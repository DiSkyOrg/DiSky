package net.itsthesky.disky.elements.effects;


import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.itsthesky.disky.api.DiSkyRegistry;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

@Name("Set Bot Guild Profile")
@Description({
    "Set the bot's per-guild avatar, banner or bio.",
    "Requires the bot to be a member of the guild.",
    "This is a JDA 6.1.0+ feature."
})
@Examples({
    "set guild avatar of bot \"mybot\" in event-guild to \"https://example.com/avatar.png\"",
    "set guild bio of bot \"mybot\" in event-guild to \"Hello from this server!\"",
    "reset guild avatar of bot \"mybot\" in event-guild"
})
public class EffSetBotGuildProfile extends AsyncEffect {

    static {
        DiSkyRegistry.registerEffect(
                EffSetBotGuildProfile.class,
                "set guild avatar of [bot] %bot% in %guild% to %string%",
                "set guild banner of [bot] %bot% in %guild% to %string%",
                "set guild bio of [bot] %bot% in %guild% to %string%",
                "reset guild avatar of [bot] %bot% in %guild%",
                "reset guild banner of [bot] %bot% in %guild%",
                "reset guild bio of [bot] %bot% in %guild%"
        );
    }

    private int pattern;
    private Expression<Bot> exprBot;
    private Expression<Guild> exprGuild;
    private Expression<String> exprValue;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);
        pattern = matchedPattern;
        exprBot = (Expression<Bot>) expressions[0];
        exprGuild = (Expression<Guild>) expressions[1];
        if (pattern < 3) {
            exprValue = (Expression<String>) expressions[2];
        }
        return true;
    }

    @Override
    public void execute(@NotNull Event e) {
        final Bot bot = EasyElement.parseSingle(exprBot, e, null);
        final Guild guild = EasyElement.parseSingle(exprGuild, e, null);
        if (bot == null || guild == null)
            return;

        try {
            final var selfMember = guild.getSelfMember();
            final var manager = selfMember.getManager();

            switch (pattern) {
                case 0 -> { // set avatar
                    final String url = EasyElement.parseSingle(exprValue, e, null);
                    if (url == null) return;
                    final Icon icon = Icon.from(new URL(url).openStream());
                    manager.setAvatar(icon).complete();
                }
                case 1 -> { // set banner
                    final String url = EasyElement.parseSingle(exprValue, e, null);
                    if (url == null) return;
                    final Icon icon = Icon.from(new URL(url).openStream());
                    manager.setBanner(icon).complete();
                }
                case 2 -> { // set bio
                    final String bio = EasyElement.parseSingle(exprValue, e, null);
                    if (bio == null) return;
                    manager.setBio(bio).complete();
                }
                case 3 -> { // reset avatar
                    manager.setAvatar(null).complete();
                }
                case 4 -> { // reset banner
                    manager.setBanner(null).complete();
                }
                case 5 -> { // reset bio
                    manager.setBio(null).complete();
                }
            }
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error(ex);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        final String action = switch (pattern) {
            case 0 -> "set guild avatar of";
            case 1 -> "set guild banner of";
            case 2 -> "set guild bio of";
            case 3 -> "reset guild avatar of";
            case 4 -> "reset guild banner of";
            case 5 -> "reset guild bio of";
            default -> "modify guild profile of";
        };
        return action + " " + exprBot.toString(e, debug) + " in " + exprGuild.toString(e, debug);
    }
}
