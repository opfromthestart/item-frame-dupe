package org.gmail.opfromthestart.dupes.modules;

import meteordevelopment.meteorclient.events.entity.player.InteractEntityEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.profiling.jfr.event.PacketReceivedEvent;
import org.gmail.opfromthestart.dupes.DupeAddon;

import java.util.ArrayList;
import java.util.List;

public class ItemFrameDupe extends Module {
    public boolean isDuping = false;

    private final Setting<Double> destroyTime = settings.getDefaultGroup().add(new DoubleSetting.Builder()
        .name("destroy-time")
        .description("Delay between placing and removing the item")
        .defaultValue(50)
        .min(1)
        .max(1000)
        .sliderMin(50)
        .sliderMax(200)
        .build()
    );

    private final Setting<Boolean> alwaysActive = settings.getDefaultGroup().add(new BoolSetting.Builder()
        .name("always-active")
        .description("Try to dupe when right-click is not held.")
        .defaultValue(false)
        .build()
    );

    public ItemFrameDupe() {
        super(DupeAddon.CATEGORY, "ItemFrameDupe", "Dupes by replacing an item in an item frame.");
        //MeteorClient.EVENT_BUS.subscribe(this);
    }

    @Override
    public void onActivate() {
        super.onActivate();
        doItemFrameDupe();
    }

    public boolean getShouldDupe()
    {
        if (!isActive())
            return false;
        if (alwaysActive.get())
            return true;
        return MinecraftClient.getInstance().mouse.wasRightButtonClicked();
    }

    @EventHandler
    public void onInteractItemFrame(InteractEntityEvent interactEntityEvent)
    {
        if (!getShouldDupe())
            return;
        if (isDuping) {
            return;
        }
        if (interactEntityEvent.entity instanceof ItemFrameEntity) {
            Thread t = new Thread(this::doItemFrameDupe);
            t.start();
        }
    }

    public void doItemFrameDupe() {
        isDuping = true;
        ClientPlayerInteractionManager c = MinecraftClient.getInstance().interactionManager;
        PlayerEntity p = MinecraftClient.getInstance().player;
        ClientWorld w = MinecraftClient.getInstance().world;
        assert c != null;
        assert p != null;
        assert w != null;

        List<ItemFrameEntity> itemFrames;
        ItemFrameEntity itemFrame;
        Box box;

        while (getShouldDupe()) {
            try {
                Thread.sleep((long) (destroyTime.get() * 0.5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            box = new Box(p.getEyePos().add(-3, -3, -3), p.getEyePos().add(3, 3, 3));
            itemFrames = w.getEntitiesByClass(ItemFrameEntity.class, box, itemFrameEntity -> true);
            if (itemFrames.isEmpty())
                continue;
            itemFrame = itemFrames.get(0);
            c.interactEntity(p, itemFrame, Hand.MAIN_HAND);
            try {
                Thread.sleep((long) (destroyTime.get() * 0.5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (itemFrame.getHeldItemStack().getCount() > 0) {
                c.attackEntity(p, itemFrame);
                System.out.println(itemFrame.getHeldItemStack().getCount());
                System.out.println(System.currentTimeMillis());
            }
        }
        isDuping = false;
    }
}
