package pt.iscte.iul;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import okhttp3.*;

import java.io.IOException;

public class TrelloAPI {
    private final String apiKey;
    private final String apiToken;
    private final String boardName;
    private final String baseAPIUrl;
    private final OkHttpClient httpClient;
    private final String boardURL = "https://api.trello.com/1/boards/";
    private final String boardId = "614df1d076293f6b763c1c9c";

    public TrelloAPI(String boardName, String apiKey, String apiToken) {
        this.apiKey = apiKey;
        this.apiToken = apiToken;
        this.boardName = boardName;

        //TODO: Function to get the id of the board giving the name of the board
        this.baseAPIUrl = "https://api.trello.com/1/members/me/boards?key=" + apiKey + "&token=" + apiToken;

        this.httpClient = new OkHttpClient();
    }

    public static class Board {
        private String name;
        private String id;
        private String url;

        public String getName() {
            return this.name;
        }

        public String getId() {
            return this.id;
        }

        public String getUrl() {
            return this.url;
        }
    }

    public static class List {
        private String name;
        private String id;

        public String getName() {
            return this.name;
        }

        public String getId() {
            return this.id;
        }
    }

    // TODO: The class Card needs more attributes for the rest of the project
    public static class Card {
        private String name;
        private String id;
        private String due;
        private String desc;

        public String getName() {
            return this.name;
        }

        public String getId() {
            return this.id;
        }

        public String getDueDate() {
            return this.due.split("T")[0]; // split by delimiter T
        }

        public String desc() {
            return this.desc;
        }
    }

    // Function to access every user's boards
    public Board[] getBoards() throws IOException {
        //HTTP request to access every user's boards
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .url(this.baseAPIUrl).build();

        Response response = this.httpClient.newCall(request).execute();

        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        // map http response to the class Board
        return mapper.readValue(response.body().string(), Board[].class);
    }

    // Function for HTTP request for components
    private Response HTTPRequest(String component, String componentId) throws IOException {
        //HTTP request to access the board
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .url(this.boardURL + componentId + "/" + component + "?key=" + apiKey + "&token=" + apiToken).build();

        Response response = this.httpClient.newCall(request).execute();
        return response;
    }

    // Function to return the Board that the user specified at login
    public Board getBoard(String boardId) throws IOException {
        //HTTP request to access the board
        Response response = HTTPRequest("", boardId);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Board
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        return mapper.readValue(response.body().string(), Board.class);
    }

    // Function to return all the lists in the board
    public List[] getBoardLists(String boardId) throws IOException {
        //HTTP request to access the board
        Response response = HTTPRequest("lists", boardId);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Board
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        // map http response to the class Board
        return mapper.readValue(response.body().string(), List[].class);
    }

    // Function to return all the lists in the board
    public Card[] getBoardCards(String boardId) throws IOException {
        //HTTP request to access the board
        Response response = HTTPRequest("cards", boardId);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Board
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        // map http response to the class Board
        return mapper.readValue(response.body().string(), Card[].class);
    }

    // Function to get start and end date of a specific sprint
    // TODO: Access specific cards based on a specific list to reduce search time
    public String[] getSprintDates(int SprintNumber) throws IOException {
        // flag to see if we've found the start date
        boolean startDateFound = false;
        // initialize list of dates
        String[] dates = new String[2];

        var cards = this.getBoardCards(this.boardId);
        // Iterate over all cards
        for (Card c : cards) {
            // search for due date of Sprint Planning that is equal to Sprint start date
            if (c.name.equals("Sprint Planning - Sprint " + SprintNumber)) {
                dates[0] = c.getDueDate();
                startDateFound = true;
            }
            // search for due date of Sprint Retrospective that is equal to Sprint end date
            else if (c.name.equals("Sprint Retrospective - Sprint " + SprintNumber)) {
                dates[1] = c.getDueDate();
                if (startDateFound) break; // if start date is found, we can break the for loop
            }
        }
        // Returns dates list
        // dates[0] -> Sprint start date
        // dates[1] -> Sprint end date
        return dates;
    }

    // Function to get the descriptions of the Sprint Ceremonies of a specific sprint
    // TODO: Access specific cards based on a specific list to reduce search time
    public void /*String[]*/ getCeremoniesDescription(int SprintNumber) throws IOException {
        // initialize list of descriptions
        String[] descriptions = new String[3];

        var cards = this.getBoardCards(this.boardId);
        // Iterate over all cards
        for (Card c : cards) {
            // get the Sprint Planning description
            if (c.name.equals("Sprint Planning - Sprint " + SprintNumber)) descriptions[0] = c.desc();
            /*
                // get the Sprint Review description
            else if (c.name.equals("Sprint Review - Sprint " + SprintNumber)) descriptions[1] = c.desc();
                // get the Sprint Retrospective description
            else if (c.name.equals("Sprint Retrospective - Sprint " + SprintNumber)) descriptions[2] = c.desc();
            */
        }
        System.out.println("Description of Sprint Planning \n" + descriptions[0]);

        // Returns descriptions list
        // descriptions[0] -> Sprint Planning Description
        // descriptions[1] -> Sprint Review Description
        // descriptions[2] -> Sprint Retrospective Description
        //return descriptions;
    }

    public void main(String[] args) throws IOException {

        this.getCeremoniesDescription(1);

    }
}