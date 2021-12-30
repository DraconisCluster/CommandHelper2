package adams.im;

public class GridBackup {
    private String gridName;
    private long entityId;
    private String date;

    public GridBackup(String gridName, long entityId, String date) {
        this.gridName = gridName;
        this. entityId = entityId;
        this.date = date;
    }

    public String getgridName(){
        return this.gridName;
    }

    public String getEntityId(){
        return Long.toString(this.entityId);
    }

    public String getDate() {
        return date;
    }

    public void setGridName(String gridName) {
        this.gridName = gridName;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String toString(){
        return this.gridName + " " + this.entityId + " " + this.date;
    }
}
