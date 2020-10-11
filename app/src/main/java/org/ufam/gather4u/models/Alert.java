package org.ufam.gather4u.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Alert implements Parcelable {

    public final static int RISK_ELETRICAL_SHOCK = 1;
    public final static int COSTUME_ITEM_FAILED = 2;

    @SerializedName("_id")
    private int _id;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("picture")
    private String picture;

    @SerializedName("user")
    private User user;

    @SerializedName("type")
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Alert() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.createdAt);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.picture);
        dest.writeParcelable(this.user, flags);
        dest.writeInt(this.type);
    }

    protected Alert(Parcel in) {
        this._id = in.readInt();
        this.createdAt = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.picture = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.type = in.readInt();
    }

    public static final Creator<Alert> CREATOR = new Creator<Alert>() {
        @Override
        public Alert createFromParcel(Parcel source) {
            return new Alert(source);
        }

        @Override
        public Alert[] newArray(int size) {
            return new Alert[size];
        }
    };

    @Override
    public String toString() {
        return "Alert{" +
                "_id=" + _id +
                ", createdAt='" + createdAt + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", picture='" + picture + '\'' +
                ", user=" + user +
                ", type=" + type +
                '}';
    }
}
