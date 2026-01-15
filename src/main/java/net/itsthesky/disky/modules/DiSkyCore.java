package net.itsthesky.disky.modules;

import net.itsthesky.disky.DiSky;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.common.properties.PropertiesModule;
import org.skriptlang.skript.util.ClassLoader;

import java.io.IOException;

public class DiSkyCore implements AddonModule {

    @Override
    public void load(SkriptAddon addon) {

        final var loader = ClassLoader.builder()
                .basePackage("net.itsthesky.disky.elements")
                .deep(true)
                .initialize(true)
                .build();
        loader.loadClasses(DiSky.class);
    }

    @Override
    public String name() {
        return "disky_core";
    }
}
