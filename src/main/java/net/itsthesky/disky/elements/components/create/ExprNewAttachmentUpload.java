package net.itsthesky.disky.elements.components.create;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.components.attachmentupload.AttachmentUpload;
import net.itsthesky.disky.api.skript.EasyElement;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprNewAttachmentUpload extends SimpleExpression<AttachmentUpload.Builder> {

	static {
		DiSkyRegistry.registerExpression(
				ExprNewAttachmentUpload.class,
                AttachmentUpload.Builder.class,
				ExpressionType.COMBINED,
				"[a] [new] [(:required)] attachment upload [input] [with] [the] [id] %string%"
		);
	}

	private boolean required;
	private Expression<String> exprId;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		required = parseResult.hasTag("required");
		exprId = (Expression<String>) exprs[0];
		return true;
	}

	@Override
	protected AttachmentUpload.Builder @NotNull [] get(@NotNull Event e) {
		final String id = EasyElement.parseSingle(exprId, e, null);
		if (EasyElement.anyNull(this, id))
			return new AttachmentUpload.Builder[0];

        final var input = AttachmentUpload.create(id)
                .setRequired(required);
		return new AttachmentUpload.Builder[] {input};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<AttachmentUpload.Builder> getReturnType() {
		return AttachmentUpload.Builder.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "new " + (required ? "required " : "") + "attachment upload with id " + exprId.toString(e, debug);
	}

}
