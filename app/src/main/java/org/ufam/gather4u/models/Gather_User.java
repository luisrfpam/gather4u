package org.ufam.gather4u.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.ufam.gather4u.utils.CustomGson;

public class Gather_User extends CustomGson {

    private int id;
    private String nome;
    private String razaosocial;
    private String login;
    private String senha;

    private short tipopessoa;
    private short tipoempresa;
    private short sexo;
    private String cpf;
    private String cnpj;

    private String dtcad;
    private String dtnasc;

    private String cep;
    private String logradouro;
    private String nr;
    private String complemento;

    private int idcidade;
    private int idbairro;
    private int idregbairro;

    private String avatar;

    private String latitude;
    private String longitude;

    private String whatsapp;
    private String telefone;
    private String email;

    private int pontos;
    private short ativo;


    public int getId() { return id; }

    public void setId(int _id) {
        this.id = _id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRazaosocial() {
        return razaosocial;
    }

    public void setRazaoSocial(String razao) {
        this.razaosocial = razao;
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
        return idcidade;
    }

    public void setIdCidade(int idcidade) {
        this.idcidade = idcidade;
    }

    public int getIdRegBairro() { return idregbairro; }

    public void setIdRegBairro(int idregbairro) { this.idregbairro = idregbairro;  }

    public int getIdBairro() { return idbairro; }

    public void setIdBairro(int idbairro) { this.idbairro = idbairro;  }

    public String getAvatar() { return avatar; }

    public void setAvatar(String picture) {
        this.avatar = picture;
    }

    public String getDTCad() {
        return dtcad;
    }

    public void setDTCad(String createdAt) {
        this.dtcad = createdAt;
    }

    public String getDTNasc() {
        return dtnasc;
    }

    public void setDTNasc(String dtnasc) { this.dtnasc = dtnasc; }

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

    public short getSexo() { return sexo; }

    public void setSexo(short sexo) {
        this.sexo = sexo;
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
        return ToJson(this);
    }

    public static Gather_User fromJSONObject( JSONObject json ) {
        return new Gather_User(json);
    }

    public Gather_User() {
    }

    protected Gather_User(JSONObject in) {

        try {

            this.id = in.getInt("id");
            this.nome = in.getString("nome");
            this.razaosocial = in.getString("razaosocial");
            this.login = in.getString("login");
            this.senha = in.getString("senha");
            this.tipopessoa = (short)in.getInt("tipopessoa");
            this.cpf = in.getString("cpf");
            this.cnpj = in.getString("cnpj");
            this.tipoempresa = (short) in.getInt("tipoempresa");
            this.sexo = (short) in.getInt("sexo");
            this.whatsapp = in.getString("whatsapp");
            this.telefone = in.getString("telefone");
            this.email = in.getString("email");
            this.dtcad = in.getString("dtcad");
            this.dtnasc = in.getString("dtnasc");

            this.cep = in.getString("cep");
            this.logradouro = in.getString("logradouro");
            this.nr = in.getString("nr");
            this.complemento = in.getString("complemento");

            this.idcidade = in.getInt("idcidade");
            //this.idbairro = in.getInt("idbairro");
            this.idregbairro = in.getInt("idregbairro");

            this.avatar = in.getString("avatar");
            this.latitude = in.getString("latitude");
            this.longitude = in.getString("longitude");

            this.pontos = in.getInt("pontos");
            this.ativo = (short)in.getInt("ativo");
        }
        catch (JSONException ex){

        }
    }
}
