package com.shimizuhaku.apeiron.network;

import com.shimizuhaku.apeiron.capability.CapabilityRegistry;
import com.shimizuhaku.apeiron.item.WoodenFluteItem;
import com.shimizuhaku.apeiron.menu.InstrumentMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

/**
 * クライアント側でキーバインドが押された時にサーバーへ送るパケット。
 * メインハンド・オフハンドのうち、楽器を持っている方を対象にGUIを開く。
 */
public class OpenInstrumentMenuPacket {

    private final InteractionHand hand;

    public OpenInstrumentMenuPacket(InteractionHand hand) {
        this.hand = hand;
    }

    public static void encode(OpenInstrumentMenuPacket packet, FriendlyByteBuf buf) {
        buf.writeEnum(packet.hand);
    }

    public static OpenInstrumentMenuPacket decode(FriendlyByteBuf buf) {
        return new OpenInstrumentMenuPacket(buf.readEnum(InteractionHand.class));
    }

    public static void handle(OpenInstrumentMenuPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            net.minecraft.server.level.ServerPlayer player = context.getSender();
            if (player == null) return;

            ItemStack stack = player.getItemInHand(packet.hand);
            if (!(stack.getItem() instanceof WoodenFluteItem)) return;

            boolean hasCapability = stack.getCapability(CapabilityRegistry.INSTRUMENT_DATA).isPresent();
            if (!hasCapability) return;

            NetworkHooks.openScreen(player,
                    new MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            return Component.literal("魔法楽器");
                        }

                        @Override
                        public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player p) {
                            return new InstrumentMenu(containerId, inv, packet.hand);
                        }
                    },
                    buf -> buf.writeEnum(packet.hand));
        });
        context.setPacketHandled(true);
    }
}