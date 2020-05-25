/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spesa;

/**
 *
 * @author Ziz
 */
public class Lista {
    
    private int rifRichiesta, rifProdotto, quantita, idLista;

       public Lista(int rifRichiesta, int rifProdotto, int quantita) {
        
        this.rifRichiesta = rifRichiesta;
        this.rifProdotto = rifProdotto;
        this.quantita = quantita;
    }
    
    public Lista(int idLista, int rifRichiesta, int rifProdotto, int quantita) {
        
        this.idLista = idLista;
        this.rifRichiesta = rifRichiesta;
        this.rifProdotto = rifProdotto;
        this.quantita = quantita;
    }

    public int getRifRichiesta() {
        return rifRichiesta;
    }

    public int getRifProdotto() {
        return rifProdotto;
    }

    public int getQuantita() {
        return quantita;
    }

    public int getIdLista() {
        return idLista;
    }
    
    
    
    
    
}
