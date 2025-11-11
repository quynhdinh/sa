package kafka;

public class FeeRecord {
    // license plate, owner info, speed, amount of the fee
    private String licencePlate;
    private String owner;
    private int speed;
    private int fee;
    public FeeRecord() {
    }
    public FeeRecord(String licencePlate, String owner, int speed, int fee) {
        this.licencePlate = licencePlate;
        this.owner = owner;
        this.speed = speed;
        this.fee = fee;
    }
    public String getLicencePlate() {
        return licencePlate;
    }
    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public int getFee() {
        return fee;
    }
    public void setFee(int fee) {
        this.fee = fee;
    }
    
}
