package info.itsthesky.disky.structures.scope;

import ch.njol.skript.Skript;
import info.itsthesky.disky.structures.StructureLoader;

/**
 * Loader for 2.6.3- 'scopes', using tricky ways with SectionValidator, now renamed as structures.
 */
public class ScopeLoader extends StructureLoader {

	@Override
	public boolean canUse() {
		return !Skript.classExists("org.skriptlang.skript.lang.Structure");
	}

	@Override
	public void load() {
		BotScope.register();
	}

}
