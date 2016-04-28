package de.uni_potsdam.hpi.bpt.bp2014.jcore.rest;

import de.uni_potsdam.hpi.bpt.bp2014.database.DbScenario;
import de.uni_potsdam.hpi.bpt.bp2014.database.DbTerminationCondition;
import de.uni_potsdam.hpi.bpt.bp2014.jcomparser.json.ScenarioData;
import de.uni_potsdam.hpi.bpt.bp2014.jcomparser.validation.InvalidDataTransitionException;
import de.uni_potsdam.hpi.bpt.bp2014.jcomparser.validation.InvalidDataclassReferenceExeption;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Path("interface/v2/scenario")
public class ScenarioRestService {
    /**
     * This method provides information about the termination condition.
     * Of the specified Scenario.
     * The termination condition is a set of sets of conditions.
     * Only if all conditions of one set are true the scenario will
     * terminate.
     * If the scenario does not exists a 404 with an error will be returned.
     * If the scenario exists the JSON representation of the condition set
     * will be returned.
     *
     * @param scenarioID This id specifies the scenario. The id is the
     *                   primary key inside the database.
     * @return Returns a response object. It will either  be a 200 or
     * 404. The content will be either the JSON representation of the termination
     * condition or an JSON object with the error message.
     */
    @GET
    @Path("{scenarioId}/terminationcondition")
    @Produces(MediaType.APPLICATION_JSON) public Response getTerminationCondition(
            @PathParam("scenarioId") int scenarioID) {
        DbScenario dbScenario = new DbScenario();
        if (!dbScenario.existScenario(scenarioID)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"error\":\"There is no scenario with the id "
                            + scenarioID + "\"}")
                    .build();
        }
        DbTerminationCondition terminationCondition = new DbTerminationCondition();
        Map<String, List<Map<String, Object>>> conditionSets = terminationCondition
                .getDetailedConditionsForScenario(scenarioID);
        JSONObject conditions = new JSONObject(conditionSets);
        JSONObject result = new JSONObject();
        result.put("conditions", conditions);
        result.put("setIDs", new JSONArray(conditionSets.keySet()));
        return Response.ok(result.toString(), MediaType.APPLICATION_JSON).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postInstance(String scenario) {
        try {
            ScenarioData newScenario = new ScenarioData(scenario);
            newScenario.save();
            return Response.status(201).build();
        } catch (IllegalArgumentException e){
            return Response.status(422)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(buildException(e.getMessage()))
                    .build();
        } catch (JAXBException e) {
            return Response.status(400)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(buildException("Invalid xml " + e.getMessage()))
                    .build();
        } catch (InvalidDataTransitionException e) {
            return Response.status(422)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(buildException(e.getMessage()))
                    .build();
        } catch (InvalidDataclassReferenceExeption e) {
            return Response.status(422)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(buildException(e.getMessage()))
                    .build();
        }
    }

    private String buildException(String text) {
        JSONArray result = new JSONArray();
        JSONObject content = new JSONObject();
        content.put("text", text);
        content.put("type", "danger");
        result.put(content);
        return result.toString();
    }

    /**
     * This method provides information about one scenario.
     * The scenario is specified by an given id.
     * The response of this request will contain a valid JSON-Object
     * containing detailed information about the scenario.
     * If there is no scenario with the specific id a 404 will be returned,
     * with a meaningful error message.
     *
     * @param scenarioId The Id of the scenario used inside the database.
     * @param uri Request URI
     * @return Returns a JSON-Object with detailed information about one scenario.
     * The Information contain the id, label, number of instances, latest version
     * and more.
     */
    @GET
    @Path("{scenarioId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getScenario(
            @Context UriInfo uri, @PathParam("scenarioId") int scenarioId) {
        DbScenario dbScenario = new DbScenario();
        Map<String, Object> data = dbScenario.getScenarioDetails(scenarioId);

        if (data.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{}")
                    .build();
        }
        data.put("instances", uri.getAbsolutePath() + "/instance");
        return Response.ok().type(MediaType.APPLICATION_JSON)
                .entity(new JSONObject(data).toString()).build();
    }


    /**
     * This method allows to give an overview of all scenarios.
     * The response will return a JSON-Array containing the basic
     * information of all scenarios currently inside the database.
     * If different versions of an scenarios exist only the latest
     * ones will be added to the json.
     *
     * @param uriInfo      Specifies the context. For example the uri
     *                     of the request.
     * @param filterString Specifies a search. Only scenarios which
     *                     name contain the specified string will be
     *                     returned.
     * @return Returns a JSON-Object with an Array with entries for
     * every Scenario.
     * Each Entry is a JSON-Object with a label and id of a scenario.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getScenarios(
            @Context UriInfo uriInfo, @QueryParam("filter") String filterString) {
        DbScenario scenario = new DbScenario();
        Map<Integer, String> scenarios;
        if (filterString == null || "".equals(filterString)) {
            scenarios = scenario.getScenarios();
        } else {
            scenarios = scenario.getScenariosLike(filterString);
        }
        JSONObject jsonResult = mapToKeysAndResults(scenarios, "ids", "labels");
        JSONObject refs = new JSONObject();
        for (int id : scenarios.keySet()) {
            refs.put("" + id, uriInfo.getAbsolutePath() + "/" + id);
        }
        jsonResult.put("links", refs);
        return Response.ok()
                .type(MediaType.APPLICATION_JSON)
                .entity(jsonResult.toString())
                .build();
    }

    /**
     * Creates a JSON object from an HashMap.
     * The keys will be listed separately.
     *
     * @param data        The HashMap which contains the data of the Object
     * @param keyLabel    The name which will be used
     * @param resultLabel The label of the results.
     * @return The newly created JSON Object.
     */
    private JSONObject mapToKeysAndResults(Map data, String keyLabel, String resultLabel) {
        JSONObject result = new JSONObject();
        result.put(keyLabel, new JSONArray(data.keySet()));
        result.put(resultLabel, data);
        return result;
    }
}