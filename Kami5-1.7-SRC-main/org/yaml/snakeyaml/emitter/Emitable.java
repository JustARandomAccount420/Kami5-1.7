/*
 * Decompiled with CFR 0.151.
 */
package org.yaml.snakeyaml.emitter;

import java.io.IOException;
import org.yaml.snakeyaml.events.Event;

public interface Emitable {
    public void emit(Event var1) throws IOException;
}

