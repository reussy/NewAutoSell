package pl.pijok.autosell.booster;

public class Booster {

    /*
        Will be included in future updates
     */
    private double multiplier;
    private long duration;

    public Booster(double multiplier, long duration){
        this.multiplier = multiplier;
        this.duration = duration;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public void increaseMultiplier(double value){
        this.multiplier += value;
    }

    public void decreaseMultiplier(double value){
        this.multiplier -= value;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void increaseDuration(long value){
        this.duration += value;
    }

    public void decreaseDuration(long value){
        this.duration -= value;
    }
}
