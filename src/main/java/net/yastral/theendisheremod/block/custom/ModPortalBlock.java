package net.yastral.theendisheremod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yastral.theendisheremod.TheEndIsHereMod;
import net.yastral.theendisheremod.block.ModBlocks;
import net.yastral.theendisheremod.worldgen.dimension.ModDimensions;
import net.yastral.theendisheremod.worldgen.portal.ModTeleporter;

import java.util.*;

@Mod.EventBusSubscriber(modid = TheEndIsHereMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModPortalBlock extends Block {

    private static final Map<UUID, BlockPos> playerPortalMap = new HashMap<>();
    private static final Map<UUID, GameType> previousGameModes = new HashMap<>();
    // Random per generare coordinate casuali al ritorno
    private static final Random random = new Random();

    public ModPortalBlock(Properties pProperties) {
        super(pProperties);
        // Registra gli eventi
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.canChangeDimensions()) {
            // Salva la posizione del portale prima del teletrasporto
            if (pLevel instanceof ServerLevel && pPlayer instanceof ServerPlayer) {
                playerPortalMap.put(pPlayer.getUUID(), pPos);

                // Salva la gamemode corrente del giocatore prima di entrare nel portale
                if (pPlayer.level().dimension() != ModDimensions.VOID_LEVEL_KEY) {
                    previousGameModes.put(pPlayer.getUUID(), ((ServerPlayer) pPlayer).gameMode.getGameModeForPlayer());
                }
            }

            handleVoidPortal(pPlayer, pPos);
            pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.CONSUME;
        }
    }

    private void handleVoidPortal(Entity player, BlockPos pPos) {
        if (player.level() instanceof ServerLevel serverlevel) {
            MinecraftServer minecraftserver = serverlevel.getServer();
            ResourceKey<Level> resourcekey = player.level().dimension() == ModDimensions.VOID_LEVEL_KEY ?
                    Level.OVERWORLD : ModDimensions.VOID_LEVEL_KEY;

            ServerLevel portalDimension = minecraftserver.getLevel(resourcekey);
            if (portalDimension != null && !player.isPassenger()) {
                if (resourcekey == ModDimensions.VOID_LEVEL_KEY) {
                    // Cambia in modalità adventure prima di entrare nella dimensione void
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.setGameMode(GameType.ADVENTURE);
                        serverPlayer.sendSystemMessage(Component.literal("Sei entrato nella dimensione void. Modalità di gioco: ADVENTURE"));
                    }

                    player.changeDimension(portalDimension, new ModTeleporter(pPos, true));
                    player.teleportTo(130, 4, 130);
                    player.lerpHeadTo(-90, 1);
                } else {
                    // Cambia in modalità survival quando torna all'overworld
                    if (player instanceof ServerPlayer serverPlayer) {
                        // Ripristina la gamemode precedente o usa survival come default
                        GameType previousGameMode = previousGameModes.getOrDefault(player.getUUID(), GameType.SURVIVAL);
                        serverPlayer.setGameMode(previousGameMode);
                        serverPlayer.sendSystemMessage(Component.literal("Sei tornato nell'overworld. Modalità di gioco: " + previousGameMode.getName().toUpperCase()));

                        // Rimuovi dalla mappa delle gamemode
                        previousGameModes.remove(player.getUUID());


                        ServerLevel overworld = serverPlayer.getServer().getLevel(Level.OVERWORLD);
                        BlockPos safePos = findSafePlaceInOverworld(serverPlayer.getServer());
                        serverPlayer.teleportTo(overworld, safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5, serverPlayer.getYRot(), serverPlayer.getXRot());
                    }

                    portalDimension.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
                    player.changeDimension(portalDimension, new ModTeleporter(pPos, false));


                }
            }
        }
    }


    private static BlockPos findSafePlaceInOverworld(MinecraftServer server) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) return new BlockPos(0, 64, 0);

        int x = random.nextInt(1000);
        int z = random.nextInt(1000);

        // Trova l'altezza sicura (blocco solido sotto e almeno 2 blocchi di aria sopra)
        int y = overworld.getSeaLevel(); // Inizia dal livello del mare
        boolean foundSafeSpot = false;

        // Cerca dall'alto verso il basso
        for (int checkY = 255; checkY > 0; checkY--) {
            BlockPos pos = new BlockPos(x, checkY, z);
            BlockPos posBelow = pos.below();
            BlockPos posAbove1 = pos.above();
            BlockPos posAbove2 = posAbove1.above();

            // Controlla se il blocco corrente e quello sopra sono aria
            // e se il blocco sottostante è solido
            if (overworld.getBlockState(pos).isAir() &&
                    overworld.getBlockState(posAbove1).isAir() &&
                    !overworld.getBlockState(posBelow).isAir() &&
                    overworld.getBlockState(posBelow).isSolidRender(overworld, posBelow)) {

                y = checkY;
                foundSafeSpot = true;
                break;
            }
        }

        // Se non è stato trovato un posto sicuro, usa il livello del mare
        if (!foundSafeSpot) {
            y = overworld.getSeaLevel();
        }

        return new BlockPos(x, y, z);
    }


    /**
     * Evento che gestisce il cambio di dimensione
     */
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        // Controlla se il giocatore è entrato nella dimensione void
        if (event.getTo() == ModDimensions.VOID_LEVEL_KEY) {
            Player player = event.getEntity();

            // Manda un messaggio al giocatore
            player.sendSystemMessage(Component.literal("Sei entrato in una dimensione speciale. Non potrai rompere blocchi qui."));
        }else if(event.getTo() == Level.OVERWORLD){
            Player player = event.getEntity();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.setGameMode(GameType.SURVIVAL);
                player.sendSystemMessage(Component.literal("W la figa dell'erika"));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

        ServerLevel overworld = serverPlayer.server.getLevel(Level.OVERWORLD);
        if (overworld != null && serverPlayer.level() != overworld) {
            serverPlayer.teleportTo(overworld, overworld.getSharedSpawnPos().getX(),
                    overworld.getSharedSpawnPos().getY(),
                    overworld.getSharedSpawnPos().getZ(), serverPlayer.getYRot(), serverPlayer.getXRot());
        }

        serverPlayer.setGameMode(GameType.SURVIVAL);
        serverPlayer.sendSystemMessage(Component.literal("Sei morto coglione"));
    }
}