package info.itsthesky.disky.api.modules;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.registration.SyntaxOrigin;

public class ModuleOrigin implements SyntaxOrigin {

    private DiSkyModule module;

    public ModuleOrigin(@NotNull DiSkyModule module) {
        this.module = module;
    }

    @Override
    public String name() {
        return module.getModuleInfo().name + " [DiSky Module]";
    }

}
