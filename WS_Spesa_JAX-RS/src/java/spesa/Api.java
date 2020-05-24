/**
     * TOSETTI LUCA
     * 
     * @GET
     * http://localhost:8080/spesa/risposte
     * http://localhost:8080/spesa/prodotto?genere={genere}&nome={nome}...
     * @POST
     * http://localhost:8080/spesa/prodotto
     * @PUT
     * http://localhost:8080/spesa/prodotto/{idProdotto}
     * @DELETE
     * http://localhost:8080/spesa/prodotto/{idProdotto}
     */

/**
     * SPANGARO FRANCESCO
     * 
     * @GET
     * http://localhost:8080/spesa/richiestaXML/{id}
     * http://localhost:8080/spesa/richiestaJSON/{id}
     * @POST
     * http://localhost:8080/spesa/utenteXML
     * http://localhost:8080/spesa/utenteJSON
     * http://localhost:8080/spesa/richiestaXML
     * http://localhost:8080/spesa/richiestaJSON
     * @DELETE
     * http://localhost:8080/spesa/lista?id={id}
     */

/**
     * GALIMBERTI FRANCESCO
     * 
     * @GET
     * http://localhost:8080/spesa/utenti
     * @POST
     * http://localhost:8080/spesa/risposta
     * @PUT
     * http://localhost:8080/spesa/utenti/{idUtente}
     * @DELETE
     * http://localhost:8080/spesa/richieste/{idRichiesta}
     */

/**
     * ROVELLI ANDREA
     * 
     * http://localhost:8080/spesa/lista?rifRichiesta={id}
     * http://localhost:8080/spesa/lista
     * @PUT
     * http://localhost:8080/spesa/updLista
     */
package spesa;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.*;
import javax.ws.rs.*;
import javax.ws.rs.ext.MessageBodyReader;
import javax.xml.parsers.ParserConfigurationException;
import org.json.JSONObject;
import org.xml.sax.SAXException;
/**
 * REST Web Service
 *
 * @author Galimberti Francesco
 */
@ApplicationPath("")
@Path("")
public class Api extends Application{

    final private String driver = "com.mysql.jdbc.Driver";
    final private String dbms_url = "jdbc:mysql://localhost/";
    final private String database = "db_spesa";
    final private String user = "root";
    final private String password = "";
    private Connection spesaDatabase;
    private boolean connected;

    // attivazione servlet (connessione a DBMS)
    public void init() {
        String url = dbms_url + database;
        try {
            Class.forName(driver);
            spesaDatabase = DriverManager.getConnection(url, user, password);
            connected = true;
        } catch (SQLException e) {
            connected = false;
        } catch (ClassNotFoundException e) {
            connected = false;
        }
    }

    // disattivazione servlet (disconnessione da DBMS)
    public void destroy() {
        try {
            spesaDatabase.close();
        } catch (SQLException e) {
        }
    }

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of Api
     */
    public Api() {
        super();
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("prova")
    public Response getMessage() {
        Response r = Response.ok("test with GET")
                             .build();
        return r;
    }

    /*
     * http://localhost:8080/spesa/utenti
     * http://localhost:8080/spesa/utenti?username=fraGali
     * http://localhost:8080/spesa/utenti?nome=Francesco
     * http://localhost:8080/spesa/utenti?cognome=Caso
     * http://localhost:8080/spesa/utenti?regione=Lombardia
     */
    @GET
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("utenti")
    public Response getUtenti(@PathParam(value = "utenti") String utenti,
            @DefaultValue("null") @QueryParam("username") String username,
            @DefaultValue("null") @QueryParam("nome") String nome,
            @DefaultValue("null") @QueryParam("cognome") String cognome,
            @DefaultValue("null") @QueryParam("regione") String regione) {

        init();
        String output = "";
        Response r;

        // verifica stato connessione a DBMS
        if (!connected) {

            r = Response.serverError().build();
            return r;

        } else {

            try {
                String sql = "SELECT idUtente, username, nome, cognome, codiceFiscale, regione, via, nCivico FROM utenti";

                if (!username.equalsIgnoreCase("null")) {
                    sql += " WHERE username='" + username + "';";
                }

                if (!nome.equalsIgnoreCase("null")) {
                    sql += " WHERE nome='" + nome + "';";
                }

                if (!cognome.equalsIgnoreCase("null")) {
                    sql += " WHERE cognome='" + cognome + "';";
                }

                if (!regione.equalsIgnoreCase("null")) {
                    sql += " WHERE regione='" + regione + "';";
                }

                // ricerca nominativo nel database
                Statement statement = spesaDatabase.createStatement();
                ResultSet result = statement.executeQuery(sql);

                ArrayList<Utente> utentiList = new ArrayList<Utente>(0);
                while (result.next()) {
                    String idUtente = result.getString("idUtente");
                    String Username = result.getString("username");
                    String Nome = result.getString("nome");
                    String Cognome = result.getString("cognome");
                    String CodiceFiscale = result.getString("codiceFiscale");
                    String Regione = result.getString("regione");
                    String Via = result.getString("via");
                    String nCivico = result.getString("nCivico");
                    Utente u = new Utente(idUtente, Username, Nome, Cognome, CodiceFiscale, Regione, Via, nCivico);
                    utentiList.add(u);
                }
                result.close();
                statement.close();

                output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                output += "<elencoUtenti>";
                if (utentiList.size() > 0) {
                    for (int i = 0; i < utentiList.size(); i++) {
                        Utente u = utentiList.get(i);
                        output += "<Utente>";
                        output += "<idUtente>" + u.getIdUtente() + "</idUtente>";
                        output += "<username>" + u.getUsername() + "</username>";
                        output += "<nome>" + u.getNome() + "</nome>";
                        output += "<cognome>" + u.getCognome() + "</cognome>";
                        output += "<codiceFiscale>" + u.getCodiceFiscale() + "</codiceFiscale>";
                        output += "<regione>" + u.getRegione() + "</regione>";
                        output += "<via>" + u.getVia() + "</via>";
                        output += "<nCivico>" + u.getnCivico() + "</nCivico>";
                        output += "</Utente>";
                    }
                    utentiList = new ArrayList<Utente>(0);
                }
                output += "</elencoUtenti>";
                destroy();
                
                r = Response.ok(output).build();
                return r;
                
            } catch (SQLException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.serverError().build();
                return r;
            }   
            
        }
    }

    /**
     * AGGIORNAMENTO DI UN UTENTE con idUtente nell'url e nel body xmlUtente
     *
     * PUT spesa/utenti/1
     *
     * body examples
      <utente>
      <idUtente>1</idUtente>
      <username>fraGali</username>
      <nome>Francesco</nome>
      <cognome>Galimberti</cognome>
      <codiceFiscale>GLMFNC01A02B729Q</codiceFiscale>
      <regione>Lombardia</regione>
      <via>Giacomo Leopardi</via>
      <nCivico>5</nCivico>
      </utente>
     */
    @PUT
    @Path("utenti/{idUtente}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.TEXT_XML})
    public String putUtente(@PathParam("idUtente") String idUtente,
            String content) {
        // verifica stato connessione a DBMS
        init();
        MyParser myParse;

        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        } else {
            try {

                BufferedWriter file;
                file = new BufferedWriter(new FileWriter("utente.xml"));
                file.write(content);
                file.flush();
                file.close();

                myParse = new MyParser();
                Utente u = myParse.parseFileUtente("utente.xml");

                if (u.getIdUtente() == null || u.getNome() == null || u.getCognome() == null || u.getCognome() == null || u.getCodiceFiscale() == null || u.getRegione() == null || u.getnCivico() == null || u.getVia() == null || u.getUsername() == null) {
                    return "<errorMessage>400</errorMessage>";
                }
                if (u.getIdUtente().isEmpty() || u.getNome().isEmpty() || u.getCognome().isEmpty() || u.getCognome().isEmpty() || u.getCodiceFiscale().isEmpty() || u.getRegione().isEmpty() || u.getnCivico().isEmpty() || u.getVia().isEmpty() || u.getUsername().isEmpty()) {
                    return "<errorMessage>400</errorMessage>";
                }
                if (!u.getIdUtente().equalsIgnoreCase(idUtente)) {
                    return "<errorMessage>400</errorMessage>";
                }

                Statement statement = spesaDatabase.createStatement();
                String sql = "UPDATE utenti SET username='" + u.getUsername() + "', nome='" + u.getNome() + "', cognome='" + u.getCognome() + "', codiceFiscale='" + u.getCodiceFiscale() + "', regione='" + u.getRegione() + "', via='" + u.getVia() + "', nCivico='" + u.getnCivico() + "' WHERE idUtente = " + u.getIdUtente() + ";";

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>404</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Update avvenuto correttamente</message>";

            } catch (IOException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>404</errorMessage>";
            } catch (SQLException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>500</errorMessage>";
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>500</errorMessage>";
            } catch (SAXException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        }
    }

    /**
     * ELIMINAZIONE DI UNA RICHIESTA DI SPESA con idRichiesta nell'url
     *
     * DELETE spesa/richieste/1
     */
    @DELETE
    @Path("richieste/{idRichiesta}")
    @Consumes(MediaType.TEXT_XML)
    public String doDelete(@PathParam("idRichiesta") String idRichiesta) {

        init();

        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        } else {
            if (idRichiesta == null) {
                return "<errorMessage>400</errorMessage>";
            }
            if (idRichiesta.isEmpty()) {
                return "<errorMessage>400</errorMessage>";
            }

            try {
                Statement statement = spesaDatabase.createStatement();
                String sql = "DELETE FROM richieste WHERE idRichiesta = " + idRichiesta + ";";

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Eliminazione avvenuta correttamente</message>";

            } catch (SQLException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * INSERIMENTO RISPOSTA AD UNA RISCHIESTA DI SPESA in body risposta POST
     * spesa/risposte
     *
     * <risposta>
     * <idUtente>1</idUtente>
     * <idRichiesta>2</idRichiesta>
     * </risposta>
     */
    @POST
    @Consumes(MediaType.TEXT_XML)
    @Path("risposta")
    public String postRisposta(String content) {

        // verifica stato connessione a DBMS
        init();
        MyParser myParse;

        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        } else {

            try {
                BufferedWriter file;
                file = new BufferedWriter(new FileWriter("risposta.xml"));
                file.write(content);
                file.flush();
                file.close();

                myParse = new MyParser();
                Risposta r = myParse.parseFileRisposta("risposta.xml");

                if (r.getIdUtente() == null || r.getIdRichiesta() == null) {
                    return "<errorMessage>400</errorMessage>";
                }
                if (r.getIdUtente().isEmpty() || r.getIdRichiesta().isEmpty()) {
                    return "<errorMessage>400</errorMessage>";
                }

                try {
                    // aggiunta voce nel database
                    Statement statement = spesaDatabase.createStatement();
                    String sql ="INSERT risposte(rifUtente, rifRichiesta) VALUES(" + r.getIdUtente() + ", " + r.getIdRichiesta() + ");";
                    
                    if (statement.executeUpdate(sql) <= 0) {                        
                        statement.close();
                        return "<errorMessage>403</errorMessage>";
                    }
                    
                    statement.close();
                    destroy();
                    return "<message>Update avvenuto correttamente</message>";
                } catch (SQLException ex) {
                    Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                    destroy();
                    return "<errorMessage>500</errorMessage>";
                }
            } catch (IOException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>404</errorMessage>";
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>404</errorMessage>";
            } catch (SAXException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>404</errorMessage>";
            }

        }        
    }
    
    /**
     * @author Tosetti_Luca
     * 
     * Visualizza i dati relativi alle richieste memorizzate nel database oppure di uno specifico utente andando a specificare l'id di tale utente
     * come parametro query
     * @param id ID dell'utente del quale si vogliono ottenere le varie richieste
     * @return Output XML contenente le informazioni relative a una o più richieste / Output messaggio di errore
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("risposte")
    public String getRisposte(@QueryParam("id") String id) {
        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        }
        
        try {
            String sql = "SELECT idRisposta,rifRichiesta FROM risposte WHERE";
            if (id != null && id.isEmpty()) {
                sql = sql + " rifUtente='" + id + "' AND";
            }

            sql = sql + " 1";
            Statement statement = spesaDatabase.createStatement();
            ResultSet result = statement.executeQuery(sql);
            
            ArrayList<Risposta> risp = new ArrayList<Risposta>();
            while (result.next()) {
                String rispID = result.getString(1);
                String rispRifRichiesta = result.getString(2);

                risp.add(new Risposta(id, rispRifRichiesta,rispID));

            }
            
            if (risp.size() > 0) {
                result.close();
                statement.close();

                output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                output = output + "<return>\n";

                for (int i = 0; i < risp.size(); i++) {
                    output = output + "<risposta>\n";
                    output = output + "<idRisposta>" + risp.get(i).getIdRisposta()+ "</idRisposta>\n";
                    output = output + "<idUtente>" + risp.get(i).getIdUtente() + "</idUtente>\n";
                    output = output + "<idRichiesta>" + risp.get(i).getIdRichiesta()+ "</idRichiesta>\n";
                    output = output + "</risposta>\n";
                }

                output = output + "</return>\n";
            } else {
                result.close();
                statement.close();
                destroy();
                return "<errorMessage>404</errorMessage>";
            }
        } catch (SQLException ex) {
            destroy();
            return "<errorMessage>500</errorMessage>";
        }
        destroy();
        return output;
    }
    
/**  
 * @author Tosetti_Luca
 * 
 * Visualizza i dati relativi ai prodotti memorizzati nel database permettendo di filtrare i risultati ottenuti
 * attraverso vari parametri di query.
 * 
 * @param genere Parametro query che permette di specificare il genere dei prodotti che si vogliono visualizzare
 * @param etichetta Parametro query che permette di specificare l'etichetta dei prodotti che si vogliono visualizzare
 * @param costo Parametro query che permette di specificare il costo dei prodotti che si vogliono visualizzare
 * @param nome Parametro query che permette di specificare il nome dei prodotti che si vogliono visualizzare
 * @param marca Parametro query che permette di specificare la marca dei prodotti che si vogliono visualizzare 
 * @param descrizione Parametro query che permette di specificare la descrizione dei prodotti che si vogliono visualizzare
 * @return Output XML contenente le informazioni relative a uno o più prodotti / Output messaggio di errore
 */
    @GET
    @Produces(MediaType.TEXT_XML)
    @Path("prodotto")
    public String getProdotto(@QueryParam("genere") String genere, @QueryParam("etichetta") String etichetta, @QueryParam("costo") double costo, @QueryParam("nome") String nome, @QueryParam("marca") String marca, @QueryParam("descrizione") String descrizione) {
        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        }

        try {
            String sql = "SELECT * FROM prodotti WHERE";
            if (genere != null) {
                sql = sql + " genere='" + genere + "' AND";
            }
            if (etichetta != null) {
                sql = sql + " etichetta='" + etichetta + "' AND";
            }
            if (costo != 0.00) {
                sql = sql + " costo='" + costo + "' AND";
            }
            if (nome != null) {
                sql = sql + " nome='" + nome + "' AND";
            }
            if (marca != null) {
                sql = sql + " marca='" + marca + "' AND";
            }

            sql = sql + " 1";
            Statement statement = spesaDatabase.createStatement();
            ResultSet result = statement.executeQuery(sql);

            ArrayList<Prodotto> prd = new ArrayList<Prodotto>();
            while (result.next()) {
                int prdID = result.getInt(1);
                String prdGenere = result.getString(2);
                String prdEtichetta = result.getString(3);
                double prdCosto = result.getDouble(4);
                String prdNome = result.getString(5);
                String prdMarca = result.getString(6);
                String prdDescrizione = result.getString(7);

                prd.add(new Prodotto(prdID, prdGenere, prdEtichetta, prdCosto, prdNome, prdMarca, prdDescrizione));

            }

            if (prd.size() > 0) {
                result.close();
                statement.close();

                output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                output = output + "<return>\n";

                for (int i = 0; i < prd.size(); i++) {
                    output = output + "<prodotto>\n";
                    output = output + "<idProdotto>" + prd.get(i).getIdProdotto() + "</idProdotto>\n";
                    output = output + "<genere>" + prd.get(i).getGenere() + "</genere>\n";
                    output = output + "<etichetta>" + prd.get(i).getEtichetta() + "</etichetta>\n";
                    output = output + "<costo>" + prd.get(i).getCosto() + "</costo>\n";
                    output = output + "<nome>" + prd.get(i).getNome() + "</nome>\n";
                    output = output + "<marca>" + prd.get(i).getMarca() + "</marca>\n";
                    output = output + "<descrizione>" + prd.get(i).getDescrizione() + "</descrizione>\n";
                    output = output + "</prodotto>\n";
                }

                output = output + "</return>\n";
            } else {
                result.close();
                statement.close();
                destroy();
                return "<errorMessage>404</errorMessage>";
            }
        } catch (SQLException ex) {
            destroy();
            return "<errorMessage>500</errorMessage>";
        }
        destroy();
        return output;
    }

    /**
     * @author Tosetti_Luca
     * 
     * Consente l'inserimento di nuovi prodotti all'interno del database
     * 
     * @param content Body della richiesta POST https/https contenente il/i nuovo/i prodotto/i da dover memorizzare sottoforma di XML
     * @return Output messaggio di successo / Output messaggio di errore
     */
    @POST
    @Consumes(MediaType.TEXT_XML)
    @Path("prodotto")
    public String postProdotto(String content) {
        try {
            init();

            MyParser myParse = new MyParser();
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("prodotti.xml"));
            writer.write(content);
            writer.flush();
            writer.close();

            ArrayList<Prodotto> prodotti = (ArrayList<Prodotto>) myParse.parseProdotto("prodotti.xml");
            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }

            try {
                String sql = "INSERT INTO prodotti (genere,etichetta,costo,nome,marca,descrizione) VALUES ('" + prodotti.get(0).getGenere() + "','" + prodotti.get(0).getEtichetta() + "','" + prodotti.get(0).getCosto() + "','" + prodotti.get(0).getNome() + "','" + prodotti.get(0).getMarca() + "','" + prodotti.get(0).getDescrizione() + "')";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Inserimento avvenuto correttamente</message>";
            } catch (SQLException ex) {
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        } catch (IOException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }

    /**
     * @author Tosetti_Luca
     * 
     * Consente la modifica di un di un determinato prodotto andando a specificarne l'ID tramite il percorso
     * @param content Body della richiesta PUT http/https contenente i nuovi valori degli attributi del prodotto specificato nel percorso sottoforma di XML
     * @return Output messaggio di successo / Output messaggio di errore
     */
    @PUT
    @Consumes(MediaType.TEXT_XML)
    @Path("prodotto/{idProdotto}")
    public String putProdotto(@PathParam("idProdotto") String idProdotto,String content) {
        try {
            init();

            MyParser myParse = new MyParser();
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("prodotti.xml"));
            writer.write(content);
            writer.flush();
            writer.close();

            ArrayList<Prodotto> prodotto = (ArrayList<Prodotto>) myParse.parseProdotto("prodotti.xml");
            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }
            
            if(prodotto.get(0).getGenere()==null || prodotto.get(0).getEtichetta()==null || prodotto.get(0).getNome()==null || prodotto.get(0).getMarca()==null || prodotto.get(0).getCosto()==0.00 || prodotto.get(0).getDescrizione()==null) {
                return "<errorMessage>400</errorMessage>";
            }
            if(prodotto.get(0).getGenere().isEmpty() || prodotto.get(0).getEtichetta().isEmpty() || prodotto.get(0).getNome().isEmpty() || prodotto.get(0).getMarca().isEmpty() || prodotto.get(0).getDescrizione().isEmpty()) {
                return "<errorMessage>400</errorMessage>";
            }

            try {
                String sql = "UPDATE prodotti SET nome='" + prodotto.get(0).getNome() + "', genere='" + prodotto.get(0).getGenere() + "', etichetta='" + prodotto.get(0).getEtichetta() + "', costo='" + prodotto.get(0).getCosto() + "', nome='" + prodotto.get(0).getNome() + "', marca='" + prodotto.get(0).getMarca() + "', descrizione='" + prodotto.get(0).getDescrizione() + "' WHERE idProdotto='" + idProdotto + "'";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Update avvenuto correttamente</message>";
            } catch (SQLException ex) {
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        } catch (IOException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }

    /**
     * @author Tosetti_Luca
     * 
     * Consente di eliminare un prodotto andando a specificarne l'ID tramite il percorso
     * @param id ID del prodotto da eliminare
     * @return Output messaggio di successo / Output messaggio di errore
     */
    @DELETE
    @Path("prodotto/{idProdotto}")
    public String deleteProdotto(@PathParam("idProdotto") int id) {
        init();

        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        }

        if (id != 0) {
            try {
                String sql = "DELETE FROM prodotti WHERE idProdotto='" + id + "'";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Eliminazione avvenuta correttamente</message>";
            } catch (SQLException ex) {
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        } else {
            return "<errorMessage>406</errorMessage>";
        }

    }
    
    /**
    *SPANGARO FRANCESCO
    *visualizza i dati di una richiesta da id fornito nella path in formato xml come definito nella progettazione api
    *@param id è l'id su cui si baserà la ricerca
    * @return varie tipologie di ritorno, conferma se corretto, altrimenti messaggi di errore corrispondenti
     */
    @GET
    @Path("richiestaXML/{id}")
    @Produces(MediaType.TEXT_XML)
    public String getRichiestaXMLDaId(@PathParam("id") String id) {
        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        } else {
            try {
                Richiesta richiesta = new Richiesta();
                String sql = "SELECT rifUtente, oraInizioConsegna, oraFineConsegna, durataRichiesta FROM richieste where idRichiesta ='" + id + "'";
                Statement statement = spesaDatabase.createStatement();
                ResultSet result = statement.executeQuery(sql);

                result.next();
                richiesta.setRifUtente(result.getInt(1));
                richiesta.setOraInizio(result.getString(2));
                richiesta.setOraFine(result.getString(3));
                richiesta.setDurata(result.getString(4));

                result.close();
                statement.close();

                if (richiesta.getRifUtente() != 0) {
                    output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                    output = output + "<richiesta>\n";
                    output = output + "<rifUtente>" + richiesta.getRifUtente() + "</rifUtente>\n";
                    output = output + "<oraInizio>" + richiesta.getOraInizio() + "</oraInizio>\n";
                    output = output + "<oraFine>" + richiesta.getOraFine() + "</oraFine>\n";
                    output = output + "<durata>" + richiesta.getDurata() + "</durata>\n";
                    output = output + "</richiesta>";

                } else {
                    destroy();
                    return "<errorMessage>404</errorMessage>";
                }

            } catch (SQLException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
            destroy();
            return output;
        }
    }

    /**
    *SPANGARO FRANCESCO
    *visualizza i dati di una richiesta da id fornito nella path in formato JSON come definito nella progettazione api
    *@param id è l'id su cui si baserà la ricerca
    *@return varie tipologie di ritorno, conferma se corretto, altrimenti messaggi di errore corrispondenti
    */
    @GET
    @Path("richiestaJSON/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getRichiestaJSONDaId(@PathParam("id") String id) {
        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        } else {
            try {
                Richiesta richiesta = new Richiesta();
                String sql = "SELECT rifUtente, oraInizioConsegna, oraFineConsegna, durataRichiesta FROM richieste where idRichiesta ='" + id + "'";
                Statement statement = spesaDatabase.createStatement();
                ResultSet result = statement.executeQuery(sql);

                result.next();
                richiesta.setRifUtente(result.getInt(1));
                richiesta.setOraInizio(result.getString(2));
                richiesta.setOraFine(result.getString(3));
                richiesta.setDurata(result.getString(4));

                result.close();
                statement.close();

                if (richiesta.getRifUtente() != -1) {
                    output = "{\"richiesta\":{\n";
                    output = output + "\"rifUtente\":\"" + richiesta.getRifUtente() + "\",\n";
                    output = output + "\"oraInizio\":\"" + richiesta.getOraInizio() + "\",\n";
                    output = output + "\"oraFine\":\"" + richiesta.getOraFine() + "\",\n";
                    output = output + "\"durata\":\"" + richiesta.getDurata() + "\"\n";
                    output = output + "}\n}";

                } else {
                    destroy();
                    return "<errorMessage>404</errorMessage>";
                }

            } catch (SQLException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
            destroy();
            return output;
        }
    }

    /**
    *SPANGARO FRANCESCO
    *inserisce i dati di un utente fornito nel body in formato XML come definito nella progettazione api
    *@param content sono i dati inviati dall'utilizzatore, salvato nella cartella server xampp/tomcat/bin/utente.xml
    *@return varie tipologie di ritorno, conferma se corretto, altrimenti messaggi di errore corrispondenti
    */
    @POST
    @Path("utenteXML")
    @Consumes(MediaType.TEXT_XML)
    public String postUtenteXML(String content) {
        init();
        try {
            String xsdFile = "\\xml\\utente.xsd";
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("utente.xml"));
            writer.write(content);
            writer.flush();
            writer.close();
            Utente utente = new Utente();

            /*try {
                MyValidator.validate("entry.xml", xsdFile);
            } catch (SAXException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                return "<errorMessage>400 Malformed XML</errorMessage>";
            }*/
            MyParser parse = new MyParser();
            utente = parse.parseUtente("utente.xml");
            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }
            String sql = "INSERT INTO utenti(username, nome, cognome, password, codiceFiscale, regione, via, nCivico) VALUES('" + utente.getUsername() + "', '" + utente.getNome() + "', '" + utente.getCognome() + "', '" + utente.getPassword() + "', '" + utente.getCodiceFiscale() + "', '" + utente.getRegione() + "', '" + utente.getVia() + "', '" + utente.getnCivico() + "')";
            Statement statement = spesaDatabase.createStatement();

            if (statement.executeUpdate(sql) <= 0) {
                statement.close();
                return "<errorMessage>403</errorMessage>";
            }

            statement.close();
            destroy();
            return "<message>Inserimento avvenuto correttamente</message>";

        } catch (IOException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }

    /**
    *SPANGARO FRANCESCO
    *inserisce i dati di un utente fornito nel body in formato JSON come definito nella progettazione api
    *@param content sono i dati inviati dall'utilizzatore, parsati dal metodo (libreria usata: json-20190722.jar)
    *@return varie tipologie di ritorno, conferma se corretto, altrimenti messaggi di errore corrispondenti
    */
    @POST
    @Path("utenteJSON")
    @Consumes(MediaType.APPLICATION_JSON)
    public String postUtenteJSON(String content) {
        init();
        try {
            JSONObject obj = new JSONObject(content);
            Utente utente = new Utente();
            utente.setUsername(obj.getJSONObject("utente").getString("username"));
            utente.setNome(obj.getJSONObject("utente").getString("nome"));
            utente.setCognome(obj.getJSONObject("utente").getString("cognome"));
            utente.setPassword(obj.getJSONObject("utente").getString("password"));
            utente.setCodiceFiscale(obj.getJSONObject("utente").getString("codiceFiscale"));
            utente.setRegione(obj.getJSONObject("utente").getString("regione"));
            utente.setVia(obj.getJSONObject("utente").getString("via"));
            utente.setnCivico(obj.getJSONObject("utente").getString("nCivico"));

            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }
            String sql = "INSERT INTO utenti(username, nome, cognome, password, codiceFiscale, regione, via, nCivico) VALUES('" + utente.getUsername() + "', '" + utente.getNome() + "', '" + utente.getCognome() + "', '" + utente.getPassword() + "', '" + utente.getCodiceFiscale() + "', '" + utente.getRegione() + "', '" + utente.getVia() + "', '" + utente.getnCivico() + "')";
            Statement statement = spesaDatabase.createStatement();

            if (statement.executeUpdate(sql) <= 0) {
                statement.close();
                return "<errorMessage>403</errorMessage>";
            }

            statement.close();
            destroy();
            return "<message>Inserimento avvenuto correttamente</message>";

        } catch (SQLException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }

    /**
    *SPANGARO FRANCESCO
    *inserisce i dati di una richiesta fornita nel body in formato XML come definito nella progettazione api
    *@param content sono i dati inviati dall'utilizzatore, salvato nella cartella server xampp/tomcat/bin/richiesta.xml
    *@return varie tipologie di ritorno, conferma se corretto, altrimenti messaggi di errore corrispondenti
    */
    @POST
    @Path("richiestaXML")
    @Consumes(MediaType.TEXT_XML)
    public String postRichiestaXML(String content) {
        init();
        try {
            String xsdFile = "\\xml\\richiesta.xsd";
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("richiesta.xml"));
            writer.write(content);
            writer.flush();
            writer.close();
            Richiesta richiesta = new Richiesta();

            /*try {
                MyValidator.validate("entry.xml", xsdFile);
            } catch (SAXException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                return "<errorMessage>400 Malformed XML</errorMessage>";
            }*/
            MyParser parse = new MyParser();
            richiesta = parse.parseRichiesta("richiesta.xml");
            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }
            String sql = "INSERT INTO richieste(rifUtente, oraInizioConsegna, oraFineConsegna, durataRichiesta) VALUES(" + richiesta.getRifUtente() + ", '" + richiesta.getOraInizio() + "', '" + richiesta.getOraFine() + "', '" + richiesta.getDurata() + "')";
            Statement statement = spesaDatabase.createStatement();

            if (statement.executeUpdate(sql) <= 0) {
                statement.close();
                return "<errorMessage>403</errorMessage>";
            }

            statement.close();
            destroy();
            return "<message>Inserimento avvenuto correttamente</message>";

        } catch (IOException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }

    /**
    *SPANGARO FRANCESCO
    *inserisce i dati di una richiesta fornita nel body in formato JSON come definito nella progettazione api
    *@param content sono i dati inviati dall'utilizzatore, parsati dal metodo (libreria usata: json-20190722.jar)
    *@return varie tipologie di ritorno, conferma se corretto, altrimenti messaggi di errore corrispondenti
    */
    @POST
    @Path("richiestaJSON")
    @Consumes(MediaType.APPLICATION_JSON)
    public String postRichiestaJSON(String content) {
        init();
        try {
            JSONObject obj = new JSONObject(content);
            Richiesta richiesta = new Richiesta();
            richiesta.setRifUtente(obj.getJSONObject("richiesta").getInt("rifUtente"));
            richiesta.setOraInizio(obj.getJSONObject("richiesta").getString("oraInizio"));
            richiesta.setOraFine((obj.getJSONObject("richiesta").getString("oraFine")));
            richiesta.setDurata((obj.getJSONObject("richiesta").getString("durata")));

            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }
            String sql = "INSERT INTO richieste(rifUtente, oraInizioConsegna, oraFineConsegna, durataRichiesta) VALUES(" + richiesta.getRifUtente() + ", '" + richiesta.getOraInizio() + "', '" + richiesta.getOraFine() + "', '" + richiesta.getDurata() + "')";
            Statement statement = spesaDatabase.createStatement();

            if (statement.executeUpdate(sql) <= 0) {
                statement.close();
                return "<errorMessage>403</errorMessage>";
            }

            statement.close();
            destroy();
            return "<message>Inserimento avvenuto correttamente</message>";

        } catch (SQLException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }

    /**
    *SPANGARO FRANCESCO
    *cancella una lista dal database, lista che corrisponde all'id inserito come parametro della query
    *@param id è l'id su cui si deve basare per fare la ricerca
    *@return varie tipologie di ritorno, conferma se corretto, altrimenti messaggi di errore corrispondenti
    */
    @DELETE
    @Path("lista")
    public String deleteLista(@QueryParam("id") int id) {
        init();

        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        }
        try {
            String sql = "DELETE FROM liste WHERE idLista='" + id + "'";
            Statement statement = spesaDatabase.createStatement();

            if (statement.executeUpdate(sql) <= 0) {
                statement.close();
                return "<errorMessage>403</errorMessage>";
            }

            statement.close();
            destroy();
            return "<message>Eliminazione avvenuta correttamente</message>";
        } catch (SQLException ex) {
            destroy();
            return "<errorMessage>500</errorMessage>";
        }
    }

    /**
    *SPANGARO FRANCESCO
    *Metodo per il casting delle stringhe restituite dal database,
    *da String a Time
    */
    public java.sql.Time getTime(String stringa) {
        DateFormat formato = new SimpleDateFormat("HH:mm:ss");
        java.sql.Time ora = null;
        try {
            ora = new java.sql.Time(formato.parse(stringa).getTime());
        } catch (ParseException ex) {
            Logger.getLogger(MyParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ora;
    }

}
