package info.itsthesky.disky.api.modules;

import info.itsthesky.disky.api.ReflectionUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converter;

import java.util.Arrays;

/**
 * Copied from Skript, this allows a modifiable value to be passed to a script.
 */
public class EventValueInfo<E extends Event, T> {
		
		public final Class<E> event;
		public final Class<T> c;
		public final Converter<T, E> getter;
		@Nullable
		public final Class<? extends E>[] excludes;
		@Nullable
		public final String excludeErrorMessage;

		public EventValueInfo(Object entity) {
			event = ReflectionUtils.getField(entity.getClass(), entity, "event");
			c = ReflectionUtils.getField(entity.getClass(), entity, "c");
			getter = ReflectionUtils.getField(entity.getClass(), entity, "getter");
			excludes = ReflectionUtils.getField(entity.getClass(), entity, "excludes");
			excludeErrorMessage = ReflectionUtils.getField(entity.getClass(), entity, "excludeErrorMessage");
		}
		
		/**
		 * Get the class that represents the Event.
		 * @return The class of the Event associated with this event value
		 */
		public Class<E> getEventClass() {
			return event;
		}
		
		/**
		 * Get the class that represents Value.
		 * @return The class of the Value associated with this event value
		 */
		public Class<T> getValueClass() {
			return c;
		}
		
		/**
		 * Get the classes that represent the excluded for this Event value.
		 * @return The classes of the Excludes associated with this event value
		 */
		@Nullable
		@SuppressWarnings("null")
		public Class<? extends E>[] getExcludes() {
			if (excludes != null)
				return Arrays.copyOf(excludes, excludes.length);
			return new Class[0];
		}
		
		/**
		 * Get the error message used when encountering an exclude value.
		 * @return The error message to use when encountering an exclude
		 */
		@Nullable
		public String getExcludeErrorMessage() {
			return excludeErrorMessage;
		}
	}