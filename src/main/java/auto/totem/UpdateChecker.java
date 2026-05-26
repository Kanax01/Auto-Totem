package auto.totem;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import java.net.URI;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {

    private static final String RELEASES_API = "https://api.github.com/repos/Kanax01/Auto-Totem/releases/latest";
    private static final String RELEASES_URL = "https://github.com/Kanax01/Auto-Totem/releases/latest";

    public static void checkAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                String latest = fetchLatestTag();
                if (latest == null) return;

                String current = FabricLoader.getInstance()
                        .getModContainer("auto-totem")
                        .map(c -> c.getMetadata().getVersion().getFriendlyString())
                        .orElse(null);

                if (current == null) return;
                if (normalizeTag(latest).equals(normalizeTag(current))) return;

                Minecraft mc = Minecraft.getInstance();
                mc.execute(() -> {
                    if (mc.player == null) return;

                    Component message = Component.literal("[Auto Totem] ")
                            .withStyle(Style.EMPTY.withColor(0xFFAA00))
                            .append(Component.literal("Update available! ")
                                    .withStyle(Style.EMPTY.withColor(0xFFFFFF)))
                            .append(Component.literal("v" + current)
                                    .withStyle(Style.EMPTY.withColor(0xFF5555)))
                            .append(Component.literal(" -> ")
                                    .withStyle(Style.EMPTY.withColor(0xFFFFFF)))
                            .append(Component.literal("v" + latest)
                                    .withStyle(Style.EMPTY.withColor(0x55FF55)))
                            .append(Component.literal(" [Click to download]")
                                    .withStyle(Style.EMPTY
                                            .withColor(0x55FFFF)
                                            .withUnderlined(true)
                                            .withClickEvent(new ClickEvent.OpenUrl(URI.create(RELEASES_URL)))));

                    mc.player.sendSystemMessage(message);
                });

            } catch (Exception ignored) {}
        });
    }

    private static String fetchLatestTag() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(RELEASES_API).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Accept", "application/vnd.github+json");
            conn.setRequestProperty("User-Agent", "Auto-Totem-Mod");

            if (conn.getResponseCode() != 200) return null;

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }

            String body = sb.toString();
            String key = "\"tag_name\":\"";
            int start = body.indexOf(key);
            if (start == -1) return null;
            start += key.length();
            int end = body.indexOf("\"", start);
            if (end == -1) return null;
            return body.substring(start, end);

        } catch (Exception e) {
            return null;
        }
    }

    private static String normalizeTag(String tag) {
        return tag.replaceAll("(?i)^v", "").trim();
    }
}