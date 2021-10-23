package model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Student {
    private String nic;
    private String fullName;
    private String address;
    private LocalDate dateOfBirth;
    private String contact;
    private String email;

    public Student() {
    }

    public Student(String nic, String fullName, String address, LocalDate dateOfBirth, String contact, String email) {
        this.nic = nic;
        this.fullName = fullName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.contact = contact;
        this.email = email;
    }

    public static Student fromMap(String nic, Map<String, String> data) {
        return new Student(
                nic,
                data.get("name"),
                data.get("address"),
                LocalDate.parse(data.get("dob")),
                data.get("contact"),
                data.get("email")
        );
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", fullName);
        map.put("address", address);
        map.put("dob", dateOfBirth + "");
        map.put("contact", contact);
        map.put("email", email);
        return map;
    }

    @Override
    public String toString() {
        return "Student{" +
                "nic='" + nic + '\'' +
                ", fullName='" + fullName + '\'' +
                ", address='" + address + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", contact='" + contact + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
