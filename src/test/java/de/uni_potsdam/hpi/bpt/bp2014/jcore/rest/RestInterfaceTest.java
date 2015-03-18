package de.uni_potsdam.hpi.bpt.bp2014.jcore.rest;

import com.ibatis.common.jdbc.ScriptRunner;
import de.uni_potsdam.hpi.bpt.bp2014.AbstractTest;
import de.uni_potsdam.hpi.bpt.bp2014.database.Connection;
import net.javacrumbs.jsonunit.core.Option;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.junit.Assert.*;

/**
 * This Class extends the {@link de.uni_potsdam.hpi.bpt.bp2014.AbstractTest}
 * to test the RestInterface of the JEngine core.
 * In order to do so it uses the functionality of the
 * {@link org.glassfish.jersey.test.JerseyTest}
 * There are test methods for every possible REST Call.
 * In order to stay independent from existing tests, the
 * database will be set up before and after the execution.
 * Define the database Properties inside the database_connection file.
 */
public class RestInterfaceTest extends AbstractTest {

    private static final String DEVELOPMENT_SQL_SEED_FILE = "src/main/resources/JEngineV2.sql";
    /**
     * Sets up the seed file for the test database.
     */
    static {
        TEST_SQL_SEED_FILE = "src/test/resources/JEngineV2RESTTest_new.sql";
    }
    /**
     * The base url of the jcore rest interface.
     * Allows us to send requests to the {@link de.uni_potsdam.hpi.bpt.bp2014.jcore.rest.RestInterface}.
     */
    private WebTarget base;

    @AfterClass
    public static void resetDatabase() throws IOException, SQLException {
        clearDatabase();
        ScriptRunner runner = new ScriptRunner(Connection.getInstance().connect(), false, false);
        runner.runScript(new FileReader(DEVELOPMENT_SQL_SEED_FILE));
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(de.uni_potsdam.hpi.bpt.bp2014.jcore.rest.RestInterface.class);
    }

    @Before
    public void setUpBase() {
        base = target("v2/");
    }

    /**
     * When you sent a GET to {@link RestInterface#getScenarios(String)}
     * the media type of the response will be JSON.
     */
    @Test
    public void testGetScenarioProducesJson() {
        Response response = base.path("scenario").request().get();
        assertEquals("The Response code of get Scenario was not 200",
                200, response.getStatus());
        assertEquals("Get Scenarios returns a Response with the wrong media Type",
                MediaType.APPLICATION_JSON, response.getMediaType().toString());
    }

    /**
     * When you sent a get to {@link RestInterface#getScenarios(String)}
     * the entity of the response will be a valid JSON array.
     */
    @Test
    public void testGetScenarioProducesValidJsonArray() {
        Response response = base.path("scenario").request().get();
        assertNotEquals("Get scenarios did not respond with a valid JSON Array",
                null, new JSONObject(response.readEntity(String.class)));
    }

    /**
     * When you sent a GET to {@link RestInterface#getScenarios(String)}
     * the returned JSON will contain the latest version of all Scenarios.
     */
    @Test
    public void testGetScenarioContent() {
        Response response = base.path("scenario").request().get();
        assertThat("Get Scenarios did not contain the expected information",
                "{\"ids\":[1,2,3,100,101,103,105,111,113,114,115,116,117,118,134,135,136,138,139,140,141,142,143,144],\"labels\":{\"1\":\"HELLOWORLD\",\"3\":\"EmailTest\",\"2\":\"helloWorld2\",\"100\":\"TestScenario\",\"101\":\"Test Insert Scenario\",\"134\":\"ReiseTestScenario\",\"103\":\"ScenarioTest1\",\"135\":\"ReiseTestScenario\",\"136\":\"TXOR1Scenario\",\"105\":\"TestScenarioTerminationCondition\",\"138\":\"TestEmail1Scenario\",\"139\":\"TestEmail1Scenario\",\"140\":\"TestEmail1Scenario\",\"141\":\"TestEmail2Scenario\",\"142\":\"TestEmail3Scenario\",\"111\":\"Test2_2ReferenceTest\",\"143\":\"TestEmail3Scenario\",\"144\":\"XORTest2Scenario\",\"113\":\"referencetest3_2\",\"114\":\"RT4Scenario\",\"115\":\"TT2Scenario\",\"116\":\"TT2Scenario\",\"117\":\"AT2Scenario\",\"118\":\"AT3Scenario\"}}",
                jsonEquals(response.readEntity(String.class)).when(Option.IGNORING_ARRAY_ORDER));
    }

    /**
     * When you sent a GET to {@link RestInterface#getScenarios(String)} and
     * you use a Filter
     * then the returned JSON will contain the latest version of all Scenarios with
     * a name containing the filterString.
     */
    @Test
    public void testGetScenarioContentWithFilter() {
        Response response = base.path("scenario").queryParam("filter", "HELLO").request().get();
        assertThat("Get Scenarios did not contain the expected information",
                "{\"ids\":[1,2],\"labels\":{\"1\":\"HELLOWORLD\",\"2\":\"helloWorld2\"}}",
                jsonEquals(response.readEntity(String.class)).when(Option.IGNORING_ARRAY_ORDER));
    }

    /**
     * When you send a GET to {@link RestInterface#getScenario(int) with an invalid id
     * a empty JSON with a 404 will be returned.
     */
    @Test
    public void testGetScenarioInvalidId() {
        Response response = base.path("scenario/99999").request().get();
        assertEquals("Get Scenario returns a Response with the wrong media Type",
                MediaType.APPLICATION_JSON, response.getMediaType().toString());
        assertEquals("Get scenario returns a not empty JSON, but the id was invalid",
                404, response.getStatus());
        assertEquals("The content of the invalid request is not an empty JSONObject",
                "{}",
                response.readEntity(String.class));
    }

    /**
     * If you send a GET to {@link RestInterface#getScenario(int)} with an valid id
     * a JSON containing the id, name and modelversion will be returned.
     */
    @Test
    public void testGetScenario() {
        Response response = base.path("scenario/1").request().get();
        String responseEntity = response.readEntity(String.class);
        assertEquals("The Response code of get Scenario was not 200",
                200, response.getStatus());
        assertEquals("Get Scenarios returns a Response with the wrong media Type",
                MediaType.APPLICATION_JSON, response.getMediaType().toString());
        assertNotEquals("Get scenarios did not respond with a valid JSON Array",
                null, new JSONObject(responseEntity));
        assertThat("The content of the valid request is not as expected",
                "{\"id\":1,\"name\":\"HELLOWORLD\",\"modelversion\":0}",
                jsonEquals(responseEntity).when(Option.IGNORING_ARRAY_ORDER));

    }

    /**
     * When you send a GET to {@link RestInterface#getAllEmailTasks(int, String)}
     * the response should be of type json.
     */
    @Test
    public void testGetMailTasksReturnsJson() {
        Response response = base.path("scenario/1/emailtask").request().get();
        assertEquals("The Response code of get all mail tasks was not 200",
                200, response.getStatus());
        assertEquals("Get all mail tasks returns a Response with the wrong media Type",
                MediaType.APPLICATION_JSON, response.getMediaType().toString());
    }

    /**
     * When you send a GET to {@link RestInterface#getAllEmailTasks(int, String)} and
     * the scenario does not contain any mail task an object with no ids will be returned.
     */
    @Test
    public void testGetAllMailTasksIfAbsent() {
        Response response = base.path("scenario/1/emailtask").request().get();
        assertThat("Get all mail Tasks returns not an empty JSON Object when the scenario has no mail tasks",
                "{\"ids\":[]}", jsonEquals(response.readEntity(String.class)));
    }

    /**
     * When you send a GET to {@link RestInterface#getAllEmailTasks(int, String)}
     * the returned JSON Object should be a specified.
     * {"ids":[1,2,...],"labels":{1:"abcd"...}}}
     */
    @Test
    public void testGetAllMailTasksJSONResponse() {
        Response response = base.path("scenario/142/emailtask").request().get();
        assertThat("Get all mail Tasks returns not an empty JSON Object when the scenario has no mail tasks",
                "{\"ids\":[362]}", jsonEquals(response.readEntity(String.class)));
    }

    /**
     * When you send a Get to {@link RestInterface#getAllEmailTasks(int, String)}
     * and the ScenarioID is invalid a 404 will be returned but the media type is still
     * JSON.
     */
    @Test
    public void testGetAllMailTasksMissingScenario() {
        Response response = base.path("scenario/99999/emailtask").request().get();
        assertEquals("The Response code of get all mail tasks was not 404",
                404, response.getStatus());
        assertEquals("Get all mail tasks returns a Response with the wrong media Type",
                MediaType.APPLICATION_JSON, response.getMediaType().toString());
    }

    /**
     * When you send a Get to {@link RestInterface#getEmailTaskConfiguration(int, int)}
     * with an invalid scenario an empty JSON object should be returned, with a 404.
     */
    @Test
    public void testGetEmailTaskConfigurationMissingScenario() {
        Response response = base.path("scenario/99999/emailtask/1").request().get();
        assertEquals("The Response code of get email task configuration was not 404",
                404, response.getStatus());
        assertEquals("Get mail task configuration responses with the wrong media Type",
                MediaType.APPLICATION_JSON, response.getMediaType().toString());
    }

    /**
     * When you send a Get to {@link RestInterface#getEmailTaskConfiguration(int, int)}
     * with an invalid mailTask an empty JSON object should be returned, with a 404.
     */
    @Test
    public void testGetEmailTaskConfigurationMissingMailTask() {
        Response response = base.path("scenario/1/emailtask/9999").request().get();
        assertEquals("The Response code of get email task configuration was not 404",
                404, response.getStatus());
        assertEquals("Get mail task configuration responses with the wrong media Type",
                MediaType.APPLICATION_JSON, response.getMediaType().toString());
    }

    /**
     * When you send a Get to {@link RestInterface#getEmailTaskConfiguration(int, int)}
     * a 200 with an json object should be returned
     */
    @Test
    public void testGetEmailTaskReturnsJSON() {
        Response response = base.path("scenario/142/emailtask/353").request().get();
        assertEquals("The Response code of get  mail configuration was not 200",
                200, response.getStatus());
        assertEquals("Get all mail tasks returns a Response with the wrong media Type",
                MediaType.APPLICATION_JSON, response.getMediaType().toString());
    }

    /**
     * When you send a Get to {@link RestInterface#getEmailTaskConfiguration(int, int)}
     * a valid json object with "receiver", "content", "subject" should be returned
     */
    @Test
    public void testGetEmailTaskReturnsCorrectJSON() {
        Response response = base.path("scenario/142/emailtask/353").request().get();
        assertThat("Get mail Task configuration returns not an valid JSON object",
                "{\"receiver\":\"bp2014w1@byom.de\",\"subject\":\"Test\",\"content\":\"Test Message\"}",
                jsonEquals(response.readEntity(String.class)).when(Option.IGNORING_ARRAY_ORDER));
    }

    /**
     * When you send a Get to {@link RestInterface#getScenarioInstances(int, String)}
     * with valid params and no filter
     * then you get 200 a JSON Object.
     */
    @Test
    public void testGetScenarioInstancesReturnsOkAndJSON() {
        Response response = base.path("scenario/1/instance").request().get();
        assertEquals("The Response code of get get instances was not 200",
                200, response.getStatus());
        assertEquals("Get instances returns a Response with the wrong media Type",
                MediaType.APPLICATION_JSON, response.getMediaType().toString());
    }

    /**
     * When you send a Get to {@link  RestInterface#getScenarioInstances(int, String)}
     * with an invalid scenario
     * then you get 404 with an error message inside the returned JSON object
     */
    @Test
    public void testGetScenarioInstancesInvalidScenario() {
        Response response = base.path("scenario/9999/instance").request().get();
        assertEquals("The Response code of get get instances was not 404",
                404, response.getStatus());
        assertEquals("Get instances returns a Response with the wrong media Type",
                MediaType.APPLICATION_JSON, response.getMediaType().toString());
        assertThat("The returned JSON is invalid or does not contain the expected message",
                "{\"error\":\"Scenario not found!\"}",
                jsonEquals(response.readEntity(String.class)));
    }

    /**
     * When you send a Get to {@link  RestInterface#getScenarioInstances(int, String)}
     * with a valid scenario id
     * then a json object with all instances, id and label should be returned.
     * The schema should be:
     * {"ids": [1,2..], "names":{1: "abc" ...}}
     */
    @Test
    public void testGetScenarioInstancesReturnsCorrectJson() {
        Response response = base.path("scenario/1/instance").request().get();
        assertThat("The returned JSON does not contain the expected content",
                "{\"ids\":[47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,92,94,95,97,99,101,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,212,214,215,216,217,218,219,220,221,222,223,224,226,228,244,246,248,250,252,255,257,259,261,262,263,265,266,270,279,281,282,284,285,286,294,296,309,310,312,313,314,315,316,317,318,319,320,321,322,323,324,325,326,327,328,333,334,346,348,349,351,353,356,358,359,361,363,365,366,367,369,370,371,372,373,374,375,376,377,378,379,380,381,383,385,386,387,388,390,392,394,396,398,400,402,404,406,408,409,410,411,412,413,415,416,417,418,419,420,421,422,430,433,438,441,444,445,446,448,449,452,456,459,460,461,464,467,470,473,476,479,482,485,488,491,492,493,494,497,505,515,519,527,531,552,561,563,572,582,590,598,605,612,614,621,628,635,642,649,656,663,670,677,684,691,698,705,712,719,726,733,740,747,754,756,763,770,777,784,791,809,816,823,836,846,853,860,867,895,915,951,952],\"labels\":{\"515\":\"HELLOWORLD\",\"519\":\"HELLOWORLD\",\"527\":\"HELLOWORLD\",\"531\":\"HELLOWORLD\",\"552\":\"HELLOWORLD\",\"47\":\"HELLOWORLD\",\"48\":\"HELLOWORLD\",\"49\":\"HELLOWORLD\",\"561\":\"HELLOWORLD\",\"50\":\"HELLOWORLD\",\"51\":\"HELLOWORLD\",\"563\":\"HELLOWORLD\",\"52\":\"HELLOWORLD\",\"53\":\"HELLOWORLD\",\"54\":\"HELLOWORLD\",\"55\":\"HELLOWORLD\",\"56\":\"HELLOWORLD\",\"57\":\"HELLOWORLD\",\"58\":\"HELLOWORLD\",\"59\":\"HELLOWORLD\",\"60\":\"HELLOWORLD\",\"572\":\"HELLOWORLD\",\"61\":\"HELLOWORLD\",\"62\":\"HELLOWORLD\",\"63\":\"HELLOWORLD\",\"64\":\"HELLOWORLD\",\"65\":\"HELLOWORLD\",\"66\":\"HELLOWORLD\",\"67\":\"HELLOWORLD\",\"68\":\"HELLOWORLD\",\"69\":\"HELLOWORLD\",\"70\":\"HELLOWORLD\",\"582\":\"HELLOWORLD\",\"71\":\"HELLOWORLD\",\"72\":\"HELLOWORLD\",\"73\":\"HELLOWORLD\",\"74\":\"HELLOWORLD\",\"75\":\"HELLOWORLD\",\"76\":\"HELLOWORLD\",\"77\":\"HELLOWORLD\",\"78\":\"HELLOWORLD\",\"590\":\"HELLOWORLD\",\"79\":\"HELLOWORLD\",\"80\":\"HELLOWORLD\",\"81\":\"HELLOWORLD\",\"82\":\"HELLOWORLD\",\"83\":\"HELLOWORLD\",\"84\":\"HELLOWORLD\",\"598\":\"HELLOWORLD\",\"92\":\"HELLOWORLD\",\"605\":\"HELLOWORLD\",\"94\":\"HELLOWORLD\",\"95\":\"HELLOWORLD\",\"97\":\"HELLOWORLD\",\"99\":\"HELLOWORLD\",\"612\":\"HELLOWORLD\",\"101\":\"HELLOWORLD\",\"614\":\"HELLOWORLD\",\"103\":\"HELLOWORLD\",\"104\":\"HELLOWORLD\",\"105\":\"HELLOWORLD\",\"106\":\"HELLOWORLD\",\"107\":\"HELLOWORLD\",\"108\":\"HELLOWORLD\",\"109\":\"HELLOWORLD\",\"621\":\"HELLOWORLD\",\"110\":\"HELLOWORLD\",\"111\":\"HELLOWORLD\",\"112\":\"HELLOWORLD\",\"113\":\"HELLOWORLD\",\"114\":\"HELLOWORLD\",\"115\":\"HELLOWORLD\",\"116\":\"HELLOWORLD\",\"628\":\"HELLOWORLD\",\"117\":\"HELLOWORLD\",\"118\":\"HELLOWORLD\",\"119\":\"HELLOWORLD\",\"120\":\"HELLOWORLD\",\"121\":\"HELLOWORLD\",\"122\":\"HELLOWORLD\",\"123\":\"HELLOWORLD\",\"635\":\"HELLOWORLD\",\"124\":\"HELLOWORLD\",\"125\":\"HELLOWORLD\",\"126\":\"HELLOWORLD\",\"127\":\"HELLOWORLD\",\"128\":\"HELLOWORLD\",\"129\":\"HELLOWORLD\",\"130\":\"HELLOWORLD\",\"642\":\"HELLOWORLD\",\"131\":\"HELLOWORLD\",\"132\":\"HELLOWORLD\",\"133\":\"HELLOWORLD\",\"134\":\"HELLOWORLD\",\"135\":\"HELLOWORLD\",\"136\":\"HELLOWORLD\",\"137\":\"HELLOWORLD\",\"649\":\"HELLOWORLD\",\"138\":\"HELLOWORLD\",\"139\":\"HELLOWORLD\",\"140\":\"HELLOWORLD\",\"141\":\"HELLOWORLD\",\"142\":\"HELLOWORLD\",\"143\":\"HELLOWORLD\",\"144\":\"HELLOWORLD\",\"656\":\"HELLOWORLD\",\"145\":\"HELLOWORLD\",\"146\":\"HELLOWORLD\",\"147\":\"HELLOWORLD\",\"148\":\"HELLOWORLD\",\"149\":\"HELLOWORLD\",\"150\":\"HELLOWORLD\",\"151\":\"HELLOWORLD\",\"663\":\"HELLOWORLD\",\"152\":\"HELLOWORLD\",\"153\":\"HELLOWORLD\",\"154\":\"HELLOWORLD\",\"155\":\"HELLOWORLD\",\"156\":\"HELLOWORLD\",\"157\":\"HELLOWORLD\",\"158\":\"HELLOWORLD\",\"670\":\"HELLOWORLD\",\"159\":\"HELLOWORLD\",\"160\":\"HELLOWORLD\",\"161\":\"HELLOWORLD\",\"162\":\"HELLOWORLD\",\"163\":\"HELLOWORLD\",\"164\":\"HELLOWORLD\",\"165\":\"HELLOWORLD\",\"677\":\"HELLOWORLD\",\"166\":\"HELLOWORLD\",\"167\":\"HELLOWORLD\",\"168\":\"HELLOWORLD\",\"169\":\"HELLOWORLD\",\"170\":\"HELLOWORLD\",\"171\":\"HELLOWORLD\",\"172\":\"HELLOWORLD\",\"684\":\"HELLOWORLD\",\"173\":\"HELLOWORLD\",\"174\":\"HELLOWORLD\",\"175\":\"HELLOWORLD\",\"176\":\"HELLOWORLD\",\"177\":\"HELLOWORLD\",\"178\":\"HELLOWORLD\",\"179\":\"HELLOWORLD\",\"691\":\"HELLOWORLD\",\"180\":\"HELLOWORLD\",\"181\":\"HELLOWORLD\",\"182\":\"HELLOWORLD\",\"183\":\"HELLOWORLD\",\"184\":\"HELLOWORLD\",\"185\":\"HELLOWORLD\",\"186\":\"HELLOWORLD\",\"698\":\"HELLOWORLD\",\"187\":\"HELLOWORLD\",\"188\":\"HELLOWORLD\",\"189\":\"HELLOWORLD\",\"190\":\"HELLOWORLD\",\"191\":\"HELLOWORLD\",\"192\":\"HELLOWORLD\",\"193\":\"HELLOWORLD\",\"705\":\"HELLOWORLD\",\"194\":\"HELLOWORLD\",\"195\":\"HELLOWORLD\",\"196\":\"HELLOWORLD\",\"197\":\"HELLOWORLD\",\"198\":\"HELLOWORLD\",\"199\":\"HELLOWORLD\",\"200\":\"HELLOWORLD\",\"712\":\"HELLOWORLD\",\"201\":\"HELLOWORLD\",\"202\":\"HELLOWORLD\",\"203\":\"HELLOWORLD\",\"204\":\"HELLOWORLD\",\"205\":\"HELLOWORLD\",\"206\":\"HELLOWORLD\",\"207\":\"HELLOWORLD\",\"719\":\"HELLOWORLD\",\"208\":\"HELLOWORLD\",\"209\":\"HELLOWORLD\",\"210\":\"HELLOWORLD\",\"212\":\"HELLOWORLD\",\"214\":\"HELLOWORLD\",\"726\":\"HELLOWORLD\",\"215\":\"HELLOWORLD\",\"216\":\"HELLOWORLD\",\"217\":\"HELLOWORLD\",\"218\":\"HELLOWORLD\",\"219\":\"HELLOWORLD\",\"220\":\"HELLOWORLD\",\"221\":\"HELLOWORLD\",\"733\":\"HELLOWORLD\",\"222\":\"HELLOWORLD\",\"223\":\"HELLOWORLD\",\"224\":\"HELLOWORLD\",\"226\":\"HELLOWORLD\",\"228\":\"HELLOWORLD\",\"740\":\"HELLOWORLD\",\"747\":\"HELLOWORLD\",\"754\":\"HELLOWORLD\",\"244\":\"HELLOWORLD\",\"756\":\"HELLOWORLD\",\"246\":\"HELLOWORLD\",\"248\":\"HELLOWORLD\",\"250\":\"HELLOWORLD\",\"763\":\"HELLOWORLD\",\"252\":\"HELLOWORLD\",\"255\":\"HELLOWORLD\",\"257\":\"HELLOWORLD\",\"770\":\"HELLOWORLD\",\"259\":\"HELLOWORLD\",\"261\":\"HELLOWORLD\",\"262\":\"HELLOWORLD\",\"263\":\"HELLOWORLD\",\"265\":\"HELLOWORLD\",\"777\":\"HELLOWORLD\",\"266\":\"HELLOWORLD\",\"270\":\"HELLOWORLD\",\"784\":\"HELLOWORLD\",\"279\":\"HELLOWORLD\",\"791\":\"HELLOWORLD\",\"281\":\"HELLOWORLD\",\"282\":\"HELLOWORLD\",\"284\":\"HELLOWORLD\",\"285\":\"HELLOWORLD\",\"286\":\"HELLOWORLD\",\"294\":\"HELLOWORLD\",\"296\":\"HELLOWORLD\",\"809\":\"HELLOWORLD\",\"816\":\"HELLOWORLD\",\"309\":\"HELLOWORLD\",\"310\":\"HELLOWORLD\",\"823\":\"HELLOWORLD\",\"312\":\"HELLOWORLD\",\"313\":\"HELLOWORLD\",\"314\":\"HELLOWORLD\",\"315\":\"HELLOWORLD\",\"316\":\"HELLOWORLD\",\"317\":\"HELLOWORLD\",\"318\":\"HELLOWORLD\",\"319\":\"HELLOWORLD\",\"320\":\"HELLOWORLD\",\"321\":\"HELLOWORLD\",\"322\":\"HELLOWORLD\",\"323\":\"HELLOWORLD\",\"324\":\"HELLOWORLD\",\"836\":\"HELLOWORLD\",\"325\":\"HELLOWORLD\",\"326\":\"HELLOWORLD\",\"327\":\"HELLOWORLD\",\"328\":\"HELLOWORLD\",\"333\":\"HELLOWORLD\",\"334\":\"HELLOWORLD\",\"846\":\"HELLOWORLD\",\"853\":\"HELLOWORLD\",\"346\":\"HELLOWORLD\",\"348\":\"HELLOWORLD\",\"860\":\"HELLOWORLD\",\"349\":\"HELLOWORLD\",\"351\":\"HELLOWORLD\",\"353\":\"HELLOWORLD\",\"867\":\"HELLOWORLD\",\"356\":\"HELLOWORLD\",\"358\":\"HELLOWORLD\",\"359\":\"HELLOWORLD\",\"361\":\"HELLOWORLD\",\"363\":\"HELLOWORLD\",\"365\":\"HELLOWORLD\",\"366\":\"HELLOWORLD\",\"367\":\"HELLOWORLD\",\"369\":\"HELLOWORLD\",\"370\":\"HELLOWORLD\",\"371\":\"HELLOWORLD\",\"372\":\"HELLOWORLD\",\"373\":\"HELLOWORLD\",\"374\":\"HELLOWORLD\",\"375\":\"HELLOWORLD\",\"376\":\"HELLOWORLD\",\"377\":\"HELLOWORLD\",\"378\":\"HELLOWORLD\",\"379\":\"HELLOWORLD\",\"380\":\"HELLOWORLD\",\"381\":\"HELLOWORLD\",\"383\":\"HELLOWORLD\",\"895\":\"HELLOWORLD\",\"385\":\"HELLOWORLD\",\"386\":\"HELLOWORLD\",\"387\":\"HELLOWORLD\",\"388\":\"HELLOWORLD\",\"390\":\"HELLOWORLD\",\"392\":\"HELLOWORLD\",\"394\":\"HELLOWORLD\",\"396\":\"HELLOWORLD\",\"398\":\"HELLOWORLD\",\"400\":\"HELLOWORLD\",\"402\":\"HELLOWORLD\",\"915\":\"HELLOWORLD\",\"404\":\"HELLOWORLD\",\"406\":\"HELLOWORLD\",\"408\":\"HELLOWORLD\",\"409\":\"HELLOWORLD\",\"410\":\"HELLOWORLD\",\"411\":\"HELLOWORLD\",\"412\":\"HELLOWORLD\",\"413\":\"HELLOWORLD\",\"415\":\"HELLOWORLD\",\"416\":\"HELLOWORLD\",\"417\":\"HELLOWORLD\",\"418\":\"HELLOWORLD\",\"419\":\"HELLOWORLD\",\"420\":\"HELLOWORLD\",\"421\":\"HELLOWORLD\",\"422\":\"HELLOWORLD\",\"430\":\"HELLOWORLD\",\"433\":\"HELLOWORLD\",\"438\":\"HELLOWORLD\",\"951\":\"HELLOWORLD\",\"952\":\"HELLOWORLD\",\"441\":\"HELLOWORLD\",\"444\":\"HELLOWORLD\",\"445\":\"HELLOWORLD\",\"446\":\"HELLOWORLD\",\"448\":\"HELLOWORLD\",\"449\":\"HELLOWORLD\",\"452\":\"HELLOWORLD\",\"456\":\"HELLOWORLD\",\"459\":\"HELLOWORLD\",\"460\":\"HELLOWORLD\",\"461\":\"HELLOWORLD\",\"464\":\"HELLOWORLD\",\"467\":\"HELLOWORLD\",\"470\":\"HELLOWORLD\",\"473\":\"HELLOWORLD\",\"476\":\"HELLOWORLD\",\"479\":\"HELLOWORLD\",\"482\":\"HELLOWORLD\",\"485\":\"HELLOWORLD\",\"488\":\"HELLOWORLD\",\"491\":\"HELLOWORLD\",\"492\":\"HELLOWORLD\",\"493\":\"HELLOWORLD\",\"494\":\"HELLOWORLD\",\"497\":\"HELLOWORLD\",\"505\":\"HELLOWORLD\"}}",
                jsonEquals(response.readEntity(String.class))
                        .when(Option.IGNORING_ARRAY_ORDER));
    }

    /**
     * When you send a Get to {@link  RestInterface#getScenarioInstances(int, String)}
     * with a valid scenario id and a filter
     * only instances with names containing this string will be returned.
     */
    @Test
    public void testGetScenarioInstancesWithFilter() {
        Response response = base.path("scenario/1/instance").queryParam("filter", "noInstanceLikeThis").request().get();
        assertThat("The returned JSON does not contain the expected content",
                "{\"ids\":[],\"labels\":{}}",
                jsonEquals(response.readEntity(String.class))
                        .when(Option.IGNORING_ARRAY_ORDER));
    }
}
