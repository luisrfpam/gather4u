package org.ufam.gather4u.eventbus;

import org.ufam.gather4u.models.Gather_User;

import java.util.ArrayList;
import java.util.List;

public class ColetorEventMessage extends CustomEventMessage {

    private Gather_User user = null;
    private List<Integer> residuos = null;
    private List<Integer> regioes = null;

    public ColetorEventMessage(Gather_User user, List<Integer> residuos, List<Integer> regioes, String reference ){
        this.user = user;
        this.residuos = residuos;
        this.regioes = regioes;
        this.setClassReference(reference);
    }

    public Gather_User getUser() {
        return user;
    }

    public void setUser(Gather_User user) {
        this.user = user;
    }

    public List<Integer> getResiduos() {
        return residuos;
    }

    public void setResiduos(ArrayList<Integer> residuos) {
        this.residuos = residuos;
    }

    public List<Integer> getRegioes() {
        return residuos;
    }

    public void setRegioes(List<Integer> regioes) {
        this.regioes = regioes;
    }
}
