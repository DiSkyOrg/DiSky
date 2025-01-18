package net.itsthesky.disky.api.emojis;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class HttpUtils {
	public static final OkHttpClient CLIENT = new OkHttpClient.Builder().build();

	@NotNull
	public static String getPageBody(String url) throws IOException {
		final Call call = CLIENT.newCall(new Request.Builder()
				.url(url)
				.build());

		String page;
		try (Response response = call.execute()) {
			if (response.isSuccessful() || response.isRedirect()) {
				page = response.body().string();
			} else {
				throw new IOException("Response code: " + response.code());
			}
		}

		return page;
	}

	public static String getPageName(String url) {
		return url.substring(url.indexOf("://") + 3);
	}

	public static void shutdown() {
		CLIENT.dispatcher().executorService().shutdownNow();
		CLIENT.connectionPool().evictAll();
	}
}
