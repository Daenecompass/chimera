package de.uni_potsdam.hpi.bpt.bp2014.jcomparser;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import de.uni_potsdam.hpi.bpt.bp2014.settings.PropertyLoader;



/**
 * As a part of the JComparser, this class is reponsible for the retrieval
 * of XML docus from a source URL like the repository of the Processeditor.
 */
public class Retrieval {
    static Logger log = Logger.getLogger(Retrieval.class.getName());

    /**
     * The pattern for setting name and password for the authentication.
     */
    private final String loginRequest = "<user>%n"
            + "<property name='name' value='%s'/>%n"
            + "<property name='pwd' value='%s'/>%n"
            + "</user>";
    /**
     * The username needed for the authentication.
     */
    private String username = PropertyLoader.getProperty("processeditor.serverName");
    
    /**
     * The password needed for the authentication.
     */
    private String password = PropertyLoader.getProperty("processeditor.serverPassword");

    /**
     * Get the content from URL. In case of the JComparser, it is used to get the XML of a processModel. Therefore,
     * the authentication for the ProcessEditor is used.
     *
     * @param hosturl   the basic hosturl (e.g. "http://localhost:1205/")
     * @param urlToRead contains the hosturl and additional path from which
     *                  the content should be retrieved
     *                  (e.g. "http://localhost:1205/models/")
     * @return the response as String from urlToRead
     */
    public String getXMLWithAuth(String hosturl, String urlToRead) {
        try {
            InputStream inputStream = getInputStream(hosturl, urlToRead);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            inputStream.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            log.error("Error: unable to read the processModel from the server. Tried to read " + urlToRead);
            log.error(e);
        }
        return null;
    }

    /**
     * Get an image from URL.
     *
     * @param hosturl   the basic hosturl (e.g. "http://localhost:1205/")
     * @param urlToRead contains the hosturl and additional path from which
     *                  html should be retrieved
     *                  (e.g. "http://localhost:1205/models/123456789.png")
     * @return the response as an image from urlToRead
     */
    public Response getImagewithAuth(String hosturl, String urlToRead) {
        try {
            InputStream inputStream = getInputStream(hosturl, urlToRead);
            BufferedImage image = ImageIO.read(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageData = baos.toByteArray();
            inputStream.close();

            // uncomment line below to send non-streamed
            return Response.ok(imageData).build();

            // uncomment line below to send streamed
            //return Response.ok(new ByteArrayInputStream(imageData)).build();
        } catch (IOException e) {
            log.error("Error: unable to load an image from the server. Tried to load the image from " + urlToRead);
            log.error(e);
        }
        return null;
    }

    /**
     * Get an image from URL.
     *
     * @param hosturl   the basic hosturl (e.g. "http://localhost:1205/")
     * @param urlToRead contains the hosturl and additional path from which
     *                  html should be retrieved
     *                  (e.g. "http://localhost:1205/models/123456789.png")
     * @return the response from urlToRead as an InputStream
     * @throws IOException 
     * @throws MalformedURLException 
     */
    private InputStream getInputStream(String hosturl, String urlToRead) throws MalformedURLException, IOException {
        HttpURLConnection connection;
        CookieHandler.setDefault(new CookieManager(
                null, CookiePolicy.ACCEPT_ALL));
        connection = (HttpURLConnection) new URL(hosturl + "users/login")
                .openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/xml");
        connection.setInstanceFollowRedirects(false);
        OutputStream os = connection.getOutputStream();
        PrintWriter osw = new PrintWriter(os);
        osw.println(String.format(loginRequest, username, password));
        osw.flush();
        osw.close();
        connection.getResponseCode();
        connection.getResponseMessage();
        HttpURLConnection modelsConnection = (HttpURLConnection)
                new URL(urlToRead).openConnection();
        modelsConnection.setInstanceFollowRedirects(false);
        modelsConnection.setRequestMethod("GET");
        InputStream inputStream = modelsConnection.getInputStream();
        connection.disconnect();
        return inputStream;
    }
}
