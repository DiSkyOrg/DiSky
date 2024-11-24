package info.itsthesky.disky.elements.sections.handler;

import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.JDAUtils;
import info.itsthesky.disky.core.Utils;
import info.itsthesky.disky.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DiSkyRuntimeHandler {

    private static final List<Exception> errors = new ArrayList<>();
    private static boolean isHandling = false;

    public static void start() {
        errors.clear();
        isHandling = true;
    }

    public static void error(Exception error) {
        error(error, null);
    }

    public static void error(Exception error, Node node) {
        error(error, node, true);
    }

    public static void error(Exception error, Node node, boolean shouldDisplayStacktrace) {
        if (!shouldDisplayStacktrace && ConfigManager.get("debug", false))
            shouldDisplayStacktrace = true;

        if (!isHandling) {
            //DiSky.getErrorHandler().exception(null, error, node);
            printException(node, error, shouldDisplayStacktrace);
            return;
        }

        if (node != null)
            errors.add(new NodeException(error, node));
        else
            errors.add(error);
    }

    public static List<Exception> end() {
        isHandling = false;

        final List<Exception> errors = new ArrayList<>(DiSkyRuntimeHandler.errors);
        DiSkyRuntimeHandler.errors.clear();
        return errors;
    }

    //region Common Errors

    public static void exprNotSet(Node node, Expression<?>... exprs) {
        if (exprs.length == 1) {
            final var expr = exprs[0];
            if (expr == null)
                error(new IllegalArgumentException("The given expression is not set! (None)"), node, false);
            else
                error(new IllegalArgumentException("The value of the expression '" + expr + "' is not set! (None)"), node, false);
        } else {
            final StringBuilder builder = new StringBuilder("The value of the following expressions are not set:\n");
            for (Expression<?> expr : exprs) {
                builder.append(" - ").append(expr).append("\n");
            }
            error(new IllegalArgumentException(builder.toString()), node, false);
        }
    }
    public static boolean checkSet(Node node, Object... data) {
        final List<Expression<?>> unSet = new ArrayList<>();
        for (int i = 0; i < data.length; i += 2) {
            if (data[i] == null) {
                unSet.add((Expression<?>) data[i + 1]);
            }
        }

        if (!unSet.isEmpty()) {
            exprNotSet(node, unSet.toArray(new Expression[0]));
            return false;
        }

        return true;
    }

    public static void notSnowflake(Node node, String id) {
        error(new IllegalArgumentException("The given ID '" + id + "' is not a valid identifier (snowflake)!"), node, false);
    }
    public static boolean checkSnowflake(Node node, String id) {
        if (!JDAUtils.isSnowflake(id)) {
            notSnowflake(node, id);
            return false;
        }

        return true;
    }

    //endregion

    //region Error Printer

    private static final List<String> QUEUED_MESSAGES = new ArrayList<>();
    private static void send(String message) {
        QUEUED_MESSAGES.add(Utils.colored(message));
    }
    private static void sendAll() {
        Bukkit.getConsoleSender().sendMessage(QUEUED_MESSAGES.toArray(new String[0]));
        QUEUED_MESSAGES.clear();
    }

    private static void printException(@Nullable Node node, @Nullable Exception exception, boolean displayStacktrace) {
        if (exception == null)
            return;
        QUEUED_MESSAGES.clear();

        send("&4[&c!&4] &c");
        send("&4[&c!&4] &4DiSky Internal Error (version: "+ DiSky.getInstance().getDescription().getVersion()+")");
        send("&4[&c!&4] &4Error type: &c" + exception.getClass().getSimpleName());
        send("&4[&c!&4] &c");

        send("&4[&c!&4] &e- - - - - - - - - - - - - - - - - - - - - - - - -");
        for (String line : exception.getMessage().split("\n")) {
            send("&4[&c!&4] &e" + line);
        }

        if (displayStacktrace) {
            send("&4[&c!&4] &e");
            for (StackTraceElement element : exception.getStackTrace()) {
                send("&4[&c!&4] &6" + element.toString());
            }
        }

        send("&4[&c!&4] &e- - - - - - - - - - - - - - - - - - - - - - - - -");

        if (node != null) {
            send("&4[&c!&4] &c");
            send("&4[&c!&4] &4Error occurred in the following node:");
            send("&4[&c!&4] &c");
            send("&4[&c!&4] &7" + node.getKey() + " &6(" + node.getConfig().getFileName() + ", line " + node.getLine() + ")");
            send("&4[&c!&4] &c");
        }

        send("&4[&c!&4] &c");
        sendAll();
    }

    //endregion

    /**
     * Represents an exception that occurred during the execution of a node,
     * with a direct link to the node that caused the exception.
     */
    public static class NodeException extends Exception {
        private final Node node;
        public NodeException(Exception exception, Node node) {
            super(exception);
            this.node = node;
        }

        public Node getNode() {
            return node;
        }
    }
}
