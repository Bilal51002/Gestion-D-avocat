package org.baeldung.persistence.model.pfe;

import lombok.Data;


public enum Statut {
    EN_ATTENTE("قيد الانتظار"),
    ACCEPTE("مقبول"),
    REFUSE("مرفوض");

    private final String arabicTranslation;

    Statut(String arabicTranslation) {
        this.arabicTranslation = arabicTranslation;
    }

    /**
     * Retourne la traduction en arabe du statut
     * @return La chaîne de caractères représentant le statut en arabe
     */
    public String getArabicTranslation() {
        return this.arabicTranslation;
    }
}
