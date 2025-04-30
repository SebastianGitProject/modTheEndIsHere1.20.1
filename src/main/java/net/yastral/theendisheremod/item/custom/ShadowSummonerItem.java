package net.yastral.theendisheremod.item.custom;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.yastral.theendisheremod.entity.fakeplayer.ModFakePlayer;

import java.util.UUID;

public class ShadowSummonerItem extends Item {
    public ShadowSummonerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.sendSystemMessage(Component.literal("diocane 1"));
        ItemStack itemstack = player.getItemInHand(hand);

        if (!level.isClientSide() && level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel) level;

            ModFakePlayer fakePlayer = new ModFakePlayer(EntityType.VILLAGER, serverLevel);
            fakePlayer.setPos(player.getX(), player.getY(), player.getZ()); // Posiziona l'entità
            fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STONE));
            System.out.println("FakePlayer ItemStack: " + fakePlayer.getMainHandItem());

            serverLevel.addFreshEntity(fakePlayer);
            //Chicken chicken = new Chicken(EntityType.CHICKEN, serverLevel);
            //Player shadowPlayer = new Player(EntityType.PLAYER, serverLevel);
            //chicken.setPos(player.getX(), player.getY(), player.getZ());
            //serverLevel.addFreshEntity(chicken);
        }

        return InteractionResultHolder.success(itemstack);
    }

}
