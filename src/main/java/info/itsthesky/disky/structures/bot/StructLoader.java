package info.itsthesky.disky.structures.bot;

import ch.njol.skript.Skript;
import ch.njol.skript.util.Version;

public class StructLoader extends StructureLoader {
	@Override
	public void load() {
		StructBot.register();
	}

	@Override
	public boolean canUse() {
		return Skript.getVersion().isLargerThan(new Version(2, 6, 3));
	}
}
