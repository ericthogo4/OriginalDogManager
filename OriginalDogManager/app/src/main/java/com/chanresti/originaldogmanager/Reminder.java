package com.chanresti.originaldogmanager;

/**
 * Created by Mwangi on 13/03/2018.
 */

// a class defining the reminder model
public class Reminder {

    private int mId;
    private String mContent;
    private String mDatestring;
    private int mImportant;

    public Reminder(int mId, String mContent, String mDatestring, int mImportant) {
        this.mId = mId;
        this.mContent = mContent;
        this.mDatestring = mDatestring;
        this.mImportant = mImportant;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public String getDatestring() {
        return mDatestring;
    }

    public void setDatestring(String mDatestring) {
        this.mDatestring = mDatestring;
    }

    public int getImportant() {
        return mImportant;
    }

    public void setImportant(int mImportant) {
        this.mImportant = mImportant;
    }
}

