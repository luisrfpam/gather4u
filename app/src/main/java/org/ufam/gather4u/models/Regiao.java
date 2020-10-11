package org.ufam.gather4u.models;

public class Regiao {

    private int id = -1;
    private Boolean checked = false;
    private String nome;
    private int imagem = -1;

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getImagem() {
        return imagem;
    }

    public void setImagem(int imagem) {
        this.imagem = imagem;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }
}
