package spesa;

import java.io.IOException;
import java.util.*;
import javax.xml.parsers.*;
import spesa.Utente;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class MyParser {

    boolean controllo = false;
    private Utente utentet;
    private Richiesta richiestat;
    private List liste;
    
    public MyParser() {
        liste = new ArrayList();
    }
    
    public Utente parseUtente(String filename) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document document;
        Element root, element;
        NodeList nodelist;
        Utente utente;

        // creazione dell’albero DOM dal documento XML
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        document = builder.parse(filename);
        root = document.getDocumentElement();
        nodelist = root.getElementsByTagName("utente");
        element = (Element) nodelist.item(0);
        utente = getUtente(element);
        utentet = utente;

        return utentet;
    }

    public Richiesta parseRichiesta(String filename) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document document;
        Element root, element;
        NodeList nodelist;
        Richiesta richiesta;

        // creazione dell’albero DOM dal documento XML
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        document = builder.parse(filename);
        root = document.getDocumentElement();
        nodelist = root.getElementsByTagName("richiesta");
        element = (Element) nodelist.item(0);
        element.getNextSibling();
        richiesta = getRichiesta(element);
        richiestat = richiesta;

        return richiestat;
    }

    public Utente parseFileUtente(String filename) throws ParserConfigurationException, SAXException, IOException {

        // estrazione dei valori dell'utente dal file "utente.xml"
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(filename);

        Element root = document.getDocumentElement();
        Utente u = getUtente(root);

        return u;
    }

    public Risposta parseFileRisposta(String filename) throws ParserConfigurationException, SAXException, IOException {

        
        // estrazione dei valori degli elementi "idUtente" e "idRichiesta" dal file "risposta.xml"
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(filename);
        Element root = document.getDocumentElement();

        NodeList list = root.getElementsByTagName("idUtente");
        String idUtente = null;
        if (list != null && list.getLength() > 0) {
            idUtente = list.item(0).getFirstChild().getNodeValue();
        }

        list = root.getElementsByTagName("idRichiesta");
        String idRichiesta = null;
        if (list != null && list.getLength() > 0) {
            idRichiesta = list.item(0).getFirstChild().getNodeValue();
        }
        
        Risposta r = new Risposta(idUtente,idRichiesta,"");

        return r;
    }

    
    /*public Richiesta parseRichiesta(String filename) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document document;
        Element root, element;
        NodeList nodelist;
        Richiesta richiesta;

        // creazione dell’albero DOM dal documento XML
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        document = builder.parse(filename);
        root = document.getDocumentElement();
        nodelist = root.getElementsByTagName("richiesta");
        element = (Element) nodelist.item(0);
        element.getNextSibling();
        richiesta = getRichiesta(element);
        richiestat = richiesta;

        return richiestat;
    }*/
    private Utente getUtente(Element root) {
        String idUtente = getTextValue(root, "idUtente");
        String Username = getTextValue(root, "username");
        String Nome = getTextValue(root, "nome");
        String Cognome = getTextValue(root, "cognome");
        String CodiceFiscale = getTextValue(root, "codiceFiscale");
        String Regione = getTextValue(root, "regione");
        String Via = getTextValue(root, "via");
        String nCivico = getTextValue(root, "nCivico");
        Utente u = new Utente(idUtente, Username, Nome, Cognome, CodiceFiscale, Regione, Via, nCivico);
        return u;
    }

    private Richiesta getRichiesta(Element elementUtente) {
        Richiesta richiesta;
        richiesta = new Richiesta();

        int rifUtente = getIntValue(elementUtente, "rifUtente");
        richiesta.setRifUtente(rifUtente);

        String oraInizio = getTextValue(elementUtente, "oraInizio");
        richiesta.setOraInizio(oraInizio);

        String oraFine = getTextValue(elementUtente, "oraFine");
        richiesta.setOraFine(oraFine);

        String durata = getTextValue(elementUtente, "durata");
        richiesta.setDurata(durata);

        return richiesta;
    }

    // restituisce il valore testuale dell’elemento figlio specificato
    private String getTextValue(Element element, String tag) {
        String value = null;
        NodeList nodelist;
        nodelist = element.getElementsByTagName(tag);
        if (nodelist != null && nodelist.getLength() > 0) {
            try {
                value = nodelist.item(0).getFirstChild().getNodeValue();
            } catch (Exception ex) {
                try {
                    value = nodelist.item(0).getNextSibling().getNodeValue();
                } catch (Exception e) {

                }
            }
            if (value == null) {
                value = "null";
            }
        }

        return value;
    }

    // restituisce il valore intero dell’elemento figlio specificato
    private int getIntValue(Element element, String tag) {
        return Integer.parseInt(getTextValue(element, tag));
    }

    // restituisce il valore numerico dell’elemento figlio specificato
    private float getFloatValue(Element element, String tag) {
        return Float.parseFloat(getTextValue(element, tag));
    }

    //restituisce il valore temporale dell'elemento figlio specificato
    private java.sql.Time getTimeValue(Element element, String tag) {
        String stringa = getTextValue(element, tag);
        DateFormat formato = new SimpleDateFormat("HH:mm:ss");
        java.sql.Time ora = null;
        try {
            ora = new java.sql.Time(formato.parse(stringa).getTime());
        } catch (ParseException ex) {
            Logger.getLogger(MyParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ora;
    }
    
    
    
    /**
     * TOSETTI LUCA
     */
    
    
    
    public List parseProdotto(String filename) throws ParserConfigurationException, SAXException, IOException {
        liste.clear();
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document document;
        Element root, element;
        NodeList nodelist;
        Prodotto prodotto;
        // creazione dell’albero DOM dal documento XML
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        document = builder.parse(filename);

        root = document.getDocumentElement();
        // generazione della lista degli elementi "table"        
        nodelist = root.getElementsByTagName("prodotto");
        if (nodelist != null && nodelist.getLength() > 0) {
            for (int i = 0; i < nodelist.getLength(); i++) {
                // solo la prima table contiene cio che mi interessa
                element = (Element) nodelist.item(i);
                prodotto = getProdotto(element);
                liste.add(prodotto);
            }
        }

        return liste;
    }
    
    public List parseDocument(String filename, String method) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document document;
        Element root, element;
        NodeList nodelist;
        Lista lista;
        // creazione dell’albero DOM dal documento XML
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        document = builder.parse(filename);

        root = document.getDocumentElement();
        // generazione della lista degli elementi "table"        
        nodelist = root.getElementsByTagName("lista");
        if (nodelist != null && nodelist.getLength() > 0) {
            for (int i = 0; i < nodelist.getLength(); i++) {
                // solo la prima table contiene cio che mi interessa
                element = (Element) nodelist.item(i);
                lista = getLista(element, method);
                liste.add(lista);
            }
        }

        return liste;
    }
    
    private Lista getLista(Element element1, String method) {
        Lista lista = null;
        try {
            int rifRichiesta = MyLibXML.getIntValue(element1, "rifRichiesta");
            int rifProdotto = MyLibXML.getIntValue(element1, "rifProdotto");
            int quantita = MyLibXML.getIntValue(element1, "quantita");

            if (method.equals("post")) {

                lista = new Lista(rifRichiesta, rifProdotto, quantita);
            
            }else if (method.equals("put")) {

                int idLista = MyLibXML.getIntValue(element1, "idLista");
                lista = new Lista(idLista, rifRichiesta, rifProdotto, quantita);
            }
            
            
            

        } catch (Exception ex) {

        }
        return lista;

    }


//    private Element getSimpleChild(Element parentElement, String childElementName) {
//        NodeList nodelist = parentElement.getElementsByTagName(childElementName);
//        return (Element) nodelist.item(0);
//    }
//
//    private NodeList getComplexChild(Element parentElement, String childElementName) {
//        NodeList nodelist = parentElement.getElementsByTagName(childElementName);
//        return nodelist;
//    }


    private Prodotto getProdotto(Element element1) {
        Prodotto prodotto;
        try {
            int idProdotto = MyLibXML.getIntValue(element1, "idProdotto");
            String genere = MyLibXML.getTextValue(element1, "genere");
            String etichetta = MyLibXML.getTextValue(element1, "etichetta");
            double costo = MyLibXML.getDoubleValue(element1, "costo");
            String nome = MyLibXML.getTextValue(element1, "nome");
            String marca = MyLibXML.getTextValue(element1, "marca");
            String descrizione = MyLibXML.getTextValue(element1, "descrizione");
            
            prodotto = new Prodotto(idProdotto,genere, etichetta, costo, nome, marca, descrizione);
        return prodotto;
        } catch (Exception ex) {
            String genere = MyLibXML.getTextValue(element1, "genere");
            String etichetta = MyLibXML.getTextValue(element1, "etichetta");
            double costo = MyLibXML.getDoubleValue(element1, "costo");
            String nome = MyLibXML.getTextValue(element1, "nome");
            String marca = MyLibXML.getTextValue(element1, "marca");
            String descrizione = MyLibXML.getTextValue(element1, "descrizione");
            
            prodotto = new Prodotto(genere, etichetta, costo, nome, marca, descrizione);
        return prodotto;
        }

        
    }

}
