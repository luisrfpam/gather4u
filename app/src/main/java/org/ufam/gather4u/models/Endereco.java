package org.ufam.gather4u.models;

import org.ufam.gather4u.utils.CustomGson;

public class Endereco extends CustomGson {

    private String cep;
    private String logradouro;
    private String nr;
    private String complemento;
    private String cidade;
    private int idcidade;
    private String bairro;
    private int idbairro;
    private String estado;
    private int idregbairro;
    private int idtiporesid;
    private double latitude;
    private double longitude;

    public String getCep() { return cep; }

    public void setCep(String cep) { this.cep = cep; }

    public String getLogradouro() { return logradouro; }

    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNr() { return nr; }

    public void setNr(String nr) { this.nr = nr; }

    public String getComplemento() { return complemento; }

    public void setComplemento(String complemento) { this.complemento = complemento; }

    public int getIdcidade() { return idcidade; }

    public void setIdcidade(int idcidade) { this.idcidade = idcidade; }

    public int getIdbairro() { return idbairro; }

    public void setIdbairro(int idbairro) { this.idbairro = idbairro; }

    public int getIdregbairro() { return idregbairro; }

    public void setIdregbairro(int idregbairro) { this.idregbairro = idregbairro; }

    public int getIdTipoResid() { return idtiporesid; }

    public void setIdTipoResid(int idtiporesid) { this.idtiporesid = idtiporesid; }

    public String getCidade() { return cidade; }

    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getBairro() { return bairro; }

    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }
}
