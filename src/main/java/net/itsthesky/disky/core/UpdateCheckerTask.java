package net.itsthesky.disky.core;

import ch.njol.skript.util.Version;
import com.google.gson.Gson;
import net.itsthesky.disky.DiSky;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UpdateCheckerTask extends BukkitRunnable {

    public static VersionState STATE = VersionState.UNKNOWN;

    private static final String VERSIONS_URL =
            "https://api.modrinth.com/v2/project/disky/version";
    private static final Gson GSON = new Gson();

    private DiSky plugin;

    public UpdateCheckerTask(DiSky plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            plugin.getLogger().info("Checking for updates...");
            final var request = HttpRequest.newBuilder()
                    .uri(new URI(VERSIONS_URL))
                    .GET()
                    .build();

            final var client = HttpClient.newHttpClient();
            final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                plugin.getLogger().warning("Failed to check for updates: " + response.statusCode());
                return;
            }

            final var body = response.body();
            final var versions = GSON.fromJson(body, ModrinthVersion[].class);
            if (versions.length == 0) {
                plugin.getLogger().warning("No versions found.");
                return;
            }

            final var latestVersion = versions[0];
            final var latestVersionNumber = new Version(latestVersion.version_number);

            final var currentVersion = DiSky.getVersion();
            if (currentVersion.equals(latestVersionNumber)) {
                plugin.getLogger().info("You are using the latest version of DiSky: " + currentVersion);
                STATE = VersionState.LATEST;
            } else {
                plugin.getLogger().warning("A new version of DiSky is available: " + latestVersionNumber);
                plugin.getLogger().warning("You are using: " + currentVersion);
                plugin.getLogger().warning("Download latest here:" +
                        " https://modrinth.com/plugin/disky/versions/" + latestVersion.id);
                STATE = VersionState.OUTDATED;
            }

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
            for (StackTraceElement element : e.getStackTrace())
                plugin.getLogger().warning("  at " + element);
        }
    }

    private static class ModrinthVersion {

        String id;
        String version_number;

    }

    public enum VersionState {
        OUTDATED,
        LATEST,
        UNKNOWN
    }
}
