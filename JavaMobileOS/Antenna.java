public abstract class Antenna {
    
    public abstract boolean isConnected();

    public abstract void setNetwork(boolean isConnected);

    public abstract int getSignalStrength();

    public abstract void setSignalStrength(int n);

}
