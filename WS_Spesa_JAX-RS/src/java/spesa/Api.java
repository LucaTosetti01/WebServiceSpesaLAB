/**
 * TOSETTI LUCA
 *
 * @GET
 * http://localhost:8080/spesa/risposte
 * http://localhost:8080/spesa/prodotto?genere={genere}&nome={nome}...
 * @POST http://localhost:8080/spesa/prodotto
 * @PUT http://localhost:8080/spesa/prodotto/{idProdotto}
 * @DELETE http://localhost:8080/spesa/prodotto/{idProdotto}
 */
/**
 * SPANGARO FRANCESCO
 *
 * @GET
 * http://localhost:8080/spesa/richiestaXML/{id}
 * http://localhost:8080/spesa/richiestaJSON/{id}
 * @POST http://localhost:8080/spesa/utenteXML
 * http://localhost:8080/spesa/utenteJSON
 * http://localhost:8080/spesa/richiestaXML
 * http://localhost:8080/spesa/richiestaJSON
 * @DELETE http://localhost:8080/spesa/lista?id={rifRichiesta}
 */
/**
 * GALIMBERTI FRANCESCO
 *
 * @GET
 * http://localhost:8080/spesa/utenti/utente/{username}
 * http://localhost:8080/spesa/utenti/utente/{idUtente}
 * http://localhost:8080/spesa/utenti
 * http://localhost:8080/spesa/utenti?nome={nome}&...
 * @POST http://localhost:8080/spesa/risposta
 * @PUT http://localhost:8080/spesa/utenti/{idUtente}
 * @DELETE http://localhost:8080/spesa/richieste/{idRichiesta}/{idUtente}
 */
/**
 * ROVELLI ANDREA
 *
 * @GET
 * http://localhost:8080/spesa/lista?rifRichiesta={id}
 * @POST http://localhost:8080/spesa/lista
 *
 * @PUT http://localhost:8080/spesa/updLista
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
public class Api extends Application {

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

    /**
     * Galimberti Francesco
     *
     * http://localhost:8080/spesa/utenti
     *
     * Visualizza i dati relativi agli utenti memorizzati nel database
     * permettendo di filtrare i risultati ottenuti attraverso vari parametri di
     * query.
     *
     * @param nome Parametro query che permette di specificare il nome dei
     * utenti che si vogliono visualizzare
     * @param cognome Parametro query che permette di specificare il cognome dei
     * utenti che si vogliono visualizzare
     * @param regione Parametro query che permette di specificare il regione dei
     * utenti che si vogliono visualizzare
     * @return Risposta, con informazioni richieste o messaggio di errore
     */
    @GET
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response getUtenti(
            @QueryParam("nome") String nome,
            @QueryParam("cognome") String cognome,
            @QueryParam("regione") String regione) {

        init();
        String output = "";
        Response r;

        // verifica stato connessione a DBMS
        if (!connected) {

            r = Response.serverError().entity("<messaggio>DBMS Error, impossibile connettersi</messaggio>").build();
            return r;

        } else {

            try {

                String sql = "";
                /*if (username != null) {
                    sql = "SELECT idUtente, username, nome, cognome, codiceFiscale, regione, via, nCivico FROM utenti WHERE username='" + username + "';";

                } else {*/
                sql = "SELECT idUtente, username, nome, cognome, codiceFiscale, regione, via, nCivico FROM utenti WHERE";

                if (nome != null && !nome.isEmpty()) {
                    sql += " nome='" + nome + "' AND";
                }

                if (cognome != null && !cognome.isEmpty()) {
                    sql += " cognome='" + cognome + "' AND";
                }

                if (regione != null && !regione.isEmpty()) {
                    sql += " regione='" + regione + "' AND";
                }

                sql = sql + " 1";
                //}

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

                if (utentiList.size() > 0) {
                    output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                    output += "<utenti>";

                    for (int i = 0; i < utentiList.size(); i++) {
                        Utente u = utentiList.get(i);
                        output += "<utente>";
                        output += "<idUtente>" + u.getIdUtente() + "</idUtente>";
                        output += "<username>" + u.getUsername() + "</username>";

                        if (nome == null) {
                            output += "<nome>" + u.getNome() + "</nome>";
                        }
                        if (cognome == null) {
                            output += "<cognome>" + u.getCognome() + "</cognome>";
                        }
                        if (regione == null) {
                            output += "<regione>" + u.getRegione() + "</regione>";
                        }

                        output += "<codiceFiscale>" + u.getCodiceFiscale() + "</codiceFiscale>";
                        output += "<via>" + u.getVia() + "</via>";
                        output += "<nCivico>" + u.getnCivico() + "</nCivico>";
                        output += "</utente>";
                    }
                    output += "</utenti>";
                    utentiList = new ArrayList<Utente>(0);

                    destroy();
                    r = Response.ok(output).build();
                    return r;

                } else {
                    destroy();
                    r = Response.status(404).entity("<messaggio>Utenti non trovati</messaggio>").build();
                    return r;
                }

            } catch (SQLException ex) {
                destroy();
                r = Response.serverError().entity("<messaggio>DBMS SQL Error</messaggio>").build();
                return r;
            }
        }
    }

    /**
     * Galimberti Francesco
     *
     * http://localhost:8080/spesa/utenti/utente?id={idUtente}
     * http://localhost:8080/spesa/utenti/utente?username={username}
     *
     * Visualizza i dati relativi agli utenti memorizzati nel database
     * permettendo di filtrare i risultati ottenuti attraverso vari parametri di
     * query.
     *
     * @param id Parametro query che permette di specificare l'id dell'utente
     * che si vuole visualizzare
     * @param username Parametro query che permette di specificare l'username
     * dell'utente che si vuole visualizzare
     * @return Risposta, con informazioni richieste o messaggio di errore
     */
    /*@GET
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("utente")
    public Response getUtente(
            @QueryParam("id") int id,
            @QueryParam("username") String username) {

        String output = "";
        Response r;

        // verifica stato connessione a DBMS
        if (!connected) {

            r = Response.serverError().entity("<messaggio>DBMS Error, impossibile connettersi</messaggio>").build();
            return r;

        } else {
            String sql = "SELECT idUtente, username, nome, cognome, codiceFiscale, regione, via, nCivico FROM utenti WHERE ";

            if (username != null && !username.isEmpty()) {
                sql += "username='" + username + "';";
            } else if (id > 0) {
                sql += "idUtente=" + id + ";";
            } else {
                destroy();
                r = Response.status(402).entity("<messaggio>Parametro non valido o mancante</messaggio>").build();
                return r;
            }

            try {
                Statement statement = spesaDatabase.createStatement();
                ResultSet result = statement.executeQuery(sql);

                result.next();

                String idUtente = result.getString("idUtente");
                String Username = result.getString("username");
                String Nome = result.getString("nome");
                String Cognome = result.getString("cognome");
                String CodiceFiscale = result.getString("codiceFiscale");
                String Regione = result.getString("regione");
                String Via = result.getString("via");
                String nCivico = result.getString("nCivico");

                Utente u = new Utente(idUtente, Username, Nome, Cognome, CodiceFiscale, Regione, Via, nCivico);

                result.close();
                statement.close();

                output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                output += "<utente>";

                if (username != null) {
                    output += "<idUtente>" + u.getIdUtente() + "</idUtente>";
                } else if (id != 0) {
                    output += "<username>" + u.getUsername() + "</username>";
                }

                output += "<nome>" + u.getNome() + "</nome>";
                output += "<cognome>" + u.getCognome() + "</cognome>";
                output += "<regione>" + u.getRegione() + "</regione>";
                output += "<codiceFiscale>" + u.getCodiceFiscale() + "</codiceFiscale>";
                output += "<via>" + u.getVia() + "</via>";
                output += "<nCivico>" + u.getnCivico() + "</nCivico>";
                output += "</utente>";

                destroy();
                r = Response.ok(output).build();
                return r;

            } catch (SQLException ex) {
                Logger.getLogger(Utenti.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.status(404).entity("<messaggio>Utente non trovato</messaggio>").build();
                return r;
            }

        }
    }*/
    /**
     * Verifica se una stringa è un numero int
     *
     * @return il numero se è un int, -1 se è una stringa
     */
    public static int isInt(String num) {
        try {
            int i = Integer.parseInt(num);
            return i;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Galimberti Francesco
     *
     * http://localhost:8080/spesa/utenti/utente/{username}
     * http://localhost:8080/spesa/utenti/utente/{idUtente}
     *
     * Visualizza i dati relativi all'utente memorizzato nel database passando
     * come parametro l'username o l'id dell'utente
     *
     * @param path contiene o l'username o l'id dell'utente ricercato
     * @return Risposta, con informazioni richieste o messaggio di errore
     */
    @GET
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("utente/{path}")
    public Response getUtente(
            @PathParam("path") String path) {

        String output = "";
        int id;
        Response r;

        // verifica stato connessione a DBMS
        if (!connected) {

            r = Response.serverError().entity("<messaggio>DBMS Error, impossibile connettersi</messaggio>").build();
            return r;

        } else {
            String sql = "SELECT idUtente, username, nome, cognome, codiceFiscale, regione, via, nCivico FROM utenti WHERE ";

            if (path != null && !path.isEmpty()) {

                id = isInt(path);
                if (id > 0) {
                    sql += "idUtente=" + id + ";";
                } else if (id == 0) {
                    destroy();
                    r = Response.status(402).entity("<messaggio>Parametro non valido o mancante</messaggio>").build();
                    return r;
                } else {
                    sql += "username='" + path + "';";
                }

            } else {
                destroy();
                r = Response.status(402).entity("<messaggio>Parametro non valido o mancante</messaggio>").build();
                return r;
            }

            try {
                Statement statement = spesaDatabase.createStatement();
                ResultSet result = statement.executeQuery(sql);

                result.next();

                String idUtente = result.getString("idUtente");
                String Username = result.getString("username");
                String Nome = result.getString("nome");
                String Cognome = result.getString("cognome");
                String CodiceFiscale = result.getString("codiceFiscale");
                String Regione = result.getString("regione");
                String Via = result.getString("via");
                String nCivico = result.getString("nCivico");

                Utente u = new Utente(idUtente, Username, Nome, Cognome, CodiceFiscale, Regione, Via, nCivico);

                result.close();
                statement.close();

                output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                output += "<utente>";

                if (id > 0) {
                    output += "<username>" + u.getUsername() + "</username>";
                } else {
                    output += "<idUtente>" + u.getIdUtente() + "</idUtente>";
                }

                output += "<nome>" + u.getNome() + "</nome>";
                output += "<cognome>" + u.getCognome() + "</cognome>";
                output += "<regione>" + u.getRegione() + "</regione>";
                output += "<codiceFiscale>" + u.getCodiceFiscale() + "</codiceFiscale>";
                output += "<via>" + u.getVia() + "</via>";
                output += "<nCivico>" + u.getnCivico() + "</nCivico>";
                output += "</utente>";

                destroy();
                r = Response.ok(output).build();
                return r;

            } catch (SQLException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.status(404).entity("<messaggio>Utente non trovato</messaggio>").build();
                return r;
            }

        }
    }

    /**
     * Galimberti Francesco
     *
     * PUT spesa/utenti/1
     *
     * body examples
     * <utente>
     * <username>fraGali</username>
     * <nome>Francesco</nome>
     * <cognome>Galimberti</cognome>
     * <codiceFiscale>GLMFNC01A02B729Q</codiceFiscale>
     * <regione>Lombardia</regione>
     * <via>Giacomo Leopardi</via>
     * <nCivico>5</nCivico>
     * </utente>
     *
     * Consente la modifica di un utente andando a specificarne l'ID tramite il
     * percorso
     *
     * @param idUtente identificativo dell'utente da modificare
     * @param content Body della richiesta PUT http/https contenente i nuovi
     * valori degli attributi dell'utente specificato nel percorso (sottoforma di
     * XML)
     * @return Risposta, con messaggio e stato
     */
    @PUT
    @Path("utenti/{idUtente}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.TEXT_XML})
    public Response putUtente(
            @PathParam("idUtente") int idUtente,
            String content) {
        // verifica stato connessione a DBMS
        init();
        MyParser myParse;
        Response r;

        if (!connected) {
             r = Response.serverError().entity("<messaggio>DBMS Error, impossibile connettersi</messaggio>").build();
            return r;
        } else {
            try {

                BufferedWriter file;
                file = new BufferedWriter(new FileWriter("utente.xml"));
                file.write(content);
                file.flush();
                file.close();

                myParse = new MyParser();
                Utente u = myParse.parseFileUtente("utente.xml");

                if (idUtente != 0) {
                    /*if (u.getNome() == null || u.getCognome() == null || u.getCognome() == null || u.getCodiceFiscale() == null || u.getRegione() == null || u.getnCivico() == null || u.getVia() == null || u.getUsername() == null) {
                        r = Response.status(409).entity("Error, Malformed XML Body").build();
                        return r;
                    }
                    if (u.getNome().isEmpty() || u.getCognome().isEmpty() || u.getCognome().isEmpty() || u.getCodiceFiscale().isEmpty() || u.getRegione().isEmpty() || u.getnCivico().isEmpty() || u.getVia().isEmpty() || u.getUsername().isEmpty()) {
                        r = Response.status(409).entity("Error, Malformed XML Body").build();
                        return r;
                    }*/

                    Statement statement = spesaDatabase.createStatement();

                    StringBuilder columns = new StringBuilder(255);
                    if (u.getUsername() != null && !u.getUsername().isEmpty()) {
                        //sql += " username='" + u.getUsername() + "', ";
                        columns.append("username='").append(u.getUsername()).append("'");
                    }
                    if (u.getNome() != null && !u.getNome().isEmpty()) {
                        //sql += " nome='" + u.getNome() + "', ";
                        if (columns.length() > 0) {
                            columns.append(", ");
                        }
                        columns.append("nome='").append(u.getNome()).append("'");
                    }
                    if (u.getCognome() != null && !u.getCognome().isEmpty()) {
                        //sql += " cognome='" + u.getCognome() + "', ";
                        if (columns.length() > 0) {
                            columns.append(", ");
                        }
                        columns.append("cognome='").append(u.getCognome()).append("'");
                    }
                    if (u.getCodiceFiscale() != null && !u.getCodiceFiscale().isEmpty()) {
                        //sql += " codiceFiscale='" + u.getCodiceFiscale() + "', ";
                        if (columns.length() > 0) {
                            columns.append(", ");
                        }
                        columns.append("codiceFiscale='").append(u.getCodiceFiscale()).append("'");
                    }
                    if (u.getRegione() != null && !u.getRegione().isEmpty()) {
                        //sql += " regione='" + u.getRegione() + "', ";
                        if (columns.length() > 0) {
                            columns.append(", ");
                        }
                        columns.append("regione='").append(u.getRegione()).append("'");
                    }
                    if (u.getVia() != null && !u.getVia().isEmpty()) {
                        //sql += " via='" + u.getVia() + "', ";
                        if (columns.length() > 0) {
                            columns.append(", ");
                        }
                        columns.append("via='").append(u.getVia()).append("'");
                    }
                    if (u.getnCivico() != null && !u.getnCivico().isEmpty()) {
                        //sql += " nCivico='" + u.getnCivico() + "', ";
                        if (columns.length() > 0) {
                            columns.append(", ");
                        }
                        columns.append("nCivico='").append(u.getnCivico()).append("'");
                    }

                    if (columns.length() > 0) {
                        String sql = "UPDATE utenti SET " + columns.toString()
                                + " WHERE idUtente = " + idUtente + ";";
                        if (statement.executeUpdate(sql) <= 0) {
                            statement.close();
                            r = Response.serverError().entity("<messaggio>DBMS SQL Error, impossibile modificare utenti</messaggio>").build();
                            return r;
                        }
                        statement.close();
                        destroy();
                        r = Response.ok("<messaggio>Update avvenuto correttamente</messaggio>").build();
                        return r;
                    
                    }else{
                        r = Response.status(404).entity("<messaggio>parametri non validi</messaggio>").build();
                        return r;
                    }

                } else {
                    r = Response.status(403).entity("<messaggio>idUtente non valido</messaggio>").build();
                    return r;
                }

            } catch (IOException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.serverError().entity("<messaggio>DBMS IO Error</messaggio>").build();

            } catch (SQLException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.serverError().entity("<messaggio>DBMS SQL Error</messaggio>").build();

            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.status(409).entity("<messaggio>Error, Malformed XML Body</messaggio>").build();

            } catch (SAXException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.serverError().entity("<messaggio>DBMS SAXE Error</messaggio>").build();
            }
            return r;
        }
    }

    /**
     * Galimberti Francesco 
     * DELETE spesa/richieste/1/1
     *
     * Consente di eliminare una richiesta andando a specificarne l'ID tramite
     * il percorso sia della richiesta che dell'utente
     *
     * @param idRichiesta ID della richiesta da eliminare
     * @param idUtente ID dell'utente collegato alla richiesta da eliminare
     * @return Risposta, con messaggio 
     */
    @DELETE
    @Path("richieste/{idRichiesta}/{idUtente}")
    @Consumes(MediaType.TEXT_XML)
    public Response deleteRichiesta(@PathParam("idRichiesta") int idRichiesta, @PathParam("idUtente") int idUtente) {

        init();
        Response r;

        if (!connected) {
             r = Response.serverError().entity("<messaggio>DBMS Error, impossibile connettersi</messaggio>").build();
            return r;
        } else {

            if (idRichiesta != 0 && idUtente != 0) {
                try {
                    Statement statement = spesaDatabase.createStatement();
                    String sql = "DELETE FROM richieste WHERE idRichiesta = '" + idRichiesta + "' AND rifUtente = '" + idUtente + "';";

                    if (statement.executeUpdate(sql) <= 0) {
                        statement.close();
                        r = Response.serverError().entity("<messaggio>DBMS SQL Error, impossibile eliminare richiesta</messaggio>").build();
                        return r;
                    }

                    statement.close();
                    destroy();
                    r = Response.ok("<messaggio>Eliminazione avvenuta correttamente</messaggio>").build();
                    return r;

                } catch (SQLException ex) {
                    destroy();
                    r = Response.status(404).entity("<messaggio>Error, idRichiesta o idUtente corretti</messaggio>").build();
                    return r;
                }
            } else {
                r = Response.status(402).entity("<messaggio>Error, idRichiesta o idUtente non validi</messaggio>").build();
                return r;
            }
        }
    }

    /**
     * Galimberti Francesco
     *
     * POST spesa/risposte
     *
     * <risposta>
     * <idUtente>1</idUtente>
     * <idRichiesta>2</idRichiesta>
     * </risposta>
     *
     * Consente l'inserimento di una nuova risposta ad una richiesta di spesa
     * all'interno del database
     *
     * @param content Body della richiesta POST https/https contenente il nuovo
     * prodotto da dover memorizzare sottoforma di XML
     * @return Risposta, con messaggio e stato
     */
    @POST
    @Consumes(MediaType.TEXT_XML)
    @Path("risposta")
    public Response postRisposta(String content) {

        // verifica stato connessione a DBMS
        init();
        MyParser myParse;
        Response r;

        if (!connected) {
             r = Response.serverError().entity("<messaggio>DBMS Error, impossibile connettersi</messaggio>").build();
            return r;
        } else {

            try {
                BufferedWriter file;
                file = new BufferedWriter(new FileWriter("risposta.xml"));
                file.write(content);
                file.flush();
                file.close();

                myParse = new MyParser();
                Risposta rr = myParse.parseFileRisposta("risposta.xml");

                if (rr.getIdUtente() == null || rr.getIdRichiesta() == null) {
                    r = Response.status(402).entity("<messaggio>Parametri non validi<messaggio>").build();
                    return r;
                }
                if (rr.getIdUtente().isEmpty() || rr.getIdRichiesta().isEmpty()) {
                    r = Response.status(402).entity("<messaggio>Parametri non validi<messaggio>").build();
                    return r;
                }

                // aggiunta voce nel database
                Statement statement = spesaDatabase.createStatement();
                String sql = "INSERT risposte(rifUtente, rifRichiesta) VALUES(" + rr.getIdUtente() + ", " + rr.getIdRichiesta() + ");";

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    r = Response.status(404).entity("<messaggio>DBMS SQL Error, impossibile effettuare inserimento</messaggio>").build();
                    return r;
                }

                statement.close();
                destroy();
                r = Response.ok("<messaggio>Inserimento avvenuto correttamente</messaggio>").build();
                return r;

            } catch (IOException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.serverError().entity("<messaggio>DBMS IO Error</messaggio>").build();

            } catch (SQLException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.serverError().entity("<messaggio>DBMS SQL Error</messaggio>").build();

            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.status(409).entity("<messaggio>Error, Malformed XML Body</messaggio>").build();

            } catch (SAXException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.serverError().entity("<messaggio>DBMS SAXE Error</messaggio>").build();
            }
            return r;

        }
    }

    /**
     * @author Tosetti_Luca
     *
     * Visualizza i dati relativi alle richieste memorizzate nel database oppure
     * di uno specifico utente andando a specificare l'id di tale utente come
     * parametro query
     * <br><br>
     * Esempi:  http://localhost:8080/spesa/risposte?rifUtente=1&rifRichiesta=4&lista=true
     *          http://localhost:8080/spesa/risposte?rifUtente=1&rifRichiesta=2
     *          http://localhost:8080/spesa/risposte?rifUtente=3
     *          http://localhost:8080/spesa/risposte?rifUtente=1
     * 
     * @param rifUtente ID dell'utente del quale si vogliono ottenere le varie risposte
     * @param rifRichiesta ID di una richiesta specifica soddisfatta dall'utente
     * @param lista Booleano che permette di visualizzare o meno la o le liste relative alla o alle richieste soddisfatte (Di default è false, quindi la lista non verrà visualizzata)
     * @return Output XML contenente le informazioni relative a una o più risposte / Output messaggio di errore
     */
    @GET
    @Produces(MediaType.TEXT_XML)
    @Path("risposte")
    public String getRisposte(@QueryParam("rifUtente") String rifUtente, @QueryParam("rifRichiesta") String rifRichiesta, @QueryParam("lista") boolean lista) {
        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>500</errorMessage>";
        }

        try {
            String sql;
            ArrayList<Object> risp = new ArrayList<Object>();
            if (rifUtente != null && !rifUtente.isEmpty()) {
                sql = "SELECT idRichiesta,richieste.rifUtente,oraInizioConsegna,oraFineConsegna,durataRichiesta FROM Richieste,risposte WHERE idRichiesta=risposte.rifRichiesta AND risposte.rifUtente='" + rifUtente + "'";
                if(rifRichiesta != null && !rifRichiesta.isEmpty()) {
                    sql = "SELECT idRichiesta,richieste.rifUtente,oraInizioConsegna,oraFineConsegna,durataRichiesta FROM Richieste,risposte WHERE idRichiesta=risposte.rifRichiesta AND risposte.rifUtente='" + rifUtente + "' AND risposte.rifRichiesta='"+rifRichiesta+"'";
                }
                
                Statement statement = spesaDatabase.createStatement();
                ResultSet result = statement.executeQuery(sql);

                String rispOraInizioConsegna, rispOraFineConsegna, rispDurataRichiesta;
                int rispIdRichiesta = 0, rispRifUtente = 0;
                ArrayList<Object> richieste = new ArrayList<Object>();
                ArrayList<Object> liste = new ArrayList<Object>();
                while (result.next()) {
                    rispIdRichiesta = result.getInt(1);
                    rispRifUtente = result.getInt(2);
                    rispOraInizioConsegna = result.getString(3);
                    rispOraFineConsegna = result.getString(4);
                    rispDurataRichiesta = result.getString(5);

                    Richiesta richiesta = new Richiesta(rispIdRichiesta, rispRifUtente, rispOraInizioConsegna, rispOraFineConsegna, rispDurataRichiesta);
                    richieste.add(richiesta);

                    sql = "SELECT idProdotto,genere,etichetta,costo,nome,marca,descrizione,quantita FROM prodotti,liste,richieste WHERE liste.rifRichiesta=richieste.idRichiesta AND rifProdotto=idProdotto AND liste.rifRichiesta='" + rispIdRichiesta + "'";

                    //result.close();
                    //statement.close();

                    Statement statement2 = spesaDatabase.createStatement();
                    ResultSet result2 = statement2.executeQuery(sql);

                    int idProdotto;
                    String rispGenere, rispEtichetta, rispNomeProdotto, rispMarca, rispDescrizione;
                    double rispCosto;

                    ArrayList<Object> listaProdotti = new ArrayList<Object>();
                    while (result2.next()) {
                        idProdotto = result2.getInt(1);
                        rispGenere = result2.getString(2);
                        rispEtichetta = result2.getString(3);
                        rispCosto = result2.getDouble(4);
                        rispNomeProdotto = result2.getString(5);
                        rispMarca = result2.getString(6);
                        rispDescrizione = result2.getString(7);

                        Prodotto prodotto = new Prodotto(idProdotto, rispGenere, rispEtichetta, rispCosto, rispNomeProdotto, rispMarca, rispDescrizione);
                        listaProdotti.add(prodotto);
                    }
                    liste.add(listaProdotti);
                    
                    result2.close();
                    statement2.close();
                }
                risp.add(richieste);
                risp.add(liste);

                sql = "SELECT idUtente,username,nome,cognome,codiceFiscale,regione,via,nCivico FROM utenti,richieste WHERE rifUtente='" + rispRifUtente + "' AND richieste.rifUtente=utenti.idUtente GROUP BY idUtente";

                statement = spesaDatabase.createStatement();
                result = statement.executeQuery(sql);

                String rispIdUtente, rispUsername, rispNome, rispCognome, rispCodiceFiscale, rispRegione, rispVia, rispNCivico;
                while (result.next()) {
                    rispIdUtente = result.getString(1);
                    rispUsername = result.getString(2);
                    rispNome = result.getString(3);
                    rispCognome = result.getString(4);
                    rispCodiceFiscale = result.getString(5);
                    rispRegione = result.getString(6);
                    rispVia = result.getString(7);
                    rispNCivico = result.getString(8);

                    Utente utente = new Utente(rispIdUtente, rispUsername, rispNome, rispCognome, rispCodiceFiscale, rispRegione, rispVia, rispNCivico);
                    risp.add(utente);

                }

                result.close();
                statement.close();
            }
            
            
            if (risp.size() > 0) {
                output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                output = output + "<elencoRisposte>\n";

                Utente utente=new Utente();
                ArrayList<Richiesta> richieste=new ArrayList<Richiesta>();
                ArrayList<Prodotto> listaProdotti=new ArrayList<Prodotto>();
                utente=(Utente) risp.get(2);
                richieste=(ArrayList<Richiesta>) risp.get(0);
                for (int i = 0; i < richieste.size(); i++) {
                    listaProdotti=(ArrayList<Prodotto>)(((ArrayList<Object>) risp.get(1)).get(i));
                    output = output + "<risposta>\n";
                    output = output + "<idRichiesta>" + richieste.get(i).getIdRichiesta() + "</idRichiesta>\n";
                    output = output + "<rifUtente>\n";
                    output = output + "<username>" + utente.getUsername() + "</username>\n";
                    output = output + "<nome>" + utente.getNome() + "</nome>\n";
                    output = output + "<cognome>" + utente.getCognome() + "</cognome>\n";
                    output = output + "<codiceFiscale>" + utente.getCodiceFiscale() + "</codiceFiscale>\n";
                    output = output + "<regione>" + utente.getRegione() + "</regione>\n";
                    output = output + "<via>" + utente.getVia() + "</via>\n";
                    output = output + "<nCivico>" + utente.getnCivico() + "</nCivico>\n";
                    output = output + "</rifUtente>\n";
                    output = output + "<oraInizio>" + richieste.get(i).getOraInizio() + "</oraInizio>\n";
                    output = output + "<oraFine>" + richieste.get(i).getOraFine() + "</oraFine>\n";
                    output = output + "<durata>" + richieste.get(i).getDurata() + "</durata>\n";
                    
                    if(lista) {
                    output = output + "<lista>\n";
                    for(int y=0;y<listaProdotti.size();y++) {
                        output = output + "<prodotto>\n";
                        output = output + "<idProdotto>" + listaProdotti.get(y).getIdProdotto() + "</idProdotto>\n";
                        output = output + "<genere>" + listaProdotti.get(y).getGenere() + "</genere>\n";
                        output = output + "<etichetta>" + listaProdotti.get(y).getEtichetta() + "</etichetta>\n";
                        output = output + "<costo>" + listaProdotti.get(y).getCosto() + "</costo>\n";
                        output = output + "<nome>" + listaProdotti.get(y).getNome()+ "</nome>\n";
                        output = output + "<marca>" + listaProdotti.get(y).getMarca()+ "</marca>\n";
                        output = output + "<descrizione>" + listaProdotti.get(y).getDescrizione()+ "</descrizione>\n";
                        output = output + "</prodotto>\n";
                    }
                    output = output + "</lista>\n";
                    }
                    output = output + "</risposta>\n";
                }

                output = output + "</elencoRisposte>\n";
            
            } else {
                destroy();
                return output;
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
     * Visualizza i dati relativi ai prodotti memorizzati nel database
     * permettendo di filtrare i risultati ottenuti attraverso vari parametri di
     * query.
     *
     * @param genere Parametro query che permette di specificare il genere dei
     * prodotti che si vogliono visualizzare
     * @param etichetta Parametro query che permette di specificare l'etichetta
     * dei prodotti che si vogliono visualizzare
     * @param costo Parametro query che permette di specificare il costo dei
     * prodotti che si vogliono visualizzare
     * @param nome Parametro query che permette di specificare il nome dei
     * prodotti che si vogliono visualizzare
     * @param marca Parametro query che permette di specificare la marca dei
     * prodotti che si vogliono visualizzare
     * @param descrizione Parametro query che permette di specificare la
     * descrizione dei prodotti che si vogliono visualizzare
     * @return Output XML contenente le informazioni relative a uno o più
     * prodotti / Output messaggio di errore
     */
    @GET
    @Produces(MediaType.TEXT_XML)
    @Path("prodotto")
    public String getProdotto(@QueryParam("genere") String genere, @QueryParam("etichetta") String etichetta, @QueryParam("costo") double costo, @QueryParam("nome") String nome, @QueryParam("marca") String marca, @QueryParam("descrizione") String descrizione) {
        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>500</errorMessage>";
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
            if (descrizione != null) {
                sql = sql + " descrizione='" + descrizione + "' AND";
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
                return output;
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
     * @param content Body della richiesta POST https/https contenente il/i
     * nuovo/i prodotto/i da dover memorizzare sottoforma di XML
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
                return "<errorMessage>500</errorMessage>";
            }

            try {
                String sql = "INSERT INTO prodotti (genere,etichetta,costo,nome,marca,descrizione) VALUES ('" + prodotti.get(0).getGenere() + "','" + prodotti.get(0).getEtichetta() + "','" + prodotti.get(0).getCosto() + "','" + prodotti.get(0).getNome() + "','" + prodotti.get(0).getMarca() + "','" + prodotti.get(0).getDescrizione() + "')";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>500</errorMessage>";
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
     * Consente la modifica di un di un determinato prodotto andando a
     * specificarne l'ID tramite il percorso
     * @param content Body della richiesta PUT http/https contenente i nuovi
     * valori degli attributi del prodotto specificato nel percorso sottoforma
     * di XML
     * @return Output messaggio di successo / Output messaggio di errore
     */
    @PUT
    @Consumes(MediaType.TEXT_XML)
    @Path("prodotto/{idProdotto}")
    public String putProdotto(@PathParam("idProdotto") String idProdotto, String content) {
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
                return "<errorMessage>500</errorMessage>";
            }

            if (prodotto.get(0).getGenere() == null || prodotto.get(0).getEtichetta() == null || prodotto.get(0).getNome() == null || prodotto.get(0).getMarca() == null || prodotto.get(0).getCosto() == 0.00 || prodotto.get(0).getDescrizione() == null) {
                return "<errorMessage>406</errorMessage>";
            }
            if (prodotto.get(0).getGenere().isEmpty() || prodotto.get(0).getEtichetta().isEmpty() || prodotto.get(0).getNome().isEmpty() || prodotto.get(0).getMarca().isEmpty() || prodotto.get(0).getDescrizione().isEmpty()) {
                return "<errorMessage>406</errorMessage>";
            }

            try {
                String sql = "UPDATE prodotti SET nome='" + prodotto.get(0).getNome() + "', genere='" + prodotto.get(0).getGenere() + "', etichetta='" + prodotto.get(0).getEtichetta() + "', costo='" + prodotto.get(0).getCosto() + "', nome='" + prodotto.get(0).getNome() + "', marca='" + prodotto.get(0).getMarca() + "', descrizione='" + prodotto.get(0).getDescrizione() + "' WHERE idProdotto='" + idProdotto + "'";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>500</errorMessage>";
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
     * Consente di eliminare un prodotto andando a specificarne l'ID tramite il
     * percorso
     * @param id ID del prodotto da eliminare
     * @return Output messaggio di successo / Output messaggio di errore
     */
    @DELETE
    @Path("prodotto/{idProdotto}")
    public String deleteProdotto(@PathParam("idProdotto") int id) {
        init();

        if (!connected) {
            return "<errorMessage>500</errorMessage>";
        }

        if (id != 0) {
            try {
                String sql = "DELETE FROM prodotti WHERE idProdotto='" + id + "'";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>500</errorMessage>";
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
     * SPANGARO FRANCESCO visualizza i dati di una richiesta da id fornito nella
     * path in formato xml come definito nella progettazione api
     *
     * @param id è l'id su cui si baserà la ricerca
     * @return varie tipologie di ritorno, conferma se corretto, altrimenti
     * messaggi di errore corrispondenti
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
     * SPANGARO FRANCESCO visualizza i dati di una richiesta da id fornito nella
     * path in formato JSON come definito nella progettazione api
     *
     * @param id è l'id su cui si baserà la ricerca
     * @return varie tipologie di ritorno, conferma se corretto, altrimenti
     * messaggi di errore corrispondenti
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
     * SPANGARO FRANCESCO inserisce i dati di un utente fornito nel body in
     * formato XML come definito nella progettazione api
     *
     * @param content sono i dati inviati dall'utilizzatore, salvato nella
     * cartella server xampp/tomcat/bin/utente.xml
     * @return varie tipologie di ritorno, conferma se corretto, altrimenti
     * messaggi di errore corrispondenti
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
     * SPANGARO FRANCESCO inserisce i dati di un utente fornito nel body in
     * formato JSON come definito nella progettazione api
     *
     * @param content sono i dati inviati dall'utilizzatore, parsati dal metodo
     * (libreria usata: json-20190722.jar)
     * @return varie tipologie di ritorno, conferma se corretto, altrimenti
     * messaggi di errore corrispondenti
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
     * SPANGARO FRANCESCO inserisce i dati di una richiesta fornita nel body in
     * formato XML come definito nella progettazione api
     *
     * @param content sono i dati inviati dall'utilizzatore, salvato nella
     * cartella server xampp/tomcat/bin/richiesta.xml
     * @return varie tipologie di ritorno, conferma se corretto, altrimenti
     * messaggi di errore corrispondenti
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
     * SPANGARO FRANCESCO inserisce i dati di una richiesta fornita nel body in
     * formato JSON come definito nella progettazione api
     *
     * @param content sono i dati inviati dall'utilizzatore, parsati dal metodo
     * (libreria usata: json-20190722.jar)
     * @return varie tipologie di ritorno, conferma se corretto, altrimenti
     * messaggi di errore corrispondenti
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
     * SPANGARO FRANCESCO cancella una lista dal database, una lista è l'insieme
     * dei prodotti, assegnata poi ad una richiesta, lista che corrisponde
     * all'id inserito come parametro della query, riferimento alla richiesta
     * corrispondente
     *
     * @param id è l'id su cui si deve basare per fare la ricerca
     * @return varie tipologie di ritorno, conferma se corretto, altrimenti
     * messaggi di errore corrispondenti
     */
    @DELETE
    @Path("lista")
    public String deleteLista(@QueryParam("id") int rifRichiesta) {
        init();

        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        }
        try {
            String sql = "DELETE FROM liste WHERE rifRichiesta='" + rifRichiesta + "'";
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
     * SPANGARO FRANCESCO Metodo per il casting delle stringhe restituite dal
     * database, da String a Time
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

    /**
     * @author Rovelli Andrea
     *
     * GET spesa/lista
     *
     * Consente di ottenere la lista della spesa di un determinato utente
     *
     * @param id Parametro che identifica la lista di un determinato utente
     *
     * @return Risposta, con messaggio e stato
     */
    @GET
    @Produces(MediaType.TEXT_XML)
    @Path("lista")
    public String getLista(@QueryParam("rifRichiesta") String id) {

        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        }

        try {
            String sql = "SELECT Costo,Nome,Marca FROM prodotti p, liste l WHERE p.idProdotto = l.rifProdotto AND ";
            if (!id.isEmpty()) {
                sql = sql + " l.rifRichiesta='" + id + "'";
            }

            Statement statement = spesaDatabase.createStatement();
            ResultSet result = statement.executeQuery(sql);

            ArrayList<Prodotto> spesa = new ArrayList(0);

            while (result.next()) {

                double costo = result.getDouble("costo");
                String marca = result.getString("marca");
                String nome = result.getString("nome");

                Prodotto prodotto = new Prodotto(costo, marca, nome);

                spesa.add(prodotto);
            }
            result.close();
            statement.close();

            output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
            output += "<listaSpesa>\n";

            if (!spesa.isEmpty()) {

                for (int i = 0; i < spesa.size(); i++) {
                    Prodotto p = spesa.get(i);

                    output += "<prodotto>\n";
                    output += "<costo>";
                    output += p.getCosto();
                    output += "</costo>\n";
                    output += "<nome>";
                    output += p.getNome();
                    output += "</nome>\n";
                    output += "<marca>";
                    output += p.getMarca();
                    output += "</marca>\n";
                    output += "</prodotto>\n";
                }

                output += "</listaSpesa>\n";
            } else {

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
     * @author Rovelli Andrea
     *
     * POST spesa/lista
     *
     * Consente di inserire nella lista della spesa un nuovo prodotto
     *
     * @param content Body contenente tutti i valori necessari all'esecuzione
     * della POST come il riferimento alla richiesta, al prodotto e la quantità
     * da comprare
     *
     * @return Risposta, con messaggio e stato
     */
    @POST
    @Consumes(MediaType.TEXT_XML)
    @Path("lista")
    public String postLista(String content) {
        try {
            init();

            MyParser myParse = new MyParser();
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("lista.xml"));
            writer.write(content);
            writer.flush();
            writer.close();

            ArrayList<Lista> liste = (ArrayList<Lista>) myParse.parseDocument("lista.xml", "post");
            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }

            try {
                String sql = "INSERT INTO liste (rifRichiesta, rifProdotto, quantita) VALUES ('" + liste.get(0).getRifRichiesta() + "','" + liste.get(0).getRifProdotto() + "','" + liste.get(0).getQuantita() + "')";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Inserimento effettuato</message>";
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
     * @author Rovelli Andrea
     *
     * PUT spesa/lista
     *
     * Consente di aggiornare un prodotto all'interno di una lista della spesa
     *
     * @param content Body contenente tutti i valori necessari all'esecuzione
     * della PUT come l'identificativo di un prodotto all'interno di una lista,
     * il riferimento alla richiesta, al prodotto e la quantità da comprare
     *
     * @return Risposta, con messaggio e stato
     *
     * esempio:
     *
     * <liste>
     * <lista>
     * <idLista>7</idLista>
     * <rifRichiesta>2</rifRichiesta>
     * <rifProdotto>1</rifProdotto>
     * <quantita>5</quantita>
     * </lista>
     * </liste>
     */
    @PUT
    @Consumes(MediaType.TEXT_XML)
    @Path("lista")
    public String putLista(String content) {
        try {
            init();

            MyParser myParse = new MyParser();
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("updLista.xml"));
            writer.write(content);
            writer.flush();
            writer.close();

            ArrayList<Lista> lista = (ArrayList<Lista>) myParse.parseDocument("updLista.xml", "put");
            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }

            try {
                String sql = "UPDATE liste SET rifRichiesta='" + lista.get(0).getRifRichiesta() + "', rifProdotto='" + lista.get(0).getRifProdotto() + "', quantita='" + lista.get(0).getQuantita() + "' WHERE idLista='" + lista.get(0).getIdLista() + "'";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Update effettuato</message>";
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

}
