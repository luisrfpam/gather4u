package org.ufam.gather4u.eventbus;

import org.ufam.gather4u.models.Gather_User;

public class ParticipanteEventMessage extends CustomEventMessage {

    private Gather_User user = null;

    public Gather_User getUser() {
        return user;
    }

    public void setUser(Gather_User user) {
        this.user = user;
    }

    public ParticipanteEventMessage(Gather_User clazz, String reference ){
        this.user = clazz;
        this.setClassReference(reference);
    }
}
