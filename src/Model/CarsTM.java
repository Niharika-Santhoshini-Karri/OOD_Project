package Model;

public class CarsTM {
    private String id;
    private String model;
    private String owner;
    private String status;

    public CarsTM(String id, String model, String owner, String status) {
        this.id = id;
        this.model = model;
        this.owner = owner;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CarsTM{" +
                "id='" + id + '\'' +
                ", model='" + model + '\'' +
                ", owner='" + owner + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
