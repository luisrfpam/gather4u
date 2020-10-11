package org.ufam.gather4u.eventbus;

import org.ufam.gather4u.models.Endereco;
import org.ufam.gather4u.models.Residuo;

import java.util.ArrayList;
import java.util.List;

public class EntregaEventMessage extends CustomEventMessage {

    private List<Residuo> residuos = null;
    private Endereco endereco = null;

    public EntregaEventMessage(List<Residuo> residuos, Endereco ender , String reference){

        this.residuos = residuos;
        this.endereco = ender;
        this.setClassReference(reference);
    }

    public List<Residuo> getResiduos() {
        return residuos;
    }

    public void setResiduos(ArrayList<Residuo> residuos) {
        this.residuos = residuos;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco ender) {
        this.endereco = ender;
    }
}
