package Entity;

public class TTTRrecord {

    private int data;
    private int timeTag;
    private int overflows;
    private int channel;
    private int valid;
    private int route;
    private int reserved;
    private double trueTime;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public int getTimeTag() {
        return timeTag;
    }

    public void setTimeTag(int timeTag) {
        this.timeTag = timeTag;
    }

    public int getOverflows() {
        return overflows;
    }

    public void setOverflows(int overflows) {
        this.overflows = overflows;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    public int getRoute() {
        return route;
    }

    public void setRoute(int route) {
        this.route = route;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public double getTrueTime() {
        return trueTime;
    }

    public void setTrueTime(double trueTime) {
        this.trueTime = trueTime;
    }
}
