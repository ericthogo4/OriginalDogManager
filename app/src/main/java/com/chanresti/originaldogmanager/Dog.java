package com.chanresti.originaldogmanager;

/**
 * Created by Mwangi on 07/04/2018.
 */
//class defining the dog model
public class Dog {
    private int dId;
    private String mUriString;
    private String mDogName;
    private String mWeight;
    private String mGender;
    private String mBreed;
    private String mHabits;
    private String mAge;

    public Dog(int dId, String mUriString, String mDogName, String mWeight, String mGender, String mBreed, String mHabits, String mAge) {
        this.dId = dId;
        this.mUriString = mUriString;
        this.mDogName = mDogName;
        this.mWeight = mWeight;
        this.mGender = mGender;
        this.mBreed = mBreed;
        this.mHabits = mHabits;
        this.mAge = mAge;
    }

    public int getdId() {
        return dId;
    }

    public void setdId(int dId) {
        this.dId = dId;
    }

    public String getUriString() {
        return mUriString;
    }

    public void setUriString(String mUriString) {
        this.mUriString = mUriString;
    }

    public String getDogName() {
        return mDogName;
    }

    public void setDogName(String mDogName) {
        this.mDogName = mDogName;
    }

    public String getWeight() {
        return mWeight;
    }

    public void setWeight(String mWeight) {
        this.mWeight = mWeight;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String mGender) {
        this.mGender = mGender;
    }

    public String getBreed() {
        return mBreed;
    }

    public void setBreed(String mBreed) {
        this.mBreed = mBreed;
    }

    public String getHabits() {
        return mHabits;
    }

    public void setHabits(String mHabits) {
        this.mHabits = mHabits;
    }

    public String getAge() {
        return mAge;
    }

    public void setAge(String mAge) {
        this.mAge = mAge;
    }
}
