package commands;

import controller.KitchenCTRL;
import model.Ingredient;
import model.catalogue.Recipe;
import model.catalogue.Catalogue;
import model.catalogue.Inventory;
import model.catalogue.RecipeBook;

import static controller.KitchenCTRL.requireActiveRecipe;
import static controller.ScreenState.RECIPEBOOK;

/**
 * Represents a command to delete an item (ingredient or recipe) from the appropriate catalogue
 * based on the current {@code ScreenState}.
 *
 * <p>This command supports:
 * <ul>
 *     <li>Deleting {@code Ingredient} from {@code Inventory}</li>
 *     <li>Deleting a {@code Recipe} from {@code RecipeBook}</li>
 *     <li>Deleting {@code Ingredient} from a specific {@code Recipe}</li>
 * </ul>
 */
public class DeleteCommand extends Command {
    private final String name;
    private final int quantity;

    /**
     * Constructs a {@code DeleteCommand} for deleting an ingredient or recipe.
     *
     * @param name     The name of the item to delete.
     * @param quantity The quantity of the ingredient to delete (ignored for recipes).
     * @throws AssertionError if {@code name} is null/empty, or {@code quantity} is non-positive where required.
     */
    public DeleteCommand(String name, int quantity) {
        assert name != null && !name.trim().isEmpty() : "Name must not be null or empty";
        assert KitchenCTRL.getCurrentScreen() == RECIPEBOOK || quantity > 0 : "Quantity must be greater than zero";

        this.name = name;
        this.quantity = quantity;
    }

    /**
     * Constructs a {@code DeleteCommand} with the specified item name.
     * <p>
     * This constructor is used when deleting a recipe, where quantity is not applicable.
     * The quantity is set to 0 by default.
     *
     * @param name The name of the recipe to delete.
     * @throws AssertionError if {@code name} is null or empty.
     */
    public DeleteCommand(String name) {
        assert name != null && !name.trim().isEmpty() : "Name must not be null or empty";

        this.name = name;
        this.quantity = 0; // not used for recipe deletion
    }

    /**
     * Executes the delete operation based on the screen context.
     *
     * @param catalogue The catalogue from which to delete the item.
     * @return A {@code CommandResult} describing the outcome.
     */
    @Override
    public CommandResult execute(Catalogue<?> catalogue) {
        assert catalogue != null : "Catalogue must not be null";

        try {
            return switch (KitchenCTRL.getCurrentScreen()) {
            case INVENTORY -> {
                if (catalogue instanceof Inventory inventory) {
                    Ingredient ingredient = new Ingredient(name, quantity);
                    yield inventory.deleteItem(ingredient);
                }
                yield new CommandResult("Error: Invalid catalogue for inventory operation.", null);
            }
            case RECIPEBOOK -> {
                if (catalogue instanceof RecipeBook recipeBook) {
                    Recipe recipe = new Recipe(name);
                    yield recipeBook.deleteItem(recipe);
                }
                yield new CommandResult("Error: Invalid catalogue for recipe book operation.", null);
            }
            case RECIPE -> {
                requireActiveRecipe();
                if (catalogue instanceof Recipe recipe) {
                    Ingredient ingredient = new Ingredient(name, quantity);
                    yield recipe.deleteItem(ingredient);
                }
                yield new CommandResult("Error: Invalid catalogue for recipe operation.", null);
            }
            default -> new CommandResult("Error: Unsupported screen state for DeleteCommand.", null);
            };
        } catch (ClassCastException e) {
            return new CommandResult("Error: Catalogue type mismatch: " + e.getMessage(), null);
        } catch (IllegalArgumentException e) {
            return new CommandResult("Invalid argument: " + e.getMessage(), null);
        } catch (Exception e) {
            return new CommandResult("Error occurred: " + e.getMessage(), null);
        }
    }
}

