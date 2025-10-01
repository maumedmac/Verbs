package bo.edu.cba;

public class Verb {
    private int id;
    private String baseForm;
    private String pastTense;
    private String pastParticipie;

    private String description;

    public Verb(int id, String baseForm, String pastTense, String pastParticipie, String description) {
        this.id = id;
        this.baseForm = baseForm;
        this.pastTense = pastTense;
        this.pastParticipie = pastParticipie;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getBaseForm() {
        return baseForm;
    }

    public String getPastTense() {
        return pastTense;
    }

    public String getPastParticipie() {
        return pastParticipie;
    }

    public String getDescription() {
        return description;
    }

    // Si usas un ArrayAdapter simple y un diseño de TextView predeterminado,
    // toString() determinará lo que se muestra.
    // Si usas un adaptador personalizado o un diseño de elemento personalizado,
    // puedes acceder a las propiedades directamente usando los getters.
    @Override
    public String toString() {
        return baseForm; // Por ejemplo, solo mostrar el nombre
    }
}

