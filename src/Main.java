import com.sun.xml.internal.ws.policy.*;
import jdk.management.resource.internal.inst.*;
import org.dreambot.api.*;
import org.dreambot.api.input.*;
import org.dreambot.api.methods.*;
import org.dreambot.api.methods.container.impl.*;
import org.dreambot.api.methods.container.impl.bank.*;
import org.dreambot.api.methods.dialogues.*;
import org.dreambot.api.methods.input.*;
import org.dreambot.api.methods.input.mouse.*;
import org.dreambot.api.methods.interactive.*;
import org.dreambot.api.methods.map.*;
import org.dreambot.api.methods.skills.*;
import org.dreambot.api.methods.tabs.*;
import org.dreambot.api.methods.walking.impl.*;
import org.dreambot.api.script.*;
import org.dreambot.api.utilities.impl.*;
import org.dreambot.api.wrappers.interactive.*;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.core.*;

import java.awt.*;
import java.awt.event.*;
import java.nio.channels.*;
import java.util.*;

import static org.dreambot.api.methods.Calculations.generateRandomString;
import static org.dreambot.api.methods.Calculations.random;
import static org.dreambot.api.methods.container.impl.bank.Bank.*;
import static org.dreambot.api.methods.skills.SkillTracker.getGainedExperience;

@ScriptManifest(author = "Sem", name = "CraftingScript", version = 1.0, description = "Crafting amulets (U)", category = Category.MONEYMAKING)
public class Main extends AbstractScript {

    public void onStart() {
        SkillTracker.start(Skill.THIEVING); //Start tracking Thieving xp
        SkillTracker.start(Skill.FISHING); //Start tracking Fishing xp
        SkillTracker.start(Skill.CRAFTING); //Start tracking Crafting xp

        log("Script opstarten.");
        log("Begin van het script.");
    }

    public void onExit() {
        log("Einde van het script.");
        log("Script afsluiten.");
    }

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

    // Crafting
    Area EdgevilleBank = new Area(3094, 3496, 3098, 3494);
    Area GrandExchange = new Area(3161, 3493, 3168, 3486);

    // Fishing
    Area BarbarianVillageFishingArea = new Area(3104, 3434, 3113, 3431); //
    Area BarbarianVillageFishingNorth = new Area(3109, 3434, 3109, 3432);
    Area BarbarianVillageFishingSouth = new Area(3103, 3425, 3103, 3424);

    // Thieving Area/NPC/Gameobjects
    Area DraynorSquare = new Area(3083, 3253, 3077, 3248); // Coördinates of Draynor Square
    Area DraynorSquareBig = new Area(3073, 3256, 3087, 3243);

    NPC MasterFarmer = NPCs.closest("Master Farmer");

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
        Instance instance = Client.getInstance();
        Canvas canvas = Instance.getCanvas();
        Inventory inventory = Inventory.get();

        instance.setKeyboardInputEnabled(true);
        Mouse.getMouseSettings();
        MouseSettings.setSpeed(random(2, 4));
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

//    public void setLocalPlayer(Character localPlayer) {
//        this.getLocalPlayer = localPlayer;
//    }

    private enum State {
        WALKSHORT, CRAFT, BANK, FISH, DROP, STEAL, BANKDRAYNOR
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
}