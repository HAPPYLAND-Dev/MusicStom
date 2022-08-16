package me.xiaozhangup.musicstom.event.player;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerStartDiggingEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.map.MapColors;
import net.minestom.server.map.framebuffers.DirectFramebuffer;
import net.minestom.server.tag.Tag;

import java.util.HashMap;

import static me.xiaozhangup.musicstom.MusicStom.instanceContainer;

public class Login {

    public static HashMap<Player, BossBar> bossBarHashMap = new HashMap<>();

    public static void with(GlobalEventHandler globalEventHandler) {
        globalEventHandler.addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 16, 0));
            player.setItemInMainHand(ItemStack.of(Material.WOODEN_SWORD));
            player.setGameMode(GameMode.CREATIVE);
            new Thread(() -> {
                PlayerSkin playerSkin = PlayerSkin.fromUsername(player.getUsername());
                player.setSkin(playerSkin);
            }).start();

            if (bossBarHashMap.get(event.getPlayer()) == null) {
                BossBar bossBar = BossBar.bossBar(Component.text("Break - ").append(event.getEntity().getName()), 0, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
                bossBarHashMap.putIfAbsent(event.getPlayer(), bossBar);
                MinecraftServer.getBossBarManager().addBossBar(event.getPlayer(), bossBar);
            }
        });
        globalEventHandler.addListener(PlayerUseItemEvent.class, event -> {
            if (event.getHand() != Player.Hand.MAIN) return;
            event.getPlayer().sendActionBar(Component.text("PlayerUseItemEvent").color(TextColor.color(53, 232, 175)));
//            Entity human = new Entity(EntityType.WITHER);
//            human.setInstance(instanceContainer, event.getPlayer().getPosition());
//            human.setCustomName(event.getPlayer().getName().color(TextColor.color(148, 73, 235)));
//            human.setCustomNameVisible(true);
//            human.setAutoViewable(true);
//            event.getPlayer().addPassenger(human);

//            Entity human = new Entity(EntityType.ARMOR_STAND);
//            Metadata metadata = new Metadata(human);
//            human.setInstance(instanceContainer, event.getPlayer().getPosition());
//            ArmorStandMeta armorStandMeta = new ArmorStandMeta(human, metadata);
//            armorStandMeta.setCustomNameVisible(true);
//            armorStandMeta.setCustomName(event.getPlayer().getName().color(TextColor.color(148, 73, 235)));
//            armorStandMeta.setSmall(true);
//            armorStandMeta.setHasArms(true);
//            armorStandMeta.setInvisible(true);
//            human.setNoGravity(true);

            Player player = event.getPlayer();
            ItemStack item = ItemStack.builder(Material.FILLED_MAP)
                    .displayName(Component.text("TestMap", NamedTextColor.GREEN))
                    //此处的Tag.Integer("map")就是MapId,10就是MapId的值
                    .build().withTag(Tag.Integer("map"), 10);
            DirectFramebuffer fb = new DirectFramebuffer();
//循环遍历地图上的像素点
            for (int i = 0; i < fb.getColors().length; i++) {
                //为某坐标的像素点添加颜色
                fb.getColors()[i] = MapColors.GOLD.baseColor();
            }
//在(0,0点添加颜色)
            fb.set(0, 0, MapColors.DIAMOND.baseColor());
            player.sendPacket(fb.preparePacket(10));
//向玩家发送数据包
            player.getInventory().addItemStack(item);

        });
        globalEventHandler.addListener(PlayerBlockBreakEvent.class, event -> {
            event.setCancelled(true);
            event.getPlayer().sendActionBar(Component.text("PlayerBlockBreakEvent").color(TextColor.color(53, 232, 175)));
            instanceContainer.setBlock(event.getBlockPosition(), Block.GLASS);
            if ((bossBarHashMap.get(event.getPlayer()).progress() + 0.01f) >= 1f) {
                bossBarHashMap.get(event.getPlayer()).progress(0);
                bossBarHashMap.get(event.getPlayer()).color(BossBar.Color.RED);
            }
            bossBarHashMap.get(event.getPlayer()).progress(bossBarHashMap.get(event.getPlayer()).progress() + 0.01f);
        });
        globalEventHandler.addListener(PlayerStartDiggingEvent.class, event -> {
            event.setCancelled(true);
            instanceContainer.breakBlock(event.getPlayer(), event.getBlockPosition());
        });

    }

}
