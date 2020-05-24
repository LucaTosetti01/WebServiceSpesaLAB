/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spesa;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author prof
 */
public class MyLibXML {
    
    /**
     * Restituisce il valore, sottoforma di stringa del contenuto del tag
     * specificato<br>
     *
     * Esempio di funzionamento:<br><br>
     * (cassonetto) <br>
     * &thinsp;&thinsp;(tipologia)organico(/tipologia)<br>
     * &thinsp;&thinsp;(dataSvuotamento)<br>
     * &thinsp;&thinsp;..........<br>
     * (/cassonetto)<br><br>
     *
     * element: (cassonetto)<br>
     * tag: "tipologia"<br>
     *
     * Verrà restituito il valore "organico" in questo caso
     *
     * @param element Elemento della NodeList che contiene il tag del quale si
     * vuole ottenere il valore
     * @param tag Stringa che contiene il nome del tag del quale si vuole
     * ottenere il valore
     * @return value: Valore contenuto nel tag specificato come parametro e
     * ritornato sotto forma di stringa<br>
     *
     * @see getAllTextValue
     * @see getTextValuePosItem
     */
    public static String getTextValue(Element element, String tag) {
        String value = null;
        NodeList nodelist;
        nodelist = element.getElementsByTagName(tag);
        if (nodelist != null && nodelist.getLength() > 0) {
            value = nodelist.item(0).getFirstChild().getNodeValue();
        }
        System.out.println("getTextValue(Element element," + " tag=" + tag + ") = " + value);
        return value;
    }
    
    /**
     * Restituisce il valore testuale dell’elemento figlio specificato in
     * posizione position nella lista dei figli<br>
     * Si utilizza principalmente per poter accedere ad un preciso tag quando ve
     * ne sono molti con lo stesso nome<br>
     * Esempio di funzionamento:<br><br>
     * (table) <br>
     * &thinsp;&thinsp;(tr)<br>
     * &thinsp;&thinsp;&thinsp;&thinsp;(td)Contenuto1(/td)<br>
     * &thinsp;&thinsp;&thinsp;&thinsp;(td)Contenuto2(/td)<br>
     * &thinsp;&thinsp;&thinsp;&thinsp;(td)Contenuto3(/td)<br>
     * &thinsp;&thinsp;(/tr)<br>
     * &thinsp;&thinsp;..........<br>
     * (/table)
     *
     * <br><br>
     *
     * element: (tr)<br>
     * tag: "td"<br>
     * position: 2<br>
     *
     * Verrà restituito il valore "Contenuto2" in questo caso
     *
     * @param element Elemento della NodeList che contiene il tag del quale si
     * vuole ottenere il valore
     * @param tag Stringa che contiene il nome del tag del quale si vuole
     * ottenere il valore
     * @param position Posizione che indica di quale tag tra quelli con il nome
     * specificato si vuole ottenerne un valore
     * @return value: Valore contenuto nel tag e alla posizione specificati come
     * parametri e ritornato sotto forma di stringa<br>
     *
     * @see getAllTextValue
     * @see getTextValue
     */
    public static String getTextValuePosItem(Element element, String tag, int position) {
        String value = "";
        if (element != null) {
            NodeList nodelist = element.getElementsByTagName(tag);

            if (nodelist != null && nodelist.getLength() > 0) {
                if ((nodelist.item(position) != null) && (nodelist.item(position).getChildNodes().getLength() > 0)) {
                    value = nodelist.item(position).getFirstChild().getNodeValue();
                }
            }
        }
        System.out.println("getTextValuePosItem(Element element," + " tag=" + tag + ", position=" + position + ") = " + value);  
        return value;
    }

    /**
     * Restituisce il valore intero del valore testuale ritornato tramite il
     * metodo getTextValue
     *
     * @param element Elemento della NodeList che contiene il tag del quale si
     * vuole ottenere il valore intero
     * @param tag Stringa che contiene il nome del tag del quale si vuole
     * ottenere il valore intero
     * @return value: Valore intero contenuto nel tag specificato
     *
     * @see getTextValue
     */
    public static int getIntValue(Element element, String tag) {
        return Integer.parseInt(getTextValue(element, tag));
    }
    
    /**
     * Restituisce il valore float del valore testuale ritornato tramite il
     * metodo getTextValue
     *
     * @param element Elemento della NodeList che contiene il tag del quale si
     * vuole ottenere il valore float
     * @param tag Stringa che contiene il nome del tag del quale si vuole
     * ottenere il valore float
     * @return value: Valore float contenuto nel tag specificato
     *
     * @see getTextValue
     */
    public static float getFloatValue(Element element, String tag) {
        return Float.parseFloat(getTextValue(element, tag));
    }  
    
    /**
     * Restituisce il valore dell'attributo con il nome specificato
     * dell'elemento passato come parametro<br><br>
     * 
     *Esempio di funzionamento:<br>
     * element: volume<br>
     * attributeName: unitàMisura<br><br>
     * 
     * (cassonetto)<br>
     * &thinsp;&thinsp;.........<br>
     * &thinsp;&thinsp;(volume unitàMisura="m^3")300(/volume)<br>
     * &thinsp;&thinsp;.........<br>
     * (/cassonetto)<br><br>
     * 
     * In questo caso verrebbe restituito "m^3"
     * 
     * @param element Elemento della NodeList che contiene l'attributo di cui si
     * vuole ottenere il valore
     * @param attributeName Stringa che contiene il nome dell'attributo del
     * quale si vuole ottenere il valore
     * @return value: Valore contenuto nell'attributo specificato
     *
     */
    public static String getAttribute(Element element, String attributeName) {
        return element.getAttribute(attributeName);
    }
    
    /**
     * Restituisce il valore double del valore testuale ritornato tramite il
     * metodo getTextValue
     *
     * @param element Elemento della NodeList che contiene il tag del quale si
     * vuole ottenere il valore double
     * @param tag Stringa che contiene il nome del tag del quale si vuole
     * ottenere il valore double
     * @return value: Valore double contenuto nel tag specificato
     *
     * @see getTextValue
     */
    public static double getDoubleValue(Element element, String tag) {
        return Double.parseDouble(getTextValue(element,tag));
    }
}
