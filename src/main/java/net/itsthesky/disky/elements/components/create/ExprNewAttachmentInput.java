package net.itsthesky.disky.elements.components.create;

/*
 * DiSky
 * Copyright (C) 2025 ItsTheSky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
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

@Name("New Attachment Upload Input")
@Description({"Create a new attachment upload component for modals.",
		"This allows users to upload files through modal interactions.",
		"The component must be wrapped in a label to be added to a modal.",
		"Use the 'attachment value' expression to retrieve uploaded files in modal receive events."})
@Examples({"set {_input} to new attachment upload with id \"user_avatar\"",
		"set {_label} to new label with label \"Upload your avatar\" with component {_input}",
		"add {_label} to rows of {_modal}"})
@Since("4.27.0")
public class ExprNewAttachmentInput extends SimpleExpression<AttachmentUpload> {

	static {
		Skript.registerExpression(
				ExprNewAttachmentInput.class,
				AttachmentUpload.class,
				ExpressionType.COMBINED,
				"[a] [new] attachment[( |-)]upload [with] [the] [id] %string%"
		);
	}

	private Expression<String> exprId;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		exprId = (Expression<String>) exprs[0];
		return true;
	}

	@Override
	protected AttachmentUpload @NotNull [] get(@NotNull Event e) {
		final String id = EasyElement.parseSingle(exprId, e, null);
		if (EasyElement.anyNull(this, id))
			return new AttachmentUpload[0];

		final var upload = AttachmentUpload.of(id);
		return new AttachmentUpload[] {upload};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<AttachmentUpload> getReturnType() {
		return AttachmentUpload.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "new attachment upload with id " + exprId.toString(e, debug);
	}

}
