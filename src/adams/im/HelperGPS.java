package adams.im;

public class HelperGPS {

    private String name;
    private String x;
    private String y;
    private String z;
    private String color;

    public HelperGPS(String name, String x, String y, String z, String color){
        this.name = name;
        this.x = x.substring(0, x.length()-1);
        this.y = y.substring(0, y.length()-1);
        this.z = z.substring(0, z.length()-1);
        this.color = color;
    }

    public HelperGPS(String ownerName, String x, String y, String z){
        this.name = ownerName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = "FF75C9F1";

    }

    public String toString(){
        return "GPS:" + name + ":" + (int)Double.parseDouble(x) + ":" + (int)Double.parseDouble(y) + ":" + (int)Double.parseDouble(z) + ":#" + color + ":";
    }

}
