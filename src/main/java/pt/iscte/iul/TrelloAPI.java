package pt.iscte.iul;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Duarte Casaleiro.
 */
public class TrelloAPI {
    private final String apiKey;
    private final String apiToken;
    private final String boardName;
    private final String baseAPIUrl;
    private final OkHttpClient httpClient;
    private final String boardURL = "https://api.trello.com/1/boards/";
    private final String cardURL = "https://api.trello.com/1/cards/";
    private final String listURL = "https://api.trello.com/1/lists/";

    /**
     * Base class for requesting information from the Trello API.
     *
     * @param boardName Name of the board.
     * @param apiKey    Trello API access key.
     * @param apiToken  Trello API access token.
     */
    public TrelloAPI(String boardName, String apiKey, String apiToken) {
        this.apiKey = apiKey;
        this.apiToken = apiToken;
        this.boardName = boardName;

        //TODO: Function to get the id of the board giving the name of the board
        this.baseAPIUrl = "https://api.trello.com/1/members/me/boards?key=" + apiKey + "&token=" + apiToken;

        this.httpClient = new OkHttpClient();
    }

    /**
     * Board object.
     */
    public static class Board {
        private String name;
        private String id;
        private String url;

        /**
         * @return The name.
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return The ID.
         */
        public String getId() {
            return this.id;
        }

        /**
         * @return The url.
         */
        public String getUrl() {
            return this.url;
        }
    }

    /**
     * Board object.
     */
    public static class List {
        private String name;
        private String id;

        /**
         * @return The name.
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return The ID.
         */
        public String getId() {
            return this.id;
        }
    }

    // TODO: The class Card needs more attributes for the rest of the project

    /**
     * Card object.
     */
    public static class Card {
        private String name;
        private String id;
        private String due;
        private String desc;
        private Member member;

        /**
         * @return The name.
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return The ID.
         */
        public String getId() {
            return this.id;
        }

        /**
         * @return The due date.
         */
        public String getDueDate() {
            if (this.due.equals(null)){
                return "N/A";
            }
            return this.due.split("T")[0]; // split by delimiter T
        }

        /**
         * @return The description.
         */
        public String getDesc() {
            return this.desc;
        }

        public Member getMember(){return this.member;}
    }

    // TODO: With this Data class it's possible to access the component card, board and list.
    //  Might be good to reduce code.

    /**
     * Data object.
     */
    public static class Data {
        private String text;

        /**
         * @return The text.
         */
        public String getText() {
            return this.text;
        }
    }

    /**
     * Action object.
     */
    public static class Action {
        private String id;
        private String text;
        private Data data;

        /**
         * @return The id.
         */
        public String getId() {
            return this.id;
        }

        /**
         * @return The text.
         */
        public String getText() {
            return this.text;
        }

        /**
         * @return The Data.
         */
        public Data getData() {
            return this.data;
        }
    }

    /**
     * Member object.
     */
    public static class Member {
        private String username;
        private String id;
        // DRAFT: Hours defined for testing
        private int estimatedHours;
        private int onGoingHours;
        private int concludedHours;

        /**
         * @return The name.
         */
        public String getName() {
            return this.username;
        }

        /**
         * @return The ID.
         */
        public String getId() {
            return this.id;
        }

        /**
         * @return The estimated hours.
         */
        public int getEstimatedHours() {
            return 2;
        }

        /**
         * @return The on going hours.
         */
        public int getOnGoingHours() {
            return 4;
        }

        /**
         * @return The concluded hours.
         */
        public int getConcludedHours() {
            return 3;
        }
    }

    /**
     * @return a list of all boards owned by the user.
     * @throws IOException If the request fails.
     */
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

    /**
     * @param component   component that we want to access ("list, card, board, etc").
     * @param componentId id of the component that we want to access.
     * @param url         url of the component (board url, list url, etc).
     * @return the http response.
     * @throws IOException If the request fails.
     */
    // Function for HTTP request for components
    private Response HTTPRequest(String component, String componentId, String url) throws IOException {
        //HTTP request to access the board
        Request request = new Request.Builder()
                .header("Accept", "application/json")
                .url(url + componentId + "/" + component + "?key=" + apiKey + "&token=" + apiToken).build();

        return this.httpClient.newCall(request).execute();
    }

    /**
     * @param boardId board id.
     * @return the board identified by the board id.
     * @throws IOException If the request fails.
     */
    // Function to return the Board that the user specified at login
    public Board getBoard(String boardId) throws IOException {
        //HTTP request to access the board
        Response response = HTTPRequest("", boardId, boardURL);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Board
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        return mapper.readValue(response.body().string(), Board.class);
    }

    /**
     * @param boardId board id.
     * @return all lists in the board identified by the board id.
     * @throws IOException If the request fails.
     */
    // Function to return all the lists in the board
    public List[] getBoardLists(String boardId) throws IOException {
        //HTTP request to access the board
        Response response = HTTPRequest("lists", boardId, boardURL);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Board
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        // map http response to the class Board
        return mapper.readValue(response.body().string(), List[].class);
    }

    /**
     * @param listName name of the list.
     * @param boardId  board id.
     * @return the list in the board identified by the board id.
     */
    // Function to return a specific list in the board
    public List getList(String listName, String boardId) throws IOException {
        var lists = this.getBoardLists(boardId);
        for (List list : lists) {
            if (list.getName().equals(listName)) {
                return list;
            }
        }
        return null;
    }

    /**
     * @param boardId id of the board.
     * @return all cards in the board identified by the board id.
     * @throws IOException If the request fails.
     */
    // Function to return all the lists in the board
    public Card[] getBoardCards(String boardId) throws IOException {
        //HTTP request to access the board
        Response response = HTTPRequest("cards", boardId, boardURL);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Board
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        // map http response to the class Board
        return mapper.readValue(response.body().string(), Card[].class);
    }

    /**
     * @param listId id of the list.
     * @return all cards in the list identified by the list id.
     * @throws IOException If the request fails.
     */
    // Function to return all the cards in a specific list
    public Card[] getListCards(String listId) throws IOException {
        //HTTP request to access the board
        Response response = HTTPRequest("cards", listId, this.listURL);

        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Board
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        // map http response to the class Board
        return mapper.readValue(response.body().string(), Card[].class);
    }

    /**
     * @param cardId id of the card.
     * @return all actions in the card identified by the card id.
     * @throws IOException If the request fails.
     */
    // Function to get actions from a specific card
    public Action[] getActionsInCard(String cardId) throws IOException {
        //HTTP request to access the board
        Response response = HTTPRequest("actions", cardId, cardURL);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Board
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        // map http response to the class Board
        return mapper.readValue(response.body().string(), Action[].class);
    }

    public Member[] getMemberOfCard(String cardId) throws  IOException {
        //HTTP request to access the board
        Response response = HTTPRequest("members", cardId, cardURL);
        // Build ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // map http response to the class Board
        // https://stackoverflow.com/a/26371693
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        // map http response to the class Board
        return mapper.readValue(response.body().string(), Member[].class);
    }

    /**
     * @param sprintNumber number of the sprint.
     * @param boardId id of the board.
     * @return an array with the start date and the end date of the specific sprint.
     * @throws IOException If the request fails.
     */
    // Function to return the start and end dates of a specific sprint
    public String[] getSprintDates(String boardId, int sprintNumber) throws IOException {
        // flag to see if we've found the start date
        boolean startDateFound = false;
        // initialize list of dates
        String[] dates = new String[2];
        String listName = "Ceremonies - Sprint " + sprintNumber;

        // get the list of all ceremonies
        var list = this.getList("Sprint Ceremonies", boardId);
        var cards = this.getListCards(list.getId());

        // Iterate over all cards in the list
        for (Card c : cards) {
            // search for due date of Sprint Planning that is equal to Sprint start date
            if (c.name.equals("Sprint Planning - Sprint " + sprintNumber)) {
                dates[0] = c.getDueDate();
                startDateFound = true;
            }
            // search for due date of Sprint Retrospective that is equal to Sprint end date
            else if (c.name.equals("Sprint Retrospective - Sprint " + sprintNumber)) {
                dates[1] = c.getDueDate();
                if (startDateFound) break; // if start date is found, we can break the for loop
            }
        }
        // Returns dates list
        // dates[0] -> Sprint start date
        // dates[1] -> Sprint end date
        return dates;
    }

    /**
     * @param boardId      id of the board.
     * @param sprintType   ceremony of the sprint.
     * @param sprintNumber number of the sprint.
     * @return a String with the description of the Sprint Ceremony in the specific sprint.
     * @throws IOException If the request fails.
     * @author Miguel Romana.
     */
    // Function to get the description of a Sprint Ceremony of a specific sprint
    public String getCeremonyDescription(String boardId, String sprintType, int sprintNumber) throws IOException {

        String listName = "Ceremonies - Sprint " + sprintNumber;

        // get the list of all ceremonies
        var list = this.getList(listName, boardId);
        var cards = this.getListCards(list.getId());

        // Iterate over all cards in the list
        for (Card c : cards)
            // get the Sprint sprintType's description
            if (c.name.equals("Sprint " + sprintType + " - Sprint " + sprintNumber))
                return c.getDesc();
        return ""; // If the description doesn't exist returns a null String
    }

    /**
     * @param boardId      id of the board.
     * @param sprintNumber number of the sprint.
     * @return an ArrayList of all the products already done in the specific sprint.
     * @throws IOException If the request fails.
     */
    // Function to get product backlog items already done
    public ArrayList<String> getDoneProductBacklog(String boardId, int sprintNumber) throws IOException {
        var doneItems = new ArrayList<String>();

        String listName = "Done - Sprint " + sprintNumber;

        // get specific list
        var list = this.getList(listName, boardId);
        // get all cards from the list
        var cards = this.getListCards(list.getId());
        for (Card card : cards) {
            doneItems.add(card.name);
        }
        return doneItems;
    }

    /**
     * @param boardId id of the board.
     * @return the total number of ceremonies that have been done.
     * @throws IOException If the request fails.
     */
    // function to get the total number of ceremonies
    public int getTotalNumberOfCeremonies(String boardId) throws IOException {
        int numberOfCeremonies = 0;
        var lists = this.getBoardLists(boardId);
        for (List ceremoniesList : lists) {
            if (ceremoniesList.getName().startsWith("Ceremonies")) {
                var ceremoniesListCards = this.getListCards(ceremoniesList.id);
                numberOfCeremonies += ceremoniesListCards.length;
            }
        }
        return numberOfCeremonies;
    }

    /**
     * @param boardId id of the board.
     * @param sprintNumber number of the sprint.
     * @return the total number of ceremonies that have been done.
     * @throws IOException If the request fails.
     */
    // function to get the total number of ceremonies in a specific sprint
    public int getTotalNumberOfCeremoniesPerSprint(String boardId, int sprintNumber) throws IOException {
        var lists = this.getBoardLists(boardId);
        for (List ceremoniesList : lists) {
            if (ceremoniesList.getName().equals("Ceremonies - Sprint " + sprintNumber)) {
                var ceremoniesListCards = this.getListCards(ceremoniesList.id);
                return ceremoniesListCards.length;
            }
        }
        return 0;
    }

    /**
     * @param boardId id of the board.
     * @param startsWith id of the board.
     * @return an array of all the lists when the name starts with a specific string.
     * @throws IOException If the request fails.
     */
    public ArrayList<List> getListThatStartsWith(String boardId, String startsWith) throws IOException {
        var allLists = this.getBoardLists(boardId);
        var listsThatStartWith = new ArrayList<List>();
        for (List list : allLists) {
            if (list.getName().startsWith(startsWith)) {
                listsThatStartWith.add(list);
            }
        }
        return listsThatStartWith;
    }

    /**
     * @param actionsInCard all actions in the card.
     * @return the total hours spent by the team in the ceremony.
     * @throws IOException If the request fails.
     */
    public double calculateTotalHoursPerCard(Action[] actionsInCard) {
        double totalHours = 0;
        for (Action action: actionsInCard) {
            if (action.getData().getText() != null && action.getData().getText().startsWith("plus! @global")) {
                String spentEstimatedTime = action.getData().getText().split(" ")[2];
                double hoursSpent = Double.parseDouble(String.valueOf(spentEstimatedTime.split("/")[0]));
                totalHours += hoursSpent;
            }
        }
        return totalHours;
    }

    /**
     * @param boardId id of the board.
     * @return the total hours spent by the team in ceremonies.
     * @throws IOException If the request fails.
     */
    // TODO: Remove unnecessary comments in trello
    public double getTotalHoursCeremony(String boardId) throws IOException {
        double totalOfHours = 0;
        ArrayList<List> listOfCeremonies = this.getListThatStartsWith(boardId, "Ceremonies");
        for (List list: listOfCeremonies){
            for (Card card: this.getListCards(list.getId())) {
                totalOfHours += calculateTotalHoursPerCard(this.getActionsInCard(card.getId()));
            }
        }
        return totalOfHours;
    }

    /**
     * @param descriptionInCard all actions in the card.
     * @return the total hours spent by the team in the ceremony.
     * @throws IOException If the request fails.
     */
    // ALTERNATIVE FUNCTION TO calculateTotalHoursPerCard!!
    public double calculateTotalHoursPerCardDescriptionBased(String descriptionInCard) {
        double totalHours = 0.0;

        String hours = descriptionInCard.split("@global")[1].split("/")[0];
        totalHours += Double.parseDouble(hours);

        return totalHours;
    }

    /**
     * @param boardId id of the board.
     * @return the total hours spent by the team in ceremonies.
     * @throws IOException If the request fails.
     */
    // ALTERNATIVE FUNCTION TO getTotalHoursCeremony!!
    public double getTotalHoursCeremonyDescriptionBased(String boardId) throws IOException {
        double totalOfHours = 0;
        ArrayList<List> listOfCeremonies = this.getListThatStartsWith(boardId, "Ceremonies");
        for (List list: listOfCeremonies){
            for (Card card: this.getListCards(list.getId())) {
                totalOfHours += calculateTotalHoursPerCardDescriptionBased(card.getDesc());
            }
        }
        return totalOfHours;
    }

    /**
     * @param boardId id of the board.
     * @return the total hours spent by user.
     * @throws IOException If the request fails.
     */
    public double[] getTotalHoursByUser(String boardId) throws IOException {
        double[] hourSpent = new double[3];
        double totalOfHours = 0;
        ArrayList<List> listOfCeremonies = this.getListThatStartsWith(boardId, "Done");
        for (List list: listOfCeremonies){
            for (Card card: this.getListCards(list.getId())) {
                // iterate over all members of the card
                for (Member member: this.getMemberOfCard(card.getId())) {
                    if (card.getDesc().contains("@" + member.getName())) {
                        // TODO: Can't be solved this way. Needs improvement
                        hourSpent[0] += calculateTotalHoursPerUser(card.getDesc(), member.getName());
                        //totalOfHours += calculateTotalHoursPerUser(card.getDesc(), name);
                    }
                }
            }
        }
        return hourSpent;
    }

    /**
     * @param descriptionInCard all actions in the card.
     * @return the total hours spent by the team in the ceremony.
     * @throws IOException If the request fails.
     */
    // TODO: Needs to be modified to calculate all spent hours by yser.
    public double calculateTotalHoursPerUser(String descriptionInCard, String userName) {
        double totalHours = 0.0;

        String hours = descriptionInCard.split("@" + userName)[1].split("/")[0];
        totalHours += Double.parseDouble(hours);

        return totalHours;
    }

    /**
     * @param boardId id of the board.
     * @param sprintNumber number of the sprint.
     * @param memberName name of the member.
     * @return an array with the hours of a specific member in a specific sprint.
     * @throws IOException If the request fails.
     */
    /*
    // Function to return the hours (estimated, concluded and ongoing) of a specific member in a specific sprint
    public int[] getMemberHours(String boardId, int sprintNumber, String memberName) throws IOException {
        // hours[0] - Estimated hours
        // hours[1] - Ongoing hours
        // hours[2] - Concluded hours
        int[] hours = new int[3];

        // TODO: Get the members of a board (including global)
        //var members = this.getMembers(boardId);
        //Member[] members = {"Alexandre", "Dudu", "Rodrigo Guerreyro ou Manel", "Miguel"};

        // Iterate over all members
        for (Member m : members) {
            if (m.name.equals(memberName)) {
                hours[0] = m.getEstimatedHours();
                hours[1] = m.getOnGoingHours();
                hours[2] = m.getConcludedHours();
                break;
            }
        }

        // Returns hours list
        return hours;
    }
    */


}