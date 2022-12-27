package com.example.contactsappassignmentrealmdb.entity;

import io.realm.RealmObject;

public class ModelClass extends RealmObject {
    String uid="";
    String name="";
    String number="";
    String birthday ="";
    byte[] avtar ;

    public ModelClass(){

    }

    public ModelClass(String uid, String name, String number, String birthday, byte[] avtar) {
        this.uid = uid;
        this.name = name;
        this.number = number;
        this.birthday = birthday;
        this.avtar = avtar;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public byte[] getAvtar() {
        return avtar;
    }

    public void setAvtar(byte[] avtar) {
        this.avtar = avtar;
    }
}
