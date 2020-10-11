package org.ufam.gather4u.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Event implements Parcelable {

    public final static int VIDEO_CHANGED = 1;
    public final static int AUDIO_CHANGED = 2;

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

    @SerializedName("sensor")
    private Sensor sensor;

    @SerializedName("type")
    private int type;

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

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Event() {
    }

    @Override
    public String toString() {
        return "Event{" +
                "_id=" + _id +
                ", createdAt='" + createdAt + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", picture='" + picture + '\'' +
                ", user=" + user +
                ", sensor=" + sensor +
                ", type=" + type +
                '}';
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
        dest.writeParcelable(this.sensor, flags);
        dest.writeInt(this.type);
    }

    protected Event(Parcel in) {
        this._id = in.readInt();
        this.createdAt = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.picture = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.sensor = in.readParcelable(Sensor.class.getClassLoader());
        this.type = in.readInt();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
