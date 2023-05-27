package info.itsthesky.disky.elements.properties.members;

import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.core.Debug;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class MemberFlags extends MultipleMemberProperty<Member.MemberFlag> {

	static {
		register(
				MemberFlags.class,
				Member.MemberFlag.class,
				"[discord] flag[s]"
		);
	}

	@Override
	protected Member.MemberFlag[] convert(Member member) {
		return member.getFlags().toArray(new Member.MemberFlag[0]);
	}

	@Override
	public @NotNull Class<? extends Member.MemberFlag> getReturnType() {
		return Member.MemberFlag.class;
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "discord flags";
	}

	@Override
	public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
		if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE)
			return new Class[] {Member.MemberFlag.class};
		return super.acceptChange(mode);
	}

	@Override
	public void change(@NotNull Event e, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
		if (!EasyElement.isValid(delta))
			return;

		final Member.MemberFlag flag = (Member.MemberFlag) delta[0];
		final Member member = EasyElement.parseSingle(getExpr(), e);
		if (!flag.isModifiable()) {
			Debug.debug(this, Debug.Type.INCOMPATIBLE_TYPE, "The flag " + flag.name() + " is not modifiable!");
			return;
		}

		switch (mode) {
			case ADD:
				member.getFlags().add(flag);
				break;
			case REMOVE:
				member.getFlags().remove(flag);
				break;
			case SET:
			case REMOVE_ALL:
			case DELETE:
			case RESET:
				Debug.debug(this, Debug.Type.INVALID_STATE, "Invalid change mode for member flags: " + mode.name());
				break;
		}
	}
}
