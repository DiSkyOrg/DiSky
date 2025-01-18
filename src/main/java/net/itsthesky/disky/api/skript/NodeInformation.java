package net.itsthesky.disky.api.skript;

import ch.njol.skript.config.Node;
import ch.njol.skript.log.SkriptLogger;

/**
 * @author Sky
 */
public class NodeInformation {

    public static NodeInformation last;

    private final Node node;
    private int line;
    private String fileName;
    private String lineContent;

    public NodeInformation() {
        this(SkriptLogger.getNode());
    }

    public NodeInformation(Node node) {
        this.node = node;
        if (this.node == null)
            return;
        this.line = this.node.getLine();
        this.fileName = this.node.getConfig().getFileName();
        this.lineContent = this.node.getKey();
        last = this;
    }

    public String getDebugLabel() {
        return "(" + fileName + ", line " + line + ": " + lineContent + ")";
    }

    public String getColoredDebugLabel() {
        return " &6(" + fileName + ", line " + line + ": " + lineContent + ")";
    }

    @Override
    public String toString() {
        return getDebugLabel();
    }

    public Node getNode() {
        return node;
    }

    public int getLine() {
        return line;
    }

    public String getFileName() {
        return fileName;
    }

    public String getLineContent() {
        return lineContent;
    }
}