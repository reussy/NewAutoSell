package pl.pijok.autosell.selling;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.pijok.autosell.AutoSell;
import pl.pijok.autosell.Controllers;
import pl.pijok.autosell.essentials.ChatUtils;
import pl.pijok.autosell.essentials.ConfigUtils;
import pl.pijok.autosell.essentials.Debug;
import pl.pijok.autosell.essentials.Utils;
import pl.pijok.autosell.miner.Miner;
import pl.pijok.autosell.settings.Lang;
import pl.pijok.autosell.settings.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SellingController {

    private final AutoSell plugin;

    private HashMap<Material, Double> blocksValues;
    private LinkedHashMap<String, Double> multipliers;
    private List<Material> sellableMaterials;

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

    public void sellPlayerInventory(Player player){
        double value = 0;

        List<ItemStack> toRemove = new ArrayList<>();

        for(ItemStack itemStack : player.getInventory().getContents()){
            if(itemStack == null || itemStack.getType().equals(Material.AIR)){
                continue;
            }

            if(blocksValues.containsKey(itemStack.getType())){
                value += blocksValues.get(itemStack.getType()) * itemStack.getAmount();
                toRemove.add(itemStack);
            }
        }

        if(value == 0){
            ChatUtils.sendMessage(player, Lang.getText("NOTHING_TO_SELL"));
            return;
        }

        value = Utils.round(countMultiplier(player, value),2);

        for(ItemStack itemStack : toRemove){
            player.getInventory().remove(itemStack);
        }

        String message = Lang.getText("SOLD_INVENTORY").replace("%value%", "" + value);
        ChatUtils.sendMessage(player, message);
        AutoSell.getEconomy().depositPlayer(player, value);
    }

    public void sellSingleItem(Player player, ItemStack itemStack){
        if(!blocksValues.containsKey(itemStack.getType())){
            return;
        }

        double value = blocksValues.get(itemStack.getType()) * itemStack.getAmount();

        value = Utils.round(countMultiplier(player, value),2);

        AutoSell.getEconomy().depositPlayer(player, value);
    }

    private double countMultiplier(Player player, double value){
        for(String permission : multipliers.keySet()){
            if(player.hasPermission(permission)){
                value = value * multipliers.get(permission);
                return value;
            }
        }
        return value;
    }

    public void openFilterGui(Player player){
        Miner miner = Controllers.getMinersController().getMiner(player);

        PaginatedGui gui = Gui.paginated()
                .title(Component.text(ChatUtils.fixColor(title)))
                .rows(6)
                .create();

        gui.setDefaultClickAction(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
        });

        GuiItem guiFiller = ItemBuilder.from(filler).asGuiItem();
        GuiItem bottomGuiFiller = ItemBuilder.from(bottomFiller).asGuiItem();

        for(int i = 0; i <= 44; i++){
            if(slotsToUse.contains(i)){
                continue;
            }
            gui.setItem(i, guiFiller);
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

    public String listToString(List<Material> materials){
        String a = materials.get(0).name();
        if(materials.size() > 1){
            for(int i = 1; i < materials.size(); i++){
                a = a + ":" + materials.get(i).name();
            }
        }
        return a;
    }

    public List<Material> stringToList(String a){
        String[] parts = a.split(":");
        List<Material> materials = new ArrayList<>();
        for(String part : parts){
            materials.add(Material.valueOf(part));
        }
        return materials;
    }
}
