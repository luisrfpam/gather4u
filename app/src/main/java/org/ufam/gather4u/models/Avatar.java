package org.ufam.gather4u.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class Avatar implements Parcelable {

    @SerializedName("_id")
    private int _id;

    @SerializedName("login")
    private String login;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("size")
    private String size;

    private String pathAvatar;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String _size) {
        this.size = _size;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String picture) {
        this.avatar = picture;
    }

    public String getPathAvatar() {
        return pathAvatar;
    }

    public void setPathAvatar(String _path) {
        this.pathAvatar = _path;
    }

    @Override
    public String toString() {
        return "User{" +
                "_id=" + _id +
                ", login='" + login + '\'' +
                ", avatar='" + avatar + '\'' +
                ", size='" + size + '\'' +
                '}';
    }

    public Avatar() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(this._id);
        dest.writeString(this.login);
        dest.writeString(this.avatar);
    }

    protected Avatar(Parcel in) {

        this._id = in.readInt();
        this.login = in.readString();
        this.avatar = in.readString();
    }

    protected Avatar(JSONObject in) {

        try {

            this._id = in.getInt("id");
            this.login = in.getString("login");
            this.avatar = in.getString("avatar");
        }
        catch (JSONException ex){

        }
    }

    public static Avatar fromJSONObject( JSONObject json ) {
        return new Avatar(json);
    }

    public static final Creator<Avatar> CREATOR = new Creator<Avatar>() {
        @Override
        public Avatar createFromParcel(Parcel source) {
            return new Avatar(source);
        }

        @Override
        public Avatar[] newArray(int size) {
            return new Avatar[size];
        }
    };


}
