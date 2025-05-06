package pl.pijok.autosell.selling;

import at.pcgamingfreaks.Minepacks.Bukkit.API.Backpack;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pl.pijok.autosell.AutoSell;
import pl.pijok.autosell.Controllers;
import pl.pijok.autosell.essentials.ChatUtils;
import pl.pijok.autosell.essentials.ConfigUtils;
import pl.pijok.autosell.essentials.Debug;
import pl.pijok.autosell.essentials.Utils;
import pl.pijok.autosell.hooks.MinepacksHook;
import pl.pijok.autosell.miner.Miner;
import pl.pijok.autosell.settings.Lang;
import pl.pijok.autosell.settings.Settings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SellingController {

    private final AutoSell plugin;

    private final HashMap<Material, Double> blocksValues;
    private final LinkedHashMap<String, Double> multipliers;
    private final List<Material> sellableMaterials;

    //Filter gui settings
    private String title;
    private ItemStack nextPage;
    private ItemStack previousPage;
    private ItemStack filler;
    private ItemStack bottomFiller;
    private List<Integer> slotsToUse;
    private String toSell;
    private String toIgnore;

    public SellingController(AutoSell plugin){
        this.plugin = plugin;
        blocksValues = new HashMap<>();
        multipliers = new LinkedHashMap<>();
        sellableMaterials = new ArrayList<>();
    }

    public void loadSettings(){

        blocksValues.clear();
        multipliers.clear();
        sellableMaterials.clear();

        YamlConfiguration configuration = ConfigUtils.load("blocksValues.yml");

        //Loading blocks values
        for(String materialName : configuration.getConfigurationSection("blocks").getKeys(false)){
            if(!Utils.isMaterial(materialName)){
                Debug.log("&cWrong material name " + materialName + " in blocksValues.yml -> blocks section");
                continue;
            }

            Material material = Material.valueOf(materialName);

            blocksValues.put(material, configuration.getDouble("blocks." + materialName));
            sellableMaterials.add(material);
        }

        //Loading multipliers
        for(String id : configuration.getConfigurationSection("multipliers").getKeys(false)){
            String permission = configuration.getString("multipliers." + id + ".permission");
            double multiplier = configuration.getDouble("multipliers." + id + ".multiplier");

            multipliers.put(permission, multiplier);
        }
    }

    public void loadGui(){
        YamlConfiguration configuration = ConfigUtils.load("filterGui.yml");

        title = configuration.getString("title");
        nextPage = ConfigUtils.getItemstack(configuration, "nextPage");
        previousPage = ConfigUtils.getItemstack(configuration, "previousPage");
        filler = ConfigUtils.getItemstack(configuration, "filler");
        bottomFiller = ConfigUtils.getItemstack(configuration, "bottomFiller");
        toSell = configuration.getString("toSell");
        toIgnore = configuration.getString("toIgnore");

        slotsToUse = configuration.getIntegerList("slotsToUse");

    }

    public void sellPlayerInventory(@NotNull Player player){
        BigDecimal value = BigDecimal.ZERO;

        List<ItemStack> toRemove = new ArrayList<>();
        List<ItemStack> toRemoveFromBackpack = new ArrayList<>();

        for(ItemStack itemStack : player.getInventory().getContents()){
            if(itemStack == null || itemStack.getType().equals(Material.AIR)){
                continue;
            }

            if(blocksValues.containsKey(itemStack.getType())){
                BigDecimal itemValue = BigDecimal.valueOf(blocksValues.get(itemStack.getType()))
                        .multiply(BigDecimal.valueOf(itemStack.getAmount()));
                value = value.add(itemValue);
                toRemove.add(itemStack);
            }
        }

        if(MinepacksHook.isEnabled() && MinepacksHook.isSellFromBackpack()){
            Backpack backpack = MinepacksHook.getMinepacksPlugin().getBackpackCachedOnly(player);
            if(backpack != null){
                for(ItemStack itemStack : backpack.getInventory().getContents()){
                    if(itemStack == null || itemStack.getType().equals(Material.AIR)){
                        continue;
                    }

                    if(blocksValues.containsKey(itemStack.getType())){
                        BigDecimal itemValue = BigDecimal.valueOf(blocksValues.get(itemStack.getType()))
                                .multiply(BigDecimal.valueOf(itemStack.getAmount()));
                        value = value.add(itemValue);
                        toRemoveFromBackpack.add(itemStack);
                    }
                }
            }
        }

        if(value.equals(BigDecimal.ZERO)){
            ChatUtils.sendMessage(player, Lang.getText("NOTHING_TO_SELL"));
            return;
        }

        value = Utils.round(countMultiplier(player, value),2);

        for(ItemStack itemStack : toRemove){
            player.getInventory().remove(itemStack);
        }

        if(MinepacksHook.isEnabled()){
            if(MinepacksHook.isSellFromBackpack()){
                Backpack backpack = MinepacksHook.getMinepacksPlugin().getBackpackCachedOnly(player);
                if(backpack != null){
                    for(ItemStack itemStack : toRemoveFromBackpack){
                        backpack.getInventory().remove(itemStack);
                    }
                }
            }
        }

        String message = Lang.getText("SOLD_INVENTORY").replace("%value%", formatMoney(value));
        ChatUtils.sendMessage(player, message);
        AutoSell.getEconomy().depositPlayer(player, value.doubleValue());
    }

    public void sellSingleItem(Player player, @NotNull ItemStack itemStack){
        if(!blocksValues.containsKey(itemStack.getType())){
            return;
        }

        BigDecimal value = BigDecimal.valueOf(blocksValues.get(itemStack.getType()) * itemStack.getAmount());

        value = Utils.round(countMultiplier(player, value),2);

        AutoSell.getEconomy().depositPlayer(player, value.doubleValue());
    }

    private BigDecimal countMultiplier(Player player, BigDecimal value){
        for(String permission : multipliers.keySet()){
            if(player.hasPermission(permission)){
                double multiplier = multipliers.get(permission);
                return value.multiply(BigDecimal.valueOf(multiplier));
            }
        }
        return value;
    }

    public static @NotNull String formatMoney(BigDecimal value) {
        value = value.setScale(2, RoundingMode.HALF_UP);

        String[] suffixes = {"", "k", "M", "B", "T", "Q", "Qi", "Sx", "Sp", "Oc", "No", "Dc"};
        BigDecimal thousand = new BigDecimal("1000");
        int index = 0;

        while (value.compareTo(thousand) >= 0 && index < suffixes.length - 1) {
            value = value.divide(thousand, 2, RoundingMode.HALF_UP);
            index++;
        }

        return value.toPlainString() + suffixes[index];
    }

    public void openFilterGui(Player player){
        Miner miner = Controllers.getMinersController().getMiner(player);

        PaginatedGui gui = Gui.paginated()
                .title(Component.text(ChatUtils.fixColor(title)))
                .rows(6)
                .create();

        gui.setDefaultClickAction(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));

        //GuiItem guiFiller = ItemBuilder.from(filler).asGuiItem();
        GuiItem bottomGuiFiller = ItemBuilder.from(bottomFiller).asGuiItem();

        for(int i = 0; i <= 44; i++){
            if(slotsToUse.contains(i)){
                continue;
            }
            //gui.setItem(i, guiFiller);
        }

        for(int i = 45; i <= 53; i++){
            gui.setItem(i, bottomGuiFiller);
        }

        gui.setItem(48, ItemBuilder.from(previousPage).asGuiItem(inventoryClickEvent -> { gui.previous(); }));
        gui.setItem(50, ItemBuilder.from(nextPage).asGuiItem(inventoryClickEvent -> { gui.next(); }));

        for(Material temp : sellableMaterials){

            boolean sell = miner.getAutoSellFilter().contains(temp);
            String name;
            if(sell){
                name = ChatUtils.fixColor(toSell);
            }
            else{
                name = ChatUtils.fixColor(toIgnore);
            }

            gui.addItem(ItemBuilder.from(temp).setName(name).asGuiItem(inventoryClickEvent -> {

                if(sell){
                    miner.getAutoSellFilter().remove(temp);
                }
                else{
                    miner.getAutoSellFilter().add(temp);
                }

                openFilterGui((Player) inventoryClickEvent.getWhoClicked());
            }));
        }

        gui.open(player);
    }

    public boolean isSellableItem(Material material){
        return blocksValues.containsKey(material);
    }

    public List<Material> getSellableMaterials(){
        List<Material> toSell = new ArrayList<>();
        for(Material material : sellableMaterials){
            toSell.add(Settings.getOreDrops().getOrDefault(material, material));
        }
        return toSell;
    }

    public String listToString(@NotNull List<Material> materials){
        StringBuilder a = new StringBuilder(materials.get(0).name());
        if(materials.size() > 1){
            for(int i = 1; i < materials.size(); i++){
                a.append(":").append(materials.get(i).name());
            }
        }
        return a.toString();
    }

    public List<Material> stringToList(@NotNull String a){
        String[] parts = a.split(":");
        List<Material> materials = new ArrayList<>();
        for(String part : parts){
            materials.add(Material.valueOf(part));
        }
        return materials;
    }
}
