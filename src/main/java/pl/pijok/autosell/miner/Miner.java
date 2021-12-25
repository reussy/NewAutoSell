package pl.pijok.autosell.miner;

public class Miner {

    private long minedBlocks;
    private boolean autoSell;

    public Miner(long minedBlocks, boolean autoSell){
        this.minedBlocks = minedBlocks;
        this.autoSell = autoSell;
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
}
