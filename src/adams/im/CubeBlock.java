package adams.im;

public class CubeBlock {
    private String definitionType;
    private String typeID;
    private String subtypeID;
    private String displayName;
    private String description;
    private String cubeSize;
    private int[] size;
    private String[] components;
    private int[] compCount;
    private String criticalComponent;
    private String blockPairName;
    private int buildTime;
    private int PCU;
    private boolean guiVisible;
    private double generalDamageMultiplier;
    // for thrusters
    private String thrusterType;
    private double maxPowerConsumption;
    private double minPowerConsumption;
    private int forceMagnitude;
    private int slowdownFactor;

    public CubeBlock(String typeID, String subtypeID){
        this.typeID = typeID;
        this.subtypeID = subtypeID;
    }

    public String toString(){
        return this.typeID + " " + this.subtypeID;
    }


    public String getDefinitionType() {
        return definitionType;
    }

    public void setDefinitionType(String definitionType) {
        this.definitionType = definitionType;
    }

    public String getTypeID() {
        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }

    public String getSubtypeID() {
        return subtypeID;
    }

    public void setSubtypeID(String subtypeID) {
        this.subtypeID = subtypeID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCubeSize() {
        return cubeSize;
    }

    public void setCubeSize(String cubeSize) {
        this.cubeSize = cubeSize;
    }

    public int[] getSize() {
        return size;
    }

    public void setSize(int[] size) {
        this.size = size;
    }

    public String[] getComponents() {
        return components;
    }

    public void setComponents(String[] components) {
        this.components = components;
    }

    public int[] getCompCount() {
        return compCount;
    }

    public void setCompCount(int[] compCount) {
        this.compCount = compCount;
    }

    public String getCriticalComponent() {
        return criticalComponent;
    }

    public void setCriticalComponent(String criticalComponent) {
        this.criticalComponent = criticalComponent;
    }

    public String getBlockPairName() {
        return blockPairName;
    }

    public void setBlockPairName(String blockPairName) {
        this.blockPairName = blockPairName;
    }

    public int getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(int buildTime) {
        this.buildTime = buildTime;
    }

    public int getPCU() {
        return PCU;
    }

    public void setPCU(int PCU) {
        this.PCU = PCU;
    }

    public boolean isGuiVisible() {
        return guiVisible;
    }

    public void setGuiVisible(boolean guiVisible) {
        this.guiVisible = guiVisible;
    }

    public double  getGeneralDamageMultiplier() {
        return generalDamageMultiplier;
    }

    public void setGeneralDamageMultiplier(double generalDamageMultiplier) {
        this.generalDamageMultiplier = generalDamageMultiplier;
    }

    public String getThrusterType() {
        return thrusterType;
    }

    public void setThrusterType(String thrusterType) {
        this.thrusterType = thrusterType;
    }

    public double getMaxPowerConsumption() {
        return maxPowerConsumption;
    }

    public void setMaxPowerConsumption(double maxPowerConsumption) {
        this.maxPowerConsumption = maxPowerConsumption;
    }

    public double getMinPowerConsumption() {
        return minPowerConsumption;
    }

    public void setMinPowerConsumption(double minPowerConsumption) {
        this.minPowerConsumption = minPowerConsumption;
    }

    public int getForceMagnitude() {
        return forceMagnitude;
    }

    public void setForceMagnitude(int forceMagnitude) {
        this.forceMagnitude = forceMagnitude;
    }

    public int getSlowdownFactor() {
        return slowdownFactor;
    }

    public void setSlowdownFactor(int slowdownFactor) {
        this.slowdownFactor = slowdownFactor;
    }
}
