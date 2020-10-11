package org.ufam.gather4u.models;

import org.json.JSONObject;
import org.ufam.gather4u.utils.Conv;
import org.ufam.gather4u.utils.CustomGson;

public class Entrega extends CustomGson {

    private int id;
    private int identregador;
    private int idusuario;
    private int idcoleta;
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

    private double ent_aval;
    private double col_aval;

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

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public int getIdColeta() { return idcoleta; }

    public void setIdColeta(int idcoleta) { this.idcoleta = idcoleta; }

    public double getEntAval() { return ent_aval; }

    public void setEntAval(double ent_aval) { this.ent_aval = ent_aval; }

    public double getColAval() { return col_aval; }

    public void setColAval(double col_aval) { this.col_aval = col_aval; }

    public static Entrega fromJSONObject( JSONObject json )
    {
        Entrega entrega = new Entrega();

        try {
            if (json != null){
                entrega.setId( json.getInt("id") );
                entrega.setIdEntregador( json.getInt("identregador") );
                entrega.setIdUsuario( json.getInt("idusuario") );
                entrega.setIdColeta( json.getInt("idcoleta") );
                entrega.setEntregador( json.getString("entregador") );
                entrega.setPesoTotal( json.getString("pesototal") );
                entrega.setPontos( json.getString("pontos") );
                entrega.setLogradouro( json.getString("logradouro") );
                entrega.setTipoResidencia( json.getString("tiporesidencia") );
                entrega.setBairro( json.getString("bairro") );
                entrega.setObs( json.getString("obs") );
                entrega.setDTCadstro( json.getString("dtcadastro") );
                entrega.setEntAval( Conv.ToDouble( json.getString("ent_aval")) );
                entrega.setColAval ( Conv.ToDouble( json.getString("col_aval")) );

                if (json.has("lat") ){
                    entrega.setLatitude( Conv.ToDouble(json.getString("lat")) );
                }
                else if (json.has("latitude") ){
                    entrega.setLatitude( Conv.ToDouble(json.getString("latitude")) );
                }

                if (json.has("lon")){
                    entrega.setLongitude( Conv.ToDouble(json.getString("lon")) );
                }
                else if (json.has("longitude")){
                    entrega.setLongitude( Conv.ToDouble(json.getString("longitude")) );
                }
            }
        }
        catch (Exception ex){

        }

        return entrega;
    }


}
