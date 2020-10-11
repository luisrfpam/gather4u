package org.ufam.gather4u.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {

    @SerializedName("_id")
    private int _id;

    @SerializedName("nome")
    private String nome;

    @SerializedName("login")
    private String login;

    @SerializedName("senha")
    private String senha;

    @SerializedName("cep")
    private String cep;

    @SerializedName("logradouro")
    private String logradouro;

    @SerializedName("nr")
    private String nr;

    @SerializedName("complemento")
    private String complemento;

    @SerializedName("_idcidade")
    private int _idcidade;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("dtcad")
    private String dtcad;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("whatsapp")
    private String whatsapp;

    @SerializedName("telefone")
    private String telefone;

    @SerializedName("email")
    private String email;

    @SerializedName("tipoempresa")
    private short tipoempresa;

    @SerializedName("tipopessoa")
    private short tipopessoa;

    @SerializedName("cpf")
    private String cpf;

    @SerializedName("cnpj")
    private String cnpj;

    @SerializedName("pontos")
    private int pontos;

    @SerializedName("ativo")
    private short ativo;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public int getIdCidade() {
        return _idcidade;
    }

    public void setIdCidade(int idcidade) {
        this._idcidade = idcidade;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String picture) {
        this.avatar = picture;
    }

    public String getDTCad() {
        return dtcad;
    }

    public void setDTCad(String createdAt) {
        this.dtcad = createdAt;
    }

    public String getLatitude() { return latitude; }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() { return longitude; }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getWhatsapp() { return whatsapp; }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getTelefone() { return telefone; }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
    }

    public short getTipoempresa() { return tipoempresa; }

    public void setTipoEmpresa(short tipoempresa) {
        this.tipoempresa = tipoempresa;
    }

    public short getTipoPessoa() { return tipopessoa; }

    public void setTipoPessoa(short tipopessoa) {
        this.tipopessoa = tipopessoa;
    }

    public String getCpf() { return cpf; }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCnpj() { return cnpj; }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    public short getAtivo() { return ativo; }

    public void setAtivo(short ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "User{" +
                "_id=" + _id +
                ", nome='" + nome + '\'' +
                ", login='" + login + '\'' +
                ", senha='" + senha + '\'' +
                ", cep='" + cep + '\'' +
                ", logradouro='" + logradouro + '\'' +
                ", nr='" + nr + '\'' +
                ", complemento='" + complemento + '\'' +
                ", idcidade='" + _idcidade + '\'' +
                ", avatar='" + avatar + '\'' +
                ", dtcad='" + dtcad + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", whatsapp='" + whatsapp + '\'' +
                ", telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", tipoempresa='" + tipoempresa + '\'' +
                ", tipopessoa='" + tipopessoa + '\'' +
                ", cpf='" + cpf + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", pontos='" + pontos + '\'' +
                ", ativo='" + ativo + '\'' +

                '}';
    }

    public User() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(this._id);
        dest.writeString(this.login);
        dest.writeString(this.senha);
        dest.writeString(this.nome);
        dest.writeString(this.cep);
        dest.writeString(this.logradouro);
        dest.writeString(this.nr);
        dest.writeString(this.complemento);
        dest.writeInt(this._idcidade);
        dest.writeString(this.avatar);
        dest.writeString(this.dtcad);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
        dest.writeString(this.whatsapp);
        dest.writeString(this.telefone);
        dest.writeString(this.email);
        dest.writeInt(this.tipoempresa);
        dest.writeInt(this.tipopessoa);
        dest.writeString(this.cpf);
        dest.writeString(this.cnpj);
        dest.writeInt(this.pontos);
        dest.writeInt(this.ativo);
    }

    protected User(Parcel in) {

        this._id = in.readInt();
        this.login = in.readString();
        this.senha = in.readString();
        this.cep = in.readString();
        this.logradouro = in.readString();
        this.nr = in.readString();
        this.complemento = in.readString();
        this._idcidade = in.readInt();
        this.dtcad = in.readString();
        this.avatar = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.whatsapp = in.readString();
        this.telefone = in.readString();
        this.email = in.readString();
        this.tipoempresa = (short) in.readInt();
        this.tipopessoa = (short)in.readInt();
        this.cpf = in.readString();
        this.cnpj = in.readString();
        this.pontos = in.readInt();
        this.ativo = (short)in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
