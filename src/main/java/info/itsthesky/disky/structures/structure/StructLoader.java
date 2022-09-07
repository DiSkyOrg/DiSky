package info.itsthesky.disky.structures.structure;

import info.itsthesky.disky.structures.StructureLoader;

public class StructLoader extends StructureLoader {
	@Override
	public void load() {
		StructBot.register();
	}

	@Override
	public boolean canUse() {
		return false;
	}
}
