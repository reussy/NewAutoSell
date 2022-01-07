package pl.pijok.autosell.miner;

import org.bukkit.Material;
import pl.pijok.autosell.Controllers;

import java.util.List;

public class Miner {

    private long minedBlocks;
    private boolean autoSell;
    private List<Material> autoSellFilter;

    public Miner(long minedBlocks, boolean autoSell){
        this.minedBlocks = minedBlocks;
        this.autoSell = autoSell;
        this.autoSellFilter = Controllers.getSellingController().getSellableMaterials();
    }

    public Miner(long minedBlocks, boolean autoSell, List<Material> autoSellFilter){
        this.minedBlocks = minedBlocks;
        this.autoSell = autoSell;
        this.autoSellFilter = autoSellFilter;
    }

    public long getMinedBlocks() {
        return minedBlocks;
    }

    public void setMinedBlocks(long minedBlocks) {
        this.minedBlocks = minedBlocks;
    }

    public boolean isAutoSell() {
        return autoSell;
    }

    public void setAutoSell(boolean autoSell) {
        this.autoSell = autoSell;
    }

    public void increasedMinedBlocks(long amount){
        this.minedBlocks += amount;
    }

    public void decreaseMinedBlocks(long amount){
        this.minedBlocks -= amount;
    }

    public List<Material> getAutoSellFilter() {
        return autoSellFilter;
    }
}
