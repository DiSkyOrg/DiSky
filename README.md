# DiSky

*** 

## What is DiSky?

DiSky is a **[Skript]()** addon, which allows you to create, manage and edit **Discord**'s bots.

It provides more than **400** different syntax, such as:

* Creating entities (roles, channels, categories, etc.)
* Editing existing entities
* Send advanced message with **embeds**, **components** or **attachments**
* Several's **utilities** syntax to let developer make their bot faster and easier.
* Fully supports **Slash** commands, including option types, sub-commands, groups and auto-completion arguments.
* And much, much more!

## Useful links

* :new: [**Wiki**](https://disky.tech/wiki/)
* [Discord Server](https://forum.itsthesky.info/discord)
* [Documentation](https://disky.tech/docs/)

## Requirements

* Skript **2.6.0** or higher
* Spigot/Paper/Any fork of them **1.13.0** or higher

> **Note:** Event if DiSky is only based on Skript, some features require Bukkit's or Spigot's API. The 1.13+ version is a more recommended version than a requirement.

## Incompatibilities

* DiscordSRV *(it uses an older version of JDA that DiSky is not based on)*
* any other JDA-compiled plugin, that doesn't have JDA 5 as JDA version.

## Small Example

### Bot loading & Slash Commands

> :warning: This has to be put OUTSIDE any triggers! (not inside an 'on load' for example)

```applescript
define new bot named "My Bot":
    token: "ultra-private-token"
    intents: default intents
    on guild ready:
        send "I'm ready to serve %event-guild% sir!" to console
        
        send "Setting up commands..." to console
        set {_cmd} to new slash command named "test" with description "Test command"
        
        add new required string option named "argument" with description "example argument" to options of {_cmd}
        add new integer option named "pls" with description "example optional number argument" to options of {_cmd}
        
        update {_cmd} locally in event-guild
    
    on ready:
        send "Hello world, the bot has been loaded and is ready to use!" to console
```

### Message Components

```applescript
discord command components:
    prefixes: !
    trigger:
        set {_buttons} to new components row
        add new link button with url "http://disky.itsthesky.info/" named "DiSky Website" with reaction "smile" to components of {_buttons}
        add new danger button with url "button-id-here" named "Dangerous!" with reaction "joy" to components of {_buttons}
        add new success button with url "cant-have-two-same-id" named "Green :D" with reaction "disky" to components of {_buttons}
        add new disabled secondary button with url "no-space-allowed-either" named "I'm disabled" with reaction "sob" to components of {_buttons}
        
        set {_dropdownRow} to new components row
        set {_dropdown} to new dropdown with id "unique-id-here-too"

        set min range of {_dropdown} to 1
        set max range of {_dropdown} to 2
        
        add new option with value "value" named "First Choice" with description "The first and NOT default choice of the dropdown" with reaction "smile" to options of {_dropdown}

        add new default option with value "value-2" named "Second Choice" with description "This one is selected by default!" with reaction "wave" to options of {_dropdown}
        add new option with value "value-3" named "Third Choice" with description "You only can select one or two choice, not more!" with reaction "joy" to options of dropdown
        add new option with value "value-4" named "Fourth Choice" with description "And yes, custom emote are also supported :D" with reaction "disky" to options of {_dropdown}
        add new option with value "value-5" named "Fifth Choice" with description "Each option have a name, description and emote." with reaction "rocket" to options of {_dropdown}
        add new option with value "value-6" named "Sixth Choice" with description "Choices? You can have up to 25 per dropdown!" with reaction "zap" to options of {_dropdown}
        add new option with value "value-7" named "Seventh Choice" with description "Also limited with 50 characters per description." with reaction "ice_cube" to options of {_dropdown}

        add {_dropdown} to components of {_dropdownRow}

        reply with "*Components ...*" with components {_buttons} and {_dropdownRow}

on button click:
    set {_id} to event-string # get back the unique ID defined above.
    {_id} is "button-id-here", "cant-have-two-same-id" or "no-space-allowed-either"

    defer the interaction # avoid the 'interaction failed' message

on dropdown click:
    set {_id} to event-string # get back the unique ID defined above.
    {_id} is "unique-id-here-too"

    reply with "Selected values: %selected values%" # replying will defer the interaction
```

## Constantly updated

DiSky aim to be updated **when a new feature is available to Discord**. After some days before it's actually stable, new **elements** will be added into DiSky to provide more functionality.
