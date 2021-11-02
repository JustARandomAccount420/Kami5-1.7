/*
 * Decompiled with CFR 0.151.
 */
package tech.mmmax.kami.api.feature.script.node;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public abstract class Node<Type> {
    String name;
    Map<Integer, Object> inputs = new HashMap<Integer, Object>();

    public Node(String name) {
        this.name = name;
    }

    public abstract Type runNode(Map<Integer, Object> var1);

    public abstract Color getColor();

    public String getName() {
        return this.name;
    }
}

