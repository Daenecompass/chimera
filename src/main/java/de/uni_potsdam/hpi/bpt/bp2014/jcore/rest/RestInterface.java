package de.uni_potsdam.hpi.bpt.bp2014.jcore.rest;

import de.uni_potsdam.hpi.bpt.bp2014.database.DbEmailConfiguration;
import de.uni_potsdam.hpi.bpt.bp2014.database.DbScenario;
import de.uni_potsdam.hpi.bpt.bp2014.database.DbScenarioInstance;
import de.uni_potsdam.hpi.bpt.bp2014.jcore.*;
import de.uni_potsdam.hpi.bpt.bp2014.jcore.rest.TransportationBeans.DataObjectJaxBean;
import de.uni_potsdam.hpi.bpt.bp2014.jcore.rest.TransportationBeans.DataObjectSetsJaxBean;
import de.uni_potsdam.hpi.bpt.bp2014.jcore.rest.TransportationBeans.EmailConfigJaxBean;
import de.uni_potsdam.hpi.bpt.bp2014.settings.PropertyLoader;
import de.uni_potsdam.hpi.bpt.bp2014.util.JsonUtil;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;
import java.util.Map;

/**
 * This class implements the REST interface of the JEngine core.
 * The core module provides methods to execute PCM instances
 * and to access the date inside the engine.
 * This REST interface provides methods to access this information
 * and to control the instances.
 * Methods which are necessary for the controlling can be found
 * inside the {@link de.uni_potsdam.hpi.bpt.bp2014.jcore.ExecutionService}.
 * This class will use {@link de.uni_potsdam.hpi.bpt.bp2014.database.Connection}
 * to access the database directly.
 */
@Path("interface/v2") public class RestInterface {

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
	@GET @Path("scenario") @Produces(MediaType.APPLICATION_JSON) public Response getScenarios(
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
	 * This method provides information about all instances of one scenario.
	 * The scenario is specified by an given id.
	 * If there is no scenario with the specific id a 404 response with a meaningful
	 * error message will be returned.
	 * If the Scenario exists a JSON-Array containing JSON-Objects with
	 * important information about an instance of the scenario will be returned.
	 *
	 * @param uri Request URI.
	 * @param scenarioID   The id of the scenario which instances should be returned.
	 * @param filterString Specifies a search. Only scenarios which
	 *                     name contain the specified string will be
	 *                     returned.
	 * @return A JSON-Object with an array of information about all instances of
	 * one specified scenario. The information contains the id and name.
	 */
	@GET
	@Path("scenario/{scenarioID}/instance")
	@Produces(MediaType.APPLICATION_JSON) public Response getScenarioInstances(
			@Context UriInfo uri,
			@PathParam("scenarioID") int scenarioID,
			@QueryParam("filter") String filterString) {
		ExecutionService executionService = ExecutionService.getInstance(scenarioID);
		if (!executionService.existScenario(scenarioID)) {
			return Response.status(Response.Status.NOT_FOUND)
					.type(MediaType.APPLICATION_JSON)
					.entity("{\"error\":\"Scenario not found!\"}")
					.build();
		}
		DbScenarioInstance instance = new DbScenarioInstance();
		JSONObject result = new JSONObject();
		Map<Integer, String> data =
				instance.getScenarioInstancesLike(scenarioID, filterString);
		JSONObject links = new JSONObject();
		for (int id : data.keySet()) {
			links.put("" + id, uri.getAbsolutePath() + "/" + id);
		}
		result.put("ids", new JSONArray(data.keySet()));
		result.put("labels", new JSONObject(data));
		result.put("links", links);
		return Response.ok(result.toString(), MediaType.APPLICATION_JSON).build();
	}







	/**
	 * Method for checking whether an address specified in the griffin editor
	 * links to a valid running chimera instance
	 * @return Response containing the version.
     */
	@GET
	@Path("version")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVersion() {
		String version = PropertyLoader.getProperty("webapp.version");

		return Response.status(Response.Status.OK)
				.type(MediaType.APPLICATION_JSON)
				.entity("{\"version\": \""
						+ version + "\"}")
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

	// ************************* RootElement ********************************************/


}
