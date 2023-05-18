package info.itsthesky.disky.core;

import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.NodeInformation;
import info.itsthesky.disky.api.skript.WaiterEffect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Class to handle the 'developer debug' mode, showing infos related to script's syntax and more.
 */
public final class Debug {

	/**
	 * The type of debug, aka what caused the debug to be called.
	 */
	public enum Type {
		INCOMPATIBLE_TYPE("Incompatible type"),
		EMPTY_VALUE("Null/not set/empty value"),
		EMPTY_LIST("Empty list"),
		INVALID_STATE("Invalid state"),
		CUSTOM("Custom"),
		;

		private final String name;

		Type(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static boolean isEnabled() {
		return DiSky.getInstance().getConfig().getBoolean("developer-debug", false);
	}

	public static void debug(Object from, String name, String message) {
		if (!isEnabled())
			return;
		final NodeInformation nodeInformation = retrieveNode(from);
		if (nodeInformation == null)
			send("&e[DiSky Debug] &7" + name + " &8» &f" + message);
		else
			send("&e[DiSky Debug] &b" + name + " &8» &f" + message + nodeInformation.getColoredDebugLabel());
	}

	public static void debug(Object from, Type type, String message) {
		debug(from, type.getName(), message);
	}

	private static void send(String message) {
		DiSky.getInstance().getServer().getConsoleSender().sendMessage(Utils.colored(message));
	}

	private static NodeInformation retrieveNode(Object from) {
		if (from instanceof WaiterEffect)
			return ((WaiterEffect) from).getNode();

		try {
			final Field[] fields = from.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.getType().equals(NodeInformation.class)) {
					field.setAccessible(true);
					return (NodeInformation) field.get(from);
				}
			}

			final Method[] methods = from.getClass().getDeclaredMethods();
			for (Method method : methods) {
				if (method.getReturnType().equals(NodeInformation.class)) {
					method.setAccessible(true);
					return (NodeInformation) method.invoke(from);
				}
			}

		} catch (Exception ex) {
			return null;
		}
		return null;
	}
}
