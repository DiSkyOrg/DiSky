package net.itsthesky.disky.elements.structures.slash.models;

public enum CommandType {
    /**
     * Represents a standalone command with no subcommands or groups
     */
    SINGLE,

    /**
     * Represents a subcommand within a group
     */
    SUB_COMMAND,

    /**
     * Represents a command group that can contain subcommands and subgroups
     */
    GROUP
}