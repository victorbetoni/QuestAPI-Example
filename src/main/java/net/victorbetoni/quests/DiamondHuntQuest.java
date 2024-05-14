package net.victorbetoni.quests;

import net.victorbetoni.questapi.quest.Quest;
import net.victorbetoni.questapi.quest.Step;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
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
    public DiamondHuntQuest(UUID holder, int currentStep, Plugin plugin) {
        super(holder, currentStep);
        this.steps().forEach(x -> Bukkit.getPluginManager().registerEvents(x, plugin));
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
                return "Find a letter inside a chest around [-100, 0, 100]";
            }

            public Quest parent() {
                return parent;
            }

            @EventHandler
            public void handle(PlayerInteractEvent event) {
                if(!event.getPlayer().getUniqueId().equals(parent.holder())){
                    return;
                }
                if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Location loc = event.getClickedBlock().getLocation();
                    if(loc.getBlockX() == -100 && loc.getBlockY() == 0 && loc.getBlockZ() == 100) {
                        parent().proceed();
                    }
                }
            }
        });

        steps.add(new Step<BlockBreakEvent>() {
            public String description() {
                return "Mine the diamond at [-120, 100, 120]";
            }

            public Quest parent() { return parent; }

            @EventHandler
            public void handle(BlockBreakEvent event) {
                if(!event.getPlayer().getUniqueId().equals(parent.holder())){
                    return;
                }
                Location loc = event.getBlock().getLocation();
                if(loc.getBlockX() == -120 && loc.getBlockY() == 100 && loc.getBlockZ() == 120) {
                    parent().proceed();
                }
            }

            @Override
            public List<ItemStack> rewards() {
                return List.of(new ItemStack(Material.IRON_PICKAXE));
            }
        });

        steps.add(new Step<InventoryClickEvent>() {
            public String description() {
                return "Return the mined diamond to the chest at [-100, 0, 100]";
            }

            public Quest parent() { return parent; }

            @EventHandler
            public void handle(InventoryClickEvent event) {
                if(!event.getWhoClicked().getUniqueId().equals(parent.holder())){
                    return;
                }
                if(event.getCursor().getType() == Material.DIAMOND) {
                    if(event.getClickedInventory().getHolder() instanceof Chest chest) {
                        Location loc = chest.getLocation();
                        if(loc.getBlockX() == -120 && loc.getBlockY() == 100 && loc.getBlockZ() == 120) {
                            parent().proceed();
                        }
                    }
                }
            }
        });

        return steps;
    }

    @Override
    public void finish() {

    }
}
