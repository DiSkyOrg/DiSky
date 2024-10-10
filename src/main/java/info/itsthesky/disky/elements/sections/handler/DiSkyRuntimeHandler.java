package info.itsthesky.disky.elements.sections.handler;

import ch.njol.skript.config.Node;
import info.itsthesky.disky.DiSky;

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
        if (!isHandling) {
            DiSky.getErrorHandler().exception(null, error, node);
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
