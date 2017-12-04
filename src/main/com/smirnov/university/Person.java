package com.smirnov.university;


public abstract class Person {
    private String name;
    private String phone;
    private String email;
    private int universityId;

    Person(String name, String phone, String email, int universityId){
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.universityId = universityId;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    String getPhone() {
        return phone;
    }

    String getEmail() {
        return email;
    }

    int getUniversityId() {
        return universityId;
    }
}
