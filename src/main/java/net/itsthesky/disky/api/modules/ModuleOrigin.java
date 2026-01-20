package net.itsthesky.disky.api.modules;

import net.itsthesky.disky.DiSky;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.docs.Origin;

public class ModuleOrigin implements Origin.AddonOrigin {

    private DiSkyModule module;

    public ModuleOrigin(@NotNull DiSkyModule module) {
        this.module = module;
    }

    @Override
    public SkriptAddon addon() {
        return DiSky.getAddonInstance();
    }

    @Override
    public String name() {
        return module.getModuleInfo().name + " [DiSky Module]";
    }

}
