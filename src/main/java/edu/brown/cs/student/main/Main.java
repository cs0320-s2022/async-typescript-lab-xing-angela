package edu.brown.cs.student.main;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import spark.*;

/**
 * The Main class of our project. This is where execution begins.
 *
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;

  /**
   * The initial method called when execution begins.
   *
   * @param args
   *             An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);

    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }
  }

  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.exception(Exception.class, new ExceptionPrinter());

    // Setup Spark Routes

    // TODO: create a call to Spark.post to make a POST request to a URL which
    // will handle getting matchmaking results for the input
    // It should only take in the route and a new ResultsHandler
    Spark.post("/results", new ResultsHandler());

    Spark.options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");

      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });

    // Allows requests from any domain (i.e., any URL). This makes development
    // easier, but it’s not a good idea for deployment.
    Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * Handles requests for horoscope matching on an input
   *
   * @return GSON which contains the result of MatchMaker.makeMatches
   */
  private static class ResultsHandler implements Route {
    @Override
    public String handle(Request req, Response res) throws JSONException {
      // TODO: Get JSONObject from req and use it to get the value of the sun, moon,
      // and rising
      // for generating matches

      JSONObject reqJson = null;
      String sun = "";
      String moon = "";
      String rising = "";

      try {
        // Put the request's body in JSON format
        reqJson = new JSONObject(req.body());
        sun = reqJson.getString("sun");
        moon = reqJson.getString("moon");
        rising = reqJson.getString("rising");

      } catch (JSONException e) {
        e.printStackTrace();
        return null;
      }

      // TODO: use the MatchMaker.makeMatches method to get matches

      List<String> matches = MatchMaker.makeMatches(sun, moon, rising);

      // TODO: create an immutable map using the matches
      Map<String, List<String>> matchesMap = Map.of("suggestionsLst", matches);

      // TODO: return a json of the suggestions (HINT: use GSON.toJson())
      Gson GSON = new Gson();
      String suggestions = GSON.toJson(matchesMap);
      return suggestions;
    }
  }
}