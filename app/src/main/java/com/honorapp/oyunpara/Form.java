package com.honorapp.oyunpara;

public class Form {
    private boolean kontorMu;
    private String tutar,eposta,iban,telefon;

    public Form(boolean kontorMu, String tutar, String eposta, String iban, String telefon) {
        this.kontorMu = kontorMu;
        this.tutar = tutar;
        this.eposta = eposta;
        this.iban = iban;
        this.telefon = telefon;
    }

    public boolean isKontorMu() {
        return kontorMu;
    }

    public void setKontorMu(boolean kontorMu) {
        this.kontorMu = kontorMu;
    }

    public String getTutar() {
        return tutar;
    }

    public void setTutar(String tutar) {
        this.tutar = tutar;
    }

    public String getEposta() {
        return eposta;
    }

    public void setEposta(String eposta) {
        this.eposta = eposta;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }
}
