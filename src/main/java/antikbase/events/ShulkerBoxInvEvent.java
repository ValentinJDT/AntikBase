package antikbase.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.HashMap;
import java.util.Map;

public class ShulkerBoxInvEvent implements Listener {

    private Map<Player, ItemStack> maps = new HashMap<>();

    @EventHandler
    public void onInteract(InventoryClickEvent e) {
        if(!hasPermissions((Player) e.getWhoClicked(), "open"))
            return;

        if(e.getInventory().getType() == InventoryType.SHULKER_BOX) {
            updateShulker(e);
        } else {
            openShulker(e);
        }

    }

    public void openShulker(InventoryClickEvent e) {
        if(e.getAction() != InventoryAction.NOTHING)
            return;

        ItemStack itemStack = e.getCurrentItem();

        if(itemStack == null || !itemStack.hasItemMeta() || !(itemStack.getItemMeta() instanceof BlockStateMeta))
            return;

        BlockStateMeta blockStateMeta = (BlockStateMeta) itemStack.getItemMeta();

        if(!(blockStateMeta.getBlockState() instanceof ShulkerBox))
            return;

        ShulkerBox shulker = (ShulkerBox) blockStateMeta.getBlockState();

        if(shulker.getInventory().isEmpty())
            return;

        e.getWhoClicked().openInventory(shulker.getInventory());

        maps.put((Player) e.getWhoClicked(), itemStack);
    }

    public void updateShulker(InventoryClickEvent e) {
        if(!maps.containsKey((Player) e.getWhoClicked()))
            return;

        ItemStack itemStack = maps.get((Player) e.getWhoClicked());
        if(!(itemStack.getItemMeta() instanceof BlockStateMeta))
            return;

        BlockStateMeta blockStateMeta = (BlockStateMeta) itemStack.getItemMeta();
        ShulkerBox shulker = (ShulkerBox) blockStateMeta.getBlockState();

        shulker.getInventory().setContents(e.getWhoClicked().getOpenInventory().getTopInventory().getContents());
        shulker.update();

        blockStateMeta.setBlockState(shulker);
        shulker.update();

        itemStack.setItemMeta(blockStateMeta);

    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if(e.getInventory().getType() != InventoryType.SHULKER_BOX || !maps.containsKey((Player) e.getPlayer()))
            return;

        maps.remove((Player) e.getPlayer());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack itemStack = e.getItemDrop().getItemStack();

        if(!itemStack.getType().name().contains("SHULKER_BOX"))
            return;

        if(!(itemStack.getItemMeta() instanceof BlockStateMeta))
            return;

        if(e.getPlayer().getOpenInventory().getTopInventory().getType() == InventoryType.SHULKER_BOX) {
            e.setCancelled(true);
        }
    }

    private boolean hasPermissions(Player player, String permission) {
        return player.isOp()
                || player.hasPermission("antikbase.shulker.*")
                || player.hasPermission("*")
                || player.hasPermission("antikbase.shulker." + permission);
    }

}
