package org.gmail.opfromthestart.dupes.modules;

import meteordevelopment.meteorclient.events.entity.player.InteractEntityEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.gmail.opfromthestart.dupes.DupeAddon;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ItemFrameDupe extends Module {
    public List<ItemFrameEntity> itemFrames = new ArrayList<>();
    public boolean isDuping = false;

    private final Setting<Double> destroyTime = settings.getDefaultGroup().add(new DoubleSetting.Builder()
        .name("destroy-time")
        .description("Delay between placing and removing the item")
        .defaultValue(50)
        .min(1)
        .max(1000)
        .sliderMin(20)
        .sliderMax(200)
        .build()
    );

    public ItemFrameDupe() {
        super(DupeAddon.CATEGORY, "ItemFrameDupe", "Dupes by replacing an item in an item frame.");
        //MeteorClient.EVENT_BUS.subscribe(this);
    }

    @EventHandler
    public void onInteractItemFrame(InteractEntityEvent interactEntityEvent)
    {
        if (!isActive())
            return;
        if (isDuping) {
            return;
        }
        if (interactEntityEvent.entity instanceof ItemFrameEntity) {
                Thread t = new Thread(() -> clearFrame((ItemFrameEntity) interactEntityEvent.entity));
                t.start();
        }
    }

    public void clearFrame(ItemFrameEntity itemFrame) {
        isDuping = true;
        assert MinecraftClient.getInstance().interactionManager != null;
        try {
            Thread.sleep(destroyTime.get().longValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ClientPlayerInteractionManager c = MinecraftClient.getInstance().interactionManager;
        PlayerEntity p = MinecraftClient.getInstance().player;

        if (itemFrame.getHeldItemStack().getCount() != 0) {
            //System.out.println("Removing");
            c.attackEntity(p, itemFrame);
        }
        isDuping = false;
        //System.out.println(isDuping);
        if (MinecraftClient.getInstance().mouse.wasRightButtonClicked()) {
            assert p != null;
            if (p.getMainHandStack().getCount() != 0) {
                //System.out.println("Adding");
                c.interactEntity(p, itemFrame, Hand.MAIN_HAND);
            }
        }
    }
}
