/*
 * Decompiled with CFR 0.151.
 */
package org.json.simple.parser;

import java.util.List;
import java.util.Map;

public interface ContainerFactory {
    public Map createObjectContainer();

    public List creatArrayContainer();
}

