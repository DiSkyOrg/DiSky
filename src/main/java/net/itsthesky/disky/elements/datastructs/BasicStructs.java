package net.itsthesky.disky.elements.datastructs;

import net.itsthesky.disky.api.datastruct.EasyDSRegistry;
import net.itsthesky.disky.elements.datastructs.structures.ButtonStructure;
import net.itsthesky.disky.elements.datastructs.structures.EmbedStructure;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.buttons.Button;

public final class BasicStructs {

    static {
        EasyDSRegistry.registerBasicDataStructure(
                EmbedStructure.class,
                EmbedBuilder.class,
                "[a] new embed [builder]",
                "new embed builder embed"
        );
        EasyDSRegistry.registerBasicDataStructure(
                ButtonStructure.class,
                Button.class,
                "[a] new button",
                "new button"
        );
    }

}
