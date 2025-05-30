package kitchenctrl;

import model.Ingredient;
import model.catalogue.Recipe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ui.inputparser.ConflictHelper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConflictHelperTest {

    //test
    private final InputStream originalIn = System.in;

    @AfterEach
    void restoreSystemIn() {
        System.setIn(originalIn);
        ConflictHelper.setScanner(new Scanner(System.in)); // Restore default after test
    }

    @Test
    public void testGetUserChoiceForAddIngredient_validInput() {
        String simulatedInput = "1\n";
        ConflictHelper.setScanner(new Scanner(new ByteArrayInputStream(simulatedInput.getBytes())));

        ArrayList<Ingredient> similar = new ArrayList<>();
        similar.add(new Ingredient("Sugar", 5));
        Ingredient newIngredient = new Ingredient("Sugar", 3);

        int result = ConflictHelper.getUserChoiceForAddIngredient(similar, newIngredient);

        assertEquals(1, result);
    }

    @Test
    public void testGetUserChoiceForDeleteIngredient_validInput() {
        String simulatedInput = "1\n";
        ConflictHelper.setScanner(new Scanner(new ByteArrayInputStream(simulatedInput.getBytes())));

        ArrayList<Ingredient> similar = new ArrayList<>();
        similar.add(new Ingredient("Flour", 2));
        Ingredient toDelete = new Ingredient("Flour", 1);

        int result = ConflictHelper.getUserChoiceForDeleteIngredient(similar, toDelete);

        assertEquals(1, result);
    }

    @Test
    public void testGetUserChoiceForAddRecipe_cancel() {
        String simulatedInput = "-1\n";
        ConflictHelper.setScanner(new Scanner(new ByteArrayInputStream(simulatedInput.getBytes())));

        ArrayList<Recipe> similar = new ArrayList<>();
        similar.add(new Recipe("Pasta"));
        Recipe newRecipe = new Recipe("Pasta");

        int result = ConflictHelper.getUserChoiceForAddRecipe(similar, newRecipe);

        assertEquals(-1, result);
    }

    @Test
    public void testGetUserChoiceForDeleteRecipe_cancel() {
        String simulatedInput = "-1\n";
        ConflictHelper.setScanner(new Scanner(new ByteArrayInputStream(simulatedInput.getBytes())));

        ArrayList<Recipe> similar = new ArrayList<>();
        similar.add(new Recipe("Soup"));
        Recipe toDelete = new Recipe("Soup");

        int result = ConflictHelper.getUserChoiceForDeleteRecipe(similar, toDelete);

        assertEquals(-1, result);
    }
}
