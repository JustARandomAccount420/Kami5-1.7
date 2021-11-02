/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package tech.mmmax.kami.api.feature.module;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Map;
import tech.mmmax.kami.api.binds.IBindable;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.management.BindManager;
import tech.mmmax.kami.api.utils.chat.ChatMessage;
import tech.mmmax.kami.api.utils.chat.ChatUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.api.value.custom.Bind;
import tech.mmmax.kami.api.wrapper.IMinecraft;

public class Module
extends Feature
implements IBindable,
IMinecraft {
    Bind bind;
    Value<Boolean> chatNotify = new ValueBuilder().withDescriptor("Chat Notify").withValue(true).register(this);

    public Module(String name, Feature.Category category) {
        super(name, category, Feature.FeatureType.Module);
        this.bind = new Bind();
        BindManager.INSTANCE.getBindables().add(this);
    }

    public Value<?> register(Value<?> value) {
        this.getValues().add(value);
        return value;
    }

    public Bind getBind() {
        return this.bind;
    }

    public void setBind(Bind bind) {
        this.bind = bind;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (this.chatNotify.getValue().booleanValue()) {
            ChatUtils.sendMessage(new ChatMessage("Enabled: " + ChatFormatting.GREEN + ChatFormatting.BOLD + this.getDisplayName(), true, 69420));
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.chatNotify.getValue().booleanValue()) {
            ChatUtils.sendMessage(new ChatMessage("Disabled: " + ChatFormatting.RED + ChatFormatting.BOLD + this.getDisplayName(), true, 69420));
        }
    }

    @Override
    public void load(Map<String, Object> objects) {
        super.load(objects);
        this.bind.setKey((Integer)objects.get("bind"));
    }

    @Override
    public Map<String, Object> save() {
        Map<String, Object> toSave = super.save();
        toSave.put("bind", this.bind.getKey());
        return toSave;
    }

    @Override
    public int getKey() {
        return this.bind.getKey();
    }

    @Override
    public void onKey() {
        this.toggle();
    }
}

