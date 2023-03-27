import org.dreambot.api.*;
import org.dreambot.api.input.*;
import org.dreambot.api.methods.container.impl.*;
import org.dreambot.api.methods.input.mouse.*;
import org.dreambot.api.methods.interactive.*;
import org.dreambot.api.methods.map.*;
import org.dreambot.api.methods.skills.*;
import org.dreambot.api.script.*;
import org.dreambot.api.utilities.*;
import org.dreambot.api.wrappers.interactive.*;
import org.dreambot.core.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import static org.dreambot.api.methods.Calculations.*;
import static org.dreambot.api.methods.container.impl.bank.Bank.*;

@ScriptManifest(author = "Sem", name = "Simple Fishing", version = 1.0, description = "Fishing at Barbarian Village", category = Category.MONEYMAKING)
public class Main extends AbstractScript {

    //    Fishing Supplies
    public int Feather = 314;
    public int FlyFishingRod = 309;
    public int RawSalmon = 331;
    public int RawTrout = 335;
    public int ClueBottleBeginner = 23129;

    private boolean isFishing() {
        //returns if we are in an attacking animation
        return Players.getLocal().isAnimating();
    }

    // Fishing coordinates
    Area BarbarianVillageFishingArea = new Area(3104, 3434, 3113, 3431); //
    Area BarbarianVillageFishingNorth = new Area(3109, 3434, 3109, 3432);
    Area BarbarianVillageFishingSouth = new Area(3103, 3425, 3103, 3424);


    public void dropOnlyThese(Integer[] droppables, int[] dropOrder) {
        // the grid construction should be in your constructor.

        // this is for the sake of example
        Rectangle[] invRects = new Rectangle[28];
        for (int r = 0; r < invRects.length; r++) {
            int x = 563 + 42 * (r % 4);
            int y = 213 + 36 * (r / 4);
            invRects[r] = new Rectangle(x, y, 42, 36);
            generateRandomString();
        }

        // Code to drop items
        Instance instance = Client.getInstance();           // ...
        Canvas canvas = Instance.getCanvas();               // ...
        Inventory inventory = (Inventory) Inventory.all();  // Gets all items in inventory

        instance.setKeyboardInputEnabled(true);             // Allows Keyboard input
        Mouse.getMouseSettings();
        MouseSettings.setSpeed(random(2, 4));               // Sets mouse speed
        canvas.dispatchEvent(new KeyEvent(canvas, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_SHIFT, KeyEvent.CHAR_UNDEFINED));
        for (int r = 0; r < dropOrder.length; r++) {
            if (Inventory.getItemInSlot(dropOrder[r]) != null && Arrays.asList(droppables).contains(Inventory.getItemInSlot(dropOrder[r]).getID())) {
                Mouse.click(invRects[dropOrder[r]]);
            }
            try {
                Thread.sleep(250); // randomize & speed this up
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        canvas.dispatchEvent(new KeyEvent(canvas, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_SHIFT, KeyEvent.CHAR_UNDEFINED));
        Mouse.getMouseSettings().resetSpeed();
        instance.setKeyboardInputEnabled(false);
    }

    private enum State {
        FISH, DROP
    }

    private State getState() {
        if (Inventory.isFull()) {
            log("Disposal of Fish");
            return State.DROP;
        }
        if (Inventory.contains(Feather, FlyFishingRod) &&
                BarbarianVillageFishingArea.contains(Players.getLocal()) && !Players.getLocal().isAnimating()) {
            log("Going Fishing");
            return State.FISH;
        }
        return null;
    }

    public void onStart() {
        SkillTracker.start(Skill.FISHING); //Start tracking Fishing xp

        log("Script opstarten.");
        log("Begin van het script.");
    }

    @Override
    public int onLoop() {


        log("Inventory contains Fly Fishing Rod and Feathers: " + Inventory.contains(Feather, FlyFishingRod));
        log("Inventory contains Salmon and/or Trout: " + Inventory.contains(RawSalmon, RawTrout));
        log("Player's location is at the Barbarian Village fishing spot: " + BarbarianVillageFishingArea.contains(Players.getLocal()));

        switch (getState()) {
            case FISH:
                log("About to reel in some fish");
                NPC FishingSpot = NPCs.closest(f -> f != null && f.getName().contains("Fishing spot") && BarbarianVillageFishingArea.contains(f));
                log(FishingSpot);

                if (FishingSpot != null) {
                    if (FishingSpot.interact()) {
                        Sleep.sleepUntil(() -> !FishingSpot.exists(), random(4000, 6000));
                    }
                }
                break;

//                if (!isFishing()) {
//                    if (FishingSpot != null) {
//                        sleep(random(1000, 2000));
//                        if (FishingSpot.interactForceLeft(null)) {
//                            Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 2000);
//                        } else {
//                            sleep(3000);
//                        }
//                    }
//                }
//                break;

            case DROP:
                log("Inventory is Full. Dropping fish now!");
                int minimum = 1;
                int maximum = 250;

                int RandomNumber = (int) Math.floor(Math.random() * (maximum - minimum + 1) + minimum);
                log(RandomNumber);

                Integer[] dropables = {RawSalmon, RawTrout, ClueBottleBeginner}; // Trout, Salmon and BeginnerClue

                if (Inventory.isFull()) {
                    if (RandomNumber < 50) {
                        placeHoldersEnabled();

                        log("Dropping items *Drunken Walk* Style");

                        int[] dropOrder = {0, 1, 2, 3, 7, 6, 5, 4, 8, 9, 10, 11, 15, 14, 13, 12, 16, 17, 18, 19, 23, 22, 21, 20, 24, 25, 26, 27}; // DrunkenWalk Pattern
                        dropOnlyThese(dropables, dropOrder);
                        if (!Inventory.isEmpty()) {
                            Inventory.dropAll(RawSalmon, RawTrout);
                        }
                        sleep(random(750, 1500));
                    }

                    if (RandomNumber >= 50 && RandomNumber <= 100) {
                        log("Dropping items *ZigZag* Style");
//                        Integer[] dropables = {RawSalmon, RawTrout, ClueBottleBeginner}; // Trout, Salmon and BeginnerClue
                        int[] dropOrder = {0, 1, 4, 5, 8, 9, 12, 13, 16, 17, 20, 21, 24, 25, 26, 27, 22, 23, 18, 19, 14, 15, 10, 11, 6, 7, 2, 3}; // ZigZag Pattern
                        dropOnlyThese(dropables, dropOrder);
                        if (!Inventory.isEmpty()) {
                            Inventory.dropAll(dropables);
//                            Inventory.dropAll(RawSalmon, RawTrout);
                        }
                        sleep(random(750, 1500));
                    }

                    if (RandomNumber > 100 && RandomNumber <= 150) {
                        log("");
                        log("Dropping items *Small Drunken Walk* Style");
//                        Integer[] dropables = {RawSalmon, RawTrout, ClueBottleBeginner}; // Trout, Salmon and BeginnerClue
                        int[] dropOrder = {0, 1, 5, 4, 8, 9, 13, 12, 16, 17, 21, 20, 25, 25, 26, 27, 23, 22, 18, 19, 15, 14, 10, 11, 7, 6, 2, 3}; // SmallDrunkenWalk Pattern
                        dropOnlyThese(dropables, dropOrder);
                        if (!Inventory.isEmpty()) {
                            Inventory.dropAll(dropables);
//                            Inventory.dropAll(RawSalmon, RawTrout, ClueBottleBeginner);
                        }
                        sleep(random(750, 1500));
                    }

                    if (RandomNumber > 150 && RandomNumber <= 200) {
                        log("Dropping items *Up to Down - Down to Up* Style");
//                        Integer[] dropables = {RawSalmon, RawTrout, ClueBottleBeginner}; // Trout, Salmon and BeginnerClue
                        int[] dropOrder = {0, 4, 8, 12, 16, 17, 20, 24, 25, 21, 17, 13, 9, 5, 1, 2, 6, 10, 14, 18, 22, 26, 27, 23, 19, 15, 11, 7, 3}; //up to down - down to up
                        dropOnlyThese(dropables, dropOrder);
                        if (!Inventory.isEmpty()) {
                            Inventory.dropAll(dropables);
//                            Inventory.dropAll(RawSalmon, RawTrout, ClueBottleBeginner);
                        }
                    }

                    if (RandomNumber > 200 && RandomNumber <= 250) {
                        log("Dropping items Up to Down - Up to Down Style");
//                        Integer[] dropables = {RawSalmon, RawTrout, ClueBottleBeginner}; // Trout, Salmon and BeginnerClue
                        int[] dropOrder = {0, 4, 8, 12, 16, 20, 24, 1, 5, 9, 13, 17, 21, 25, 2, 6, 10, 14, 18, 22, 26, 3, 7, 11, 15, 19, 23, 27};
                        dropOnlyThese(dropables, dropOrder);
                        if (!Inventory.isEmpty()) {
                            Inventory.dropAll(dropables);
//                            Inventory.dropAll(RawSalmon, RawTrout, ClueBottleBeginner);
                        }
                    }
                }
                break;


        }
        return 0;
    }


    public void onExit() {
        log("Einde van het script.");
        log("Script afsluiten.");
    }
}

