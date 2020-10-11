package org.ufam.gather4u.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.ufam.gather4u.utils.CustomGson;

public class Residuo extends CustomGson implements Parcelable {

    private Boolean checked = false;
    private int id;

    @SerializedName("id")
    private int id_residuo;

    @SerializedName("nome")
    private String nome;

    @SerializedName("descricao")
    private String descricao;

    private String peso;

    @SerializedName("cor")
    private String cor;

    @SerializedName("peso_pontuacao")
    private String peso_pontuacao;

    private int imagem = -1;

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getIdResiduo() { return id_residuo; }

    public void setIdResiduo(int id) { this.id_residuo = id; }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getImagem() {
        return imagem;
    }

    public void setImagem(int imagem) {
        this.imagem = imagem;
    }

    public String getPeso() { return peso; }

    public void setPeso(String peso) { this.peso = peso; }

    public String getPesoPontuacao() { return peso_pontuacao; }

    public void setPesoPontuacao(String peso_pontuacao) { this.peso_pontuacao = peso_pontuacao; }

    public String getCor() { return cor; }

    public void setCor(String cor) { this.cor = cor; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(this.id_residuo);
        dest.writeString(this.nome);
        dest.writeString(this.descricao);
        dest.writeString(this.cor);
        dest.writeString(this.peso_pontuacao);
    }

    public Residuo() {}

    public Residuo(Parcel in) {

        this.id_residuo = in.readInt();
        this.nome = in.readString();
        this.descricao = in.readString();
        this.cor = in.readString();
        this.peso_pontuacao = in.readString();
    }

    @Override
    public String toString() {
        return "Residuo{" +
                "id=" + id_residuo +
                ", nome='" + nome + '\'' +
                ", descricao='" +  descricao + '\'' +
                ", cor='" + cor + '\'' +
                ", peso_pontuacao='" + peso_pontuacao + '\'' +
                '}';
    }

    public static final Creator<Residuo> CREATOR = new Creator<Residuo>() {
        @Override
        public Residuo createFromParcel(Parcel source) {
            return new Residuo(source);
        }

        @Override
        public Residuo[] newArray(int size) {
            return new Residuo[size];
        }
    };
}
