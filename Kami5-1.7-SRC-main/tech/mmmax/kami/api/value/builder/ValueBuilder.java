/*
 * Decompiled with CFR 0.151.
 */
package tech.mmmax.kami.api.value.builder;

import java.util.function.Consumer;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.value.Value;

public class ValueBuilder<Type> {
    Value<Type> value = new Value();

    public ValueBuilder<Type> withDescriptor(String name, String tag) {
        this.value.setName(name);
        this.value.setTag(tag);
        return this;
    }

    public ValueBuilder<Type> withDescriptor(String name) {
        this.value.setName(name);
        String camelCase = name.replace(" ", "");
        char[] chars = camelCase.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        camelCase = new String(chars);
        this.value.setTag(camelCase);
        return this;
    }

    public ValueBuilder<Type> withValue(Type value) {
        this.value.setValue(value);
        return this;
    }

    public ValueBuilder<Type> withAction(Consumer<Value<Type>> action) {
        this.value.setAction(action);
        return this;
    }

    public ValueBuilder<Type> withRange(Type min, Type max) {
        this.value.setMin(min);
        this.value.setMax(max);
        return this;
    }

    public ValueBuilder<Type> withModes(String ... modes) {
        this.value.setModes(modes);
        return this;
    }

    public Value<Type> getValue() {
        return this.value;
    }

    public Value<Type> register(Feature feature) {
        feature.getValues().add(this.value);
        return this.value;
    }
}

