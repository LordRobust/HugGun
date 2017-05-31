package io.nanocode.hugs.commands;

import io.nanocode.hugs.Main;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HugCommand implements CommandExecutor {
    private Main main;

    public HugCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player p = (Player) commandSender;
            p.getInventory().addItem(new ItemStack(Material.getMaterial(main.item_id), 1));
        }

        return true;
    }
}
