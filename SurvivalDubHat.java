package com.survivaldub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SurvivalDubHat extends JavaPlugin {
  public static Permission permission;
  public static final String NOPERM = ChatColor.DARK_RED + "§8[§aSurvivalDub§8] §bNo tienes acceso a los sombreros, compra el paquete §6[VIP] §ben §ahttp://tienda.survivaldub.com";
  private static final Logger log = Logger.getLogger("Minecraft");
  
  public void onEnable()
  {
    if (!setupPermissions().booleanValue())
    {
      log.info("SurvivalDubHat requiere Vault. Descargue la versión más reciente de http://dev.bukkit.org/bukkit-plugins/vault/");
      getServer().getPluginManager().disablePlugin(this);
    }
  }
  
  private Boolean setupPermissions()
  {
    if (getServer().getPluginManager().getPlugin("Vault") != null)
    {
      RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
      if (permissionProvider != null) {
        permission = (Permission)permissionProvider.getProvider();
      }
    }
    return Boolean.valueOf(permission != null);
  }
  
  boolean checkPermission(CommandSender sender, String nodes)
  {
    if ((sender instanceof ConsoleCommandSender)) {
      return nodes.startsWith("SurvivalDubHat.hat.give.");
    }
    if ((sender instanceof Player))
    {
      Player player = (Player)sender;
      return permission.has(player, nodes);
    }
    return false;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
  {
    Player player = null;
    if ((sender instanceof Player)) {
      player = (Player)sender;
    }
    if ((commandLabel.equalsIgnoreCase("hat")) || (commandLabel.equalsIgnoreCase("SurvivalDubHat")))
    {
      if (args.length == 1)
      {
        if (args[0].equals("help"))
        {
          showHelp(command.getName().toLowerCase(), sender);
        }
        else if (args[0].startsWith("ver"))
        {
          sender.sendMessage(getDescription().getFullName());
        }
        else if (checkPermission(sender, SurvivalDubHatPerm.HAT_ITEMS.node))
        {
          ItemStack stack = stackFromString(args[0], 0);
          if ((stack == null) || (stack.getTypeId() > 255) || (stack.getTypeId() < 1))
          {
            sender.sendMessage(ChatColor.RED + args[0] + " no es un bloque valido");
            return true;
          }
          placeOnHead(player, stack);
        }
        else
        {
          sender.sendMessage(NOPERM);
        }
      }
      else if (args.length == 2)
      {
        if (checkPermission(sender, SurvivalDubHatPerm.HAT_GIVE_PLAYERS_ITEMS.node))
        {
          ItemStack stack = stackFromString(args[1], 0);
          if ((stack == null) || (stack.getTypeId() > 255) || (stack.getTypeId() < 1))
          {
            sender.sendMessage(ChatColor.RED + args[1] + " no es un bloque valido");
            return true;
          }
          List<Player> players = getServer().matchPlayer(args[0]);
          if (players.size() < 1)
          {
            sender.sendMessage(ChatColor.RED + "No se pudo encontrar el jugador");
          }
          else if (players.size() > 1)
          {
            sender.sendMessage(ChatColor.RED + "Mas de un jugador encontrado");
            String msg = "";
            for (Iterator<Player> localIterator = players.iterator(); localIterator.hasNext();)
            {
              Player other = (Player)localIterator.next();
              msg = msg + " " + other.getName();
            }
            sender.sendMessage(msg.trim());
          }
          else
          {
            Player other1 = (Player)players.get(0);
            placeOnHead(other1, stack);
            sender.sendMessage("Poner un bloque en la cabeza de " + other1.getName());
          }
        }
        else
        {
          sender.sendMessage(NOPERM);
        }
      }
      else if ((args.length > 2) && (args[0].equalsIgnoreCase("group")))
      {
        if (checkPermission(sender, SurvivalDubHatPerm.HAT_GIVE_GROUPS_ITEMS.node))
        {
          ItemStack stack = stackFromString(args[2], 0);
          if ((stack == null) || (stack.getTypeId() > 255) || (stack.getTypeId() < 1))
          {
            sender.sendMessage(ChatColor.RED + args[2] + " No es un bloque valido");
            return true;
          }
          List<Player> players = new ArrayList();
          for (Player player2 : Bukkit.getOnlinePlayers()) {
            if (permission.playerInGroup(player2, args[1])) {
              players.add(player2);
            }
          }
          if (players.size() < 1)
          {
            sender.sendMessage(ChatColor.RED + "No se pudo encontrar a ningún jugador en " + args[1]);
          }
          else
          {
            for (Player other2 : players) {
              placeOnHead(other2, stack);
            }
            sender.sendMessage("Poner los bloques en los jugadores en las cabezas" + args[1]);
          }
        }
        else
        {
          sender.sendMessage(NOPERM);
        }
      }
      else if (checkPermission(sender, SurvivalDubHatPerm.HAT.node)) {
        placeOnHead(player, player.getItemInHand());
      } else {
        sender.sendMessage(NOPERM);
      }
    }
    else if (commandLabel.equalsIgnoreCase("unhat")) {
      if (checkPermission(sender, SurvivalDubHatPerm.UNHAT.node))
      {
        ItemStack item = new ItemStack(0);
        placeOnHead(player, item);
      }
      else
      {
        sender.sendMessage(NOPERM);
      }
    }
    return true;
  }
  
  private void showHelp(String cmd, CommandSender sender)
  {
    ChatColor nm = ChatColor.BLUE;
    ChatColor ch = ChatColor.LIGHT_PURPLE;
    ChatColor cc = ChatColor.WHITE;
    ChatColor cd = ChatColor.GOLD;
    
    ChatColor ct = ChatColor.YELLOW;
    sender.sendMessage(ch + getDescription().getFullName());
    sender.sendMessage(cc + "/" + cmd + " help " + cd + "-" + ct + " Muestra la Ayuda del menú");
    sender.sendMessage(cc + "/" + cmd + " version " + cd + "-" + ct + " Muestra la versión actual");
    if (checkPermission(sender, "SurvivalDubHat.hat")) {
      sender.sendMessage(cc + "/" + cmd + " " + cd + "-" + ct + " Pone el tema que actualmente ocupa en la cabeza");
    }
    if (checkPermission(sender, "SurvivalDubHat.hat.items")) {
      sender.sendMessage(cc + "/" + cmd + " [block] " + cd + "-" + ct + " Pone un bloque con id bloque en la cabeza");
    }
    if (checkPermission(sender, "SurvivalDubHat.hat.give.players.items")) {
      sender.sendMessage(cc + "/" + cmd + " [player] [block] " + cd + "-" + ct + " Pone un bloque sobre otro jugador");
    }
    if (checkPermission(sender, "SurvivalDubHat.hat.give.groups.items")) {
      sender.sendMessage(cc + "/" + cmd + " group [group] [block] " + cd + "-" + ct + " Coloca los bloques de todos los jugadores en ese grupo");
    }
    sender.sendMessage(cd + "-" + ct + "§8[§aSurvivalDub§8] §bPara quitar un sombrero, acaba de tomar y eliminar desde el punto de casco en su inventario");
    sender.sendMessage(cd + "-" + ct + " Identificadores de elementos de sombrero válidos son 1-255");
    sender.sendMessage(nm + "Gracias por usar SurvivalDubHat, espero que os guste!");
    sender.sendMessage(cd + "======================" + ch + "Extensiones" + cd + "======================");
    sender.sendMessage(cd + "-" + cc + " GlowHat:" + ct + " Añade la posibilidad de que vuestro sombrero brille!");
  }
  
  private boolean placeOnHead(Player player, ItemStack item)
  {
    PlayerInventory inv = player.getInventory();
    ArrayList<Integer> validTypes = new ArrayList();
    validTypes.add(Integer.valueOf(298));
    validTypes.add(Integer.valueOf(302));
    validTypes.add(Integer.valueOf(306));
    validTypes.add(Integer.valueOf(310));
    validTypes.add(Integer.valueOf(314));
    int id = item.getTypeId();
    String itemName = item.getType().name().toLowerCase();
    if (((id < 0) || (id > 255)) && (!validTypes.contains(Integer.valueOf(id))))
    {
      player.sendMessage(ChatColor.RED + "No se puede poner " + itemName + " en la cabeza!!");
      return false;
    }
    ItemStack helmet = inv.getHelmet();
    ItemStack hat = new ItemStack(item.getType(), item.getAmount() < 0 ? item.getAmount() : 1, item.getDurability());
    if (item.getEnchantments() != null) {
      hat.addEnchantments(item.getEnchantments());
    }
    if (item.hasItemMeta()) {
      hat.setItemMeta(item.getItemMeta());
    }
    MaterialData data = item.getData();
    if (data != null) {
      hat.setData(item.getData());
    }
    inv.setHelmet(hat);
    if (item.getAmount() > 1) {
      item.setAmount(item.getAmount() - 1);
    } else {
      inv.removeItem(new ItemStack[] { item });
    }
    if ((helmet != null) && (helmet.getAmount() > 0) && (checkPermission(player, SurvivalDubHatPerm.HAT_RETURN.node)))
    {
      HashMap<Integer, ItemStack> leftover = inv.addItem(new ItemStack[] { helmet });
      if (!leftover.isEmpty())
      {
        player.sendMessage("§8[§aSurvivalDub§8] §bEra incapaz de poner el viejo sombrero de lejos, dejándolo caer a tus pies");
        for (Entry<Integer, ItemStack> e : leftover.entrySet()) {
          player.getWorld().dropItem(player.getLocation(), (ItemStack)e.getValue());
        }
      }
    }
    if (id == 0) {
      player.sendMessage("§8[§aSurvivalDub§8] §bUsted no lleva un sombrero!");
    } else {
      player.sendMessage("§8[§aSurvivalDub§8] §bDisfruta de tu nuevo sombrero " + itemName + "");
    }
    return true;
  }
  
  public ItemStack stackFromString(String item, int count)
  {
    String itemType = item;
    Byte data = Byte.valueOf((byte)0);
    Short dmg = Short.valueOf((short)0);
    if (item.contains(":"))
    {
      String[] sp = item.split(":");
      try
      {
        data = Byte.valueOf(Byte.parseByte(sp[1]));
      }
      catch (NumberFormatException nf) {}
      itemType = sp[0];
    }
    Material material = Material.matchMaterial(itemType);
    if (material == null) {
      return null;
    }
    ItemStack itemStack = new ItemStack(material, count, dmg.shortValue(), data);
    return itemStack;
  }
}
