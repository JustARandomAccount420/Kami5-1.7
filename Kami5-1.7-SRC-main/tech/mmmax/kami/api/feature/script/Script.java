/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Text
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 */
package tech.mmmax.kami.api.feature.script;

import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tech.mmmax.kami.api.event.MoveEvent;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.feature.script.node.nodes.EventNode;

public class Script
extends Module {
    Map<Integer, EventNode<?>> nodes = new HashMap();

    public Script(String name) {
        super(name, Feature.Category.Scripts);
        this.setType(Feature.FeatureType.Script);
        this.nodes.put(0, new EventNode("Tick Event"));
        this.nodes.put(1, new EventNode("Render2D Event"));
        this.nodes.put(2, new EventNode("Render3D Event"));
        this.nodes.put(3, new EventNode("Packet Event"));
        this.nodes.put(4, new EventNode("Move Event"));
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        HashMap<Integer, Object> args = new HashMap<Integer, Object>();
        args.put(0, event);
        this.nodes.get(0).runNode(args);
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Text event) {
        HashMap<Integer, Object> args = new HashMap<Integer, Object>();
        args.put(0, event);
        this.nodes.get(1).runNode(args);
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        HashMap<Integer, Object> args = new HashMap<Integer, Object>();
        args.put(0, event);
        this.nodes.get(2).runNode(args);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        HashMap<Integer, Object> args = new HashMap<Integer, Object>();
        args.put(0, (Object)event);
        this.nodes.get(3).runNode(args);
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        HashMap<Integer, Object> args = new HashMap<Integer, Object>();
        args.put(0, (Object)event);
        this.nodes.get(4).runNode(args);
    }
}

