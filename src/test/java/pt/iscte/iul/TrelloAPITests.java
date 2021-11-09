package pt.iscte.iul;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

public class TrelloAPITests {
    private TrelloAPI api;

    @BeforeEach
    public void once() throws IOException {
        var lines = FileUtils.readLines(new File("tokens.txt"), Charset.defaultCharset());

        this.api = new TrelloAPI(
                "ES-LETI-1Sem-2021-Grupo10",
                lines.get(0),
                lines.get(1));
    }

    @Test
    public void boardAttributes() throws IOException {
        String boardId = "614df1d076293f6b763c1c9c";
        Assertions.assertEquals("ES-LETI-1Sem-2021-Grupo10", this.api.getBoard(boardId).getName());
        Assertions.assertEquals("614df1d076293f6b763c1c9c", this.api.getBoard(boardId).getId());
        Assertions.assertEquals("https://trello.com/b/lzp7YmaF/es-leti-1sem-2021-grupo10",
        this.api.getBoard(boardId).getUrl());
    }


    @Test
    public void numberOfBoards() throws IOException {
        Assertions.assertEquals(2, this.api.getBoards().length);
    }

    @Test
    public void SprintStartDate() throws IOException {
        String[] dates = {"2021-10-09", "2021-10-30"};
        Assertions.assertArrayEquals(dates, this.api.getSprintDates(1, "614df1d076293f6b763c1c9c"));
    }

    @Test
    public void numberOfLists() throws IOException {
        String boardId = "614df1d076293f6b763c1c9c";
        Assertions.assertEquals(6, this.api.getBoardLists(boardId).length);
    }

    @Test
    public void listAttributes() throws IOException {
        String boardId = "614df1d076293f6b763c1c9c";
        TrelloAPI.List list = this.api.getBoardLists(boardId)[0];
        Assertions.assertEquals("Product Backlog", list.getName());
        Assertions.assertEquals("614df1f97143f252bad74c5b", list.getId());
    }

    @Test
    public void cardAttributes() throws IOException {
        String boardId = "614df1d076293f6b763c1c9c";
        String Id = "6161b8f50e32ff864a928bd6";
        String cardName = "";
        String cardId = "";
        String cardDue = "";
        TrelloAPI.Card[] card = this.api.getBoardCards(boardId);
        for (TrelloAPI.Card c: card){
            if (c.getId().equals(Id)){
                cardName = c.getName();
                cardId = c.getId();
                cardDue = c.getDueDate();
                break;
            }
        }
        Assertions.assertEquals("Sprint Planning - Sprint 1", cardName);
        Assertions.assertEquals("6161b8f50e32ff864a928bd6", cardId);
        Assertions.assertEquals("2021-10-09", cardDue);
    }

    @Test
    public void doneProductBacklog() throws IOException {
        String boardId = "614df1d076293f6b763c1c9c";
        ArrayList<String> backlogProducts = new ArrayList<String>(
                Arrays.asList("APIs - Sprint 1", "Home UI - Sprint 1", "12. Descrição do projeto - Sprint 1",
                        "1. Identificação do Projeto - Sprint 1", "3. Início do projeto - Sprint 1",
                        "Organização do Trello - Sprint 1", "4. Datas dos Sprints - Sprint 1",
                        "2. Elementos da equipa - Sprint 1")
        );
        var doneProducts = this.api.getDoneProductBacklog(boardId, 1);
        Assertions.assertEquals(backlogProducts, doneProducts);
    }



}
