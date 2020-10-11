package org.ufam.gather4u.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Sensor implements Parcelable {

    @SerializedName("_id")
    private int _id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("picture")
    private String picture;

    @SerializedName("status")
    private int status;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.picture);
        dest.writeInt(this.status);
    }

    public Sensor() {
    }

    protected Sensor(Parcel in) {
        this._id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.picture = in.readString();
        this.status = in.readInt();
    }

    public static final Parcelable.Creator<Sensor> CREATOR = new Parcelable.Creator<Sensor>() {
        @Override
        public Sensor createFromParcel(Parcel source) {
            return new Sensor(source);
        }

        @Override
        public Sensor[] newArray(int size) {
            return new Sensor[size];
        }
    };

    @Override
    public String toString() {
        return "Sensor{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", picture='" + picture + '\'' +
                ", status=" + status +
                '}';
    }
}
