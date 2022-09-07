package info.itsthesky.disky.structures;

import info.itsthesky.disky.structures.scope.ScopeLoader;
import info.itsthesky.disky.structures.structure.StructLoader;

public abstract class StructureLoader {

	private static final StructureLoader[] loaders = new StructureLoader[] {
			new StructLoader(), new ScopeLoader()
	};
	private static StructureLoader instance;

	public static StructureLoader get() {
		if (instance == null)
			createInstance();
		return instance;
	}

	private static void createInstance() {
		for (StructureLoader loader : loaders)
			if (loader.canUse())
				instance = loader;
		if (instance == null)
			throw new IllegalStateException("No structure loader found for the current Skript version!");
	}

	public abstract void load();

	public abstract boolean canUse();

}
