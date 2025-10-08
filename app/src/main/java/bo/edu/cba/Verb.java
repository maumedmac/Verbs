package bo.edu.cba;

public class Verb {
    private int id;
    private String baseForm;
    private String pastTense;
    private String pastParticiple;

    private String description;
    private String baseFormSentense;
    private String pastTenseSentense;
    private String pastParticipleSentense;
    private String baseFormSentenseEsp;
    private String pastTenseSentenseEsp;
    private String pastParticipleSentenseEsp;

    public Verb(int id, String baseForm, String pastTense, String pastParticiple, String description, String baseFormSentense, String pastTenseSentense, String pastParticipleSentense, String baseFormSentenseEsp, String pastTenseSentenseEsp, String pastParticipleSentenseEsp) {
        this.id = id;
        this.baseForm = baseForm;
        this.pastTense = pastTense;
        this.pastParticiple = pastParticiple;
        this.description = description;
        this.baseFormSentense = baseFormSentense;
        this.pastTenseSentense = pastTenseSentense;
        this.pastParticipleSentense = pastParticipleSentense;
        this.baseFormSentenseEsp = baseFormSentenseEsp;
        this.pastTenseSentenseEsp = pastTenseSentenseEsp;
        this.pastParticipleSentenseEsp = pastParticipleSentenseEsp;
    }

    public Verb(int id, String baseForm, String pastTense, String pastParticiple, String description) {
        this.id = id;
        this.baseForm = baseForm;
        this.pastTense = pastTense;
        this.pastParticiple = pastParticiple;
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

    public String getPastParticiple() {
        return pastParticiple;
    }

    public String getDescription() {
        return description;
    }
    public String getBaseFormSentense() {
        return baseFormSentense;
    }
    public String getPastTenseSentense() {
        return pastTenseSentense;
    }
    public String getPastParticipleSentense() {
        return pastParticipleSentense;
    }
    public String getBaseFormSentenseEsp() {
        return baseFormSentenseEsp;
    }
    public String getPastTenseSentenseEsp() {
        return pastTenseSentenseEsp;
    }
    public String getPastParticipleSentenseEsp() {
        return pastParticipleSentenseEsp;
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

