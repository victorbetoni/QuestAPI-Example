package net.victorbetoni.quests;

import net.kyori.adventure.text.Component;
import net.victorbetoni.questapi.quest.Quest;
import net.victorbetoni.questapi.quest.Step;
import net.victorbetoni.questapi.quest.metadata.QuestMetadata;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreeperHuntQuest extends Quest {

    public CreeperHuntQuest(UUID holder, int currentStep, Plugin plugin, QuestMetadata metadata) {
        super(holder, currentStep, metadata);
    }

    @Override
    public String id() {
        return "creeper-hunt";
    }

    @Override
    public String name() {
        return "Creeper Hunt";
    }

    @Override
    public List<Step<?>> steps() {

        List<Step<?>> steps = new ArrayList<>();
        Quest parent = this;

        steps.add(new Step<EntityDeathEvent>() {
            public String description() {
                return "Kill 5 creepers (" + (5 - parent.metadata().asInt("KILLED_CREEPERS", 0)) + " left)";
            }

            public Quest parent() {
                return parent;
            }

            @EventHandler
            public void handle(EntityDeathEvent event) {
                System.out.println("MATOOO");
                Player who = event.getEntity().getKiller();
                System.out.println(1);
                if(who == null || !who.getUniqueId().equals(parent.holder())) {
                    return;
                }
                System.out.println(2);
                if(event.getEntity().getType().equals(EntityType.CREEPER)) {
                    System.out.println(3);
                    int currentKilled = parent.metadata().asInt("KILLED_CREEPERS", 0);
                    if(currentKilled + 1 >= 5) {
                        if(finish()) {
                            parent().proceed();
                            QuestMetadata.updateAndMarkDirty(q -> q.upsert("KILLED_CREEPERS", (s1) -> String.valueOf(currentKilled + 1)), this.parent());
                        }
                    } else {
                        QuestMetadata.updateAndMarkDirty(q -> q.upsert("KILLED_CREEPERS", (s1) -> String.valueOf(currentKilled + 1)), this.parent());
                    }
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
