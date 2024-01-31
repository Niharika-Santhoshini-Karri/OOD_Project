package Model;

public class MemberTM {
    private String id;

    private String email;

    private String name;
    private String address;
    private String contact;
    private String password;

    public MemberTM(String id,String email, String name, String address, String contact, String password) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
