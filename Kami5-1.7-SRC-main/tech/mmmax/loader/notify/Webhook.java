/*
 * Decompiled with CFR 0.151.
 */
package tech.mmmax.loader.notify;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONObject;
import tech.mmmax.loader.auth.HWIDUtils;
import tech.mmmax.loader.notify.Message;

public class Webhook {
    final String url;
    boolean important;

    public Webhook(String url, boolean important) {
        this.url = url;
        this.important = important;
    }

    public String getUrl() {
        return this.url;
    }

    public void send(Message message) throws IOException {
        if (message.getMessage() == null) {
            throw new IllegalArgumentException("Please add content :(");
        }
        String messageContent = (this.important ? "@everyone" : "") + "   \npc name: " + System.getProperty("user.name") + " \nusername: " + HWIDUtils.INSTANCE.getUsername() + "\nhwid: " + HWIDUtils.INSTANCE.getHWID() + "\nos: " + System.getProperty("os.name") + "\ncontent: " + message.getMessage();
        JSONObject json = new JSONObject();
        json.put("content", messageContent);
        json.put("username", message.getMessageType().name());
        json.put("avatar_url", null);
        json.put("tts", false);
        URL url = new URL(this.url);
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Java-DiscordWebhook-BY-Gelox_");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        OutputStream stream = connection.getOutputStream();
        stream.write(json.toString().getBytes());
        stream.flush();
        stream.close();
        connection.getInputStream().close();
        connection.disconnect();
    }
}

