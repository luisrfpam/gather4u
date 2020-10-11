package org.ufam.gather4u.models;

import org.ufam.gather4u.utils.CustomGson;

public class Disponibilidade extends CustomGson {

    private String data;
    private String hrini;
    private String hrfim;
    private Boolean checked = false;

    public String getData() { return data; }

    public void setData(String data) { this.data = data; }

    public String getHrini() { return hrini; }

    public void setHrini(String hrini) { this.hrini = hrini; }

    public String getHrfim() { return hrfim; }

    public void setHrfim(String hrfim) { this.hrfim = hrfim; }

    public Boolean getChecked() { return checked; }

    public void setChecked(Boolean checked) { this.checked = checked; }
}
