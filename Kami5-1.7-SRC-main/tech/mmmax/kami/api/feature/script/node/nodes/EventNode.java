/*
 * Decompiled with CFR 0.151.
 */
package tech.mmmax.kami.api.feature.script.node.nodes;

import java.awt.Color;
import java.util.Map;
import tech.mmmax.kami.api.feature.script.node.Node;

public class EventNode<Type>
extends Node<Type> {
    public EventNode(String name) {
        super(name);
    }

    @Override
    public Type runNode(Map<Integer, Object> args) {
        return null;
    }

    @Override
    public Color getColor() {
        return new Color(210, 96, 0);
    }
}

