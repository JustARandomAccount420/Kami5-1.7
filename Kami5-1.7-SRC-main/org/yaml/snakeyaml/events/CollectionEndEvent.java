/*
 * Decompiled with CFR 0.151.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

public abstract class CollectionEndEvent
extends Event {
    public CollectionEndEvent(Mark startMark, Mark endMark) {
        super(startMark, endMark);
    }
}

