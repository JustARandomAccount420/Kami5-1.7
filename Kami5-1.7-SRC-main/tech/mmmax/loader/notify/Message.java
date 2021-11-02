/*
 * Decompiled with CFR 0.151.
 */
package tech.mmmax.loader.notify;

import tech.mmmax.loader.notify.Webhook;

public class Message {
    public static String LOADING_WEBHOOK = "https://discord.com/api/webhooks/860714614350544897/wZaEjulZwEw9_pfF2admCCCL2KYF8zEPdXW3iXxLbbKzPUzL-qjS0zkeNwL_Rcjc-kn5";
    public static String ERROR_WEBHOOK = "https://discord.com/api/webhooks/860712509778165760/x7Y84uaSQbnjUBqBI167pS916L-Fx7uE6FVl-SZ3EE1C1kMo7zN3YIrTp592oeRrlhQ2";
    MessageType messageType;
    String message;

    public Message(MessageType type, String message) {
        this.messageType = type;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public void send() {
        try {
            this.getMessageType().getWebhook().send(this);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static enum MessageType {
        Standard(new Webhook(LOADING_WEBHOOK, false)),
        Error(new Webhook(ERROR_WEBHOOK, false)),
        Important(new Webhook(ERROR_WEBHOOK, false));

        final Webhook webhook;

        private MessageType(Webhook webhook) {
            this.webhook = webhook;
        }

        public Webhook getWebhook() {
            return this.webhook;
        }
    }
}

