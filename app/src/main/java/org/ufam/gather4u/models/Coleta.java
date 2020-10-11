package org.ufam.gather4u.models;

import org.ufam.gather4u.utils.CustomGson;

public class Coleta extends CustomGson {

    private int id;
    private int identregador;
    private int idcoletor;
    private int idusuario;
    private String entregador;
    private String logradouro;
    private String dtcadastro;
    private String pesototal;
    private String pontos;

    private String tiporesidencia;
    private String bairro;
    private String obs;

    private double latitude;
    private double longitude;

    private int imagem = -1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEntregador() { return identregador; }

    public void setIdEntregador(int identregador) { this.identregador = identregador; }

    public int getIdUsuario() { return idusuario; }

    public void setIdUsuario(int idusuario) { this.idusuario = idusuario; }

    public String getEntregador() {
        return entregador;
    }

    public void setEntregador(String nome) {
        this.entregador = nome;
    }

    public int getImagem() {
        return imagem;
    }

    public void setImagem(int imagem) {
        this.imagem = imagem;
    }

    public String getDTCadstro() {
        return dtcadastro;
    }

    public void setDTCadstro(String dtentrega) {
        this.dtcadastro = dtentrega;
    }

    public String getPesoTotal() {
        return pesototal;
    }

    public void setPesoTotal(String pesototal) {
        this.pesototal = pesototal;
    }

    public String getLogradouro() { return logradouro; }

    public void setLogradouro(String descricao) {
        this.logradouro = descricao;
    }

    public String getPontos() { return pontos; }

    public void setPontos(String pontos) { this.pontos = pontos; }

    public String getTipoResidencia() { return tiporesidencia; }

    public void setTipoResidencia(String tiporesidencia) { this.tiporesidencia = tiporesidencia; }

    public String getBairro() { return bairro; }

    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getObs() { return obs; }

    public void setObs(String obs) { this.obs = obs; }

    public int getIdcoletor() { return idcoletor; }

    public void setIdcoletor(int idcoletor) { this.idcoletor = idcoletor; }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }
}
