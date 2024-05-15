package net.victorbetoni.quests;

import net.kyori.adventure.text.Component;
import net.victorbetoni.questapi.quest.Quest;
import net.victorbetoni.questapi.quest.Step;
import net.victorbetoni.questapi.quest.metadata.QuestMetadata;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DiamondHuntQuest extends Quest {
    public DiamondHuntQuest(UUID holder, int currentStep, Plugin plugin, QuestMetadata metadata) {
        super(holder, currentStep, metadata);
    }

    @Override
    public String id() {
        return "diamond-hunt";
    }

    @Override
    public String name() {
        return "Diamond Hunt";
    }

    @Override
    public List<Step<?>> steps() {

        List<Step<?>> steps = new ArrayList<>();
        Quest parent = this;

        steps.add(new Step<PlayerInteractEvent>() {
            public String description() {
                return "Find a letter inside a chest at [-6, 64, -7]";
            }

            public Quest parent() {
                return parent;
            }

            @EventHandler
            public void handle(PlayerInteractEvent event) {
                Player who = event.getPlayer();
                if(!event.getPlayer().getUniqueId().equals(parent.holder())){
                    return;
                }
                if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Location loc = event.getClickedBlock().getLocation();
                    if(loc.getBlockX() == -7 && loc.getBlockY() == 64 && loc.getBlockZ() == -8) {
                        int slot = who.getInventory().firstEmpty();
                        if(slot == -1) {
                            who.sendMessage(Component.text(ChatColor.RED + "Free one slot in your inventory before proceeding."));
                            event.setCancelled(true);
                            return;
                        }
                        who.getInventory().setItem(slot, new ItemStack(Material.IRON_PICKAXE));
                        event.getPlayer().sendMessage(Component.text("[Quest " + parent.name() + " started...]"));
                        event.getPlayer().playSound(event.getPlayer(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
                        parent().proceed();
                    }
                }
            }
        });

        steps.add(new Step<BlockBreakEvent>() {
            public String description() {
                return "Mine the diamond at [-15, 64, -29]";
            }

            public Quest parent() { return parent; }

            @EventHandler
            public void handle(BlockBreakEvent event) {
                Player who = event.getPlayer();
                if(!who.getUniqueId().equals(parent.holder())){
                    return;
                }
                Location loc = event.getBlock().getLocation();
                if(loc.getBlockX() == -16 && loc.getBlockY() == 64 && loc.getBlockZ() == -30) {
                    if(!finish()) {
                        event.setCancelled(true);
                        return;
                    }
                    proceed();
                }
            }

        });

        return steps;
    }

    @Override
    public boolean finish() {
        Player p = Bukkit.getPlayer(this.holder());
        if(p == null) {
            return false;
        }
        int freeSlot = p.getInventory().firstEmpty();
        if(freeSlot == -1) {
            p.sendMessage(Component.text(ChatColor.RED + "Free one slot in your inventory before finishing the quest!"));
            return false;
        }
        p.sendMessage(Component.text(ChatColor.AQUA + "[Quest " + this.name() + " finished!]"));
        p.sendMessage(Component.text(ChatColor.GREEN + "[+15 Diamonds]"));
        p.getInventory().setItem(freeSlot, new ItemStack(Material.DIAMOND, 15));
        return true;
    }
}
