package mtg;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jaroslaw Pawlak
 */
public class Resources {

    private Resources() {}
    
    private static String[] wieldingSteelCards = {
        "Angel's Feather.jpg",
        "Archangel of Strife.jpg",
        "Argentum Armor.jpg",
        "Arrest.jpg",
        "Baneslayer Angel.jpg",
        "Brave the Elements.jpg",
        "Captain of the Watch.jpg",
        "Congregate.jpg",
        "Conqueror's Pledge.jpg",
        "Elite Vanguard.jpg",
        "Gideon's Avenger.jpg",
        "Gideon's Lawkeeper.jpg",
        "Glory Seeker.jpg",
        "Harmless Assault.jpg",
        "Infiltration Lens.jpg",
        "Kitesail Apprentice.jpg",
        "Kitesail.jpg",
        "Kor Duelist.jpg",
        "Kor Hookmaster.jpg",
        "Kor Outfitter.jpg",
        "Pennon Blade.jpg",
        "Plains.jpg",
        "Puresteel Paladin.jpg",
        "Revoke Existence.jpg",
        "Serra Angel.jpg",
        "Stoneforge Mystic.jpg",
        "Strider Harness.jpg",
        "Sunspear Shikari.jpg",
        "Sword of War and Peace.jpg",
        "Trusty Machete.jpg",
    };
    private static String[] realmOfIllusionCards = {};
    private static String[] strengthOfStoneCards = {};
    private static String[] guardiansOfTheWoodCards = {
        "Elven Riders.jpg",
        "Elvish Eulogist.jpg",
        "Elvish Lyrist.jpg",
        "Elvish Promenade.jpg",
        "Elvish Visionary.jpg",
        "Epic Proportions.jpg",
        "Essence Drain.jpg",
        "Eyeblight's Ending.jpg",
        "Ezuri, Renegade Leader.jpg",
        "Ezuri's Archers.jpg",
        "Forest.jpg",
        "Heedless One.jpg",
        "Imperious Perfect.jpg",
        "Jagged-Scar Archers.jpg",
        "Joraga Warcaller.jpg",
        "Lys Alana Huntmaster.jpg",
        "Maelstrom Pulse.jpg",
        "Might of the Masses.jpg",
        "Nath of the Gilt-Leaf.jpg",
        "Nissa's Chosen.jpg",
        "Norwood Ranger.jpg",
        "Plummet.jpg",
        "Swamp.jpg",
        "Sylvan Ranger.jpg",
        "Titania's Chosen.jpg",
        "Viridian Emissary.jpg",
        "Viridian Shaman.jpg",
        "Wildheart Invoker.jpg",
    };
    private static String[] ancientDepthsCards = {
        "Compulsive Research.jpg",
        "Æther Mutation.jpg",
        "Assault Zeppelid.jpg",
        "Coiling Oracle.jpg",
        "Cultivate.jpg",
        "Edric, Spymaster of Trest.jpg",
        "Elvish Piper.jpg",
        "Explore.jpg",
        "Explosive Vegetation.jpg",
        "Forest.jpg",
        "Giant Octopus.jpg",
        "Inkwell Leviathan.jpg",
        "Island.jpg",
        "Isleback Spawn.jpg",
        "Kozilek, Butcher of Truth.jpg",
        "Kraken Hatchling.jpg",
        "Levitation.jpg",
        "Living Destiny.jpg",
        "Lorthos, the Tidemaker.jpg",
        "Mind Control.jpg",
        "New Frontiers.jpg",
        "Ondu Giant.jpg",
        "Polymorph.jpg",
        "Primeval Titan.jpg",
        "Rite of Replication.jpg",
        "Simic Sky Swallower.jpg",
        "Skyshroud Claim.jpg",
        "Tidal Kraken.jpg",
        "Tidings.jpg",
        "Treasure Hunt.jpg",
        "Ulamog, the Infinite Gyre.jpg",
        "Yavimaya's Embrace.jpg",
    };
    private static String[] dragonsRoarCards = {};
    private static String[] blooHungerCards = {
        "Barony Vampire.jpg",
        "Blade of the Bloodchief.jpg",
        "Bloodghast.jpg",
        "Bloodrage Vampire.jpg",
        "Captivating Vampire.jpg",
        "Child of Night.jpg",
        "Corrupt.jpg",
        "Demon's Horn.jpg",
        "Drana, Kalastria Bloodchief.jpg",
        "Duskhunter Bat.jpg",
        "Feast of Blood.jpg",
        "Gatekeeper of Malakir.jpg",
        "Mirri the Cursed.jpg",
        "Quag Vampires.jpg",
        "Repay in Kind.jpg",
        "Ruthless Cullblade.jpg",
        "Sangromancer.jpg",
        "Sengir Vampire.jpg",
        "Skeletal Vampire.jpg",
        "Spread the Sickness.jpg",
        "Stalking Bloodsucker.jpg",
        "Swamp.jpg",
        "Tormented Soul.jpg",
        "Urge to Feed.jpg",
        "Vampire Aristocrat.jpg",
        "Vampire Nighthawk.jpg",
        "Vampire Nocturnus.jpg",
        "Vampire Outcasts.jpg",
        "Vampire's Bite.jpg",
        "Vicious Hunger.jpg",
    };
    private static String[] machinationsCards = {
        "Alpha Myr.jpg",
        "Darksteel Colossus.jpg",
        "Darksteel Plate.jpg",
        "Dead Reckoning.jpg",
        "Dispense Justice.jpg",
        "Etched Champion.jpg",
        "Etherium Sculptor.jpg",
        "Go for the Throat.jpg",
        "Golem's Heart.jpg",
        "Gust-Skimmer.jpg",
        "Hunger of the Nim.jpg",
        "Island.jpg",
        "Magister Sphinx.jpg",
        "Master of Etherium.jpg",
        "Mirrorworks.jpg",
        "Pilgrim's Eye.jpg",
        "Plains.jpg",
        "Psychosis Crawler.jpg",
        "Razorfield Rhino.jpg",
        "Razorfield Thresher.jpg",
        "Sanctum Gargoyle.jpg",
        "Seer's Sundial.jpg",
        "Shape Anew.jpg",
        "Signal Pest.jpg",
        "Sleep.jpg",
        "Snapsail Glider.jpg",
        "Soulquake.jpg",
        "Steel Overseer.jpg",
        "Stoic Rebuttal.jpg",
        "Stone Golem.jpg",
        "Swamp.jpg",
        "Terramorphic Expanse.jpg",
        "Tidehollow Strix.jpg",
        "Undermine.jpg",
        "Venser's Journal.jpg",
        "Wurmcoil Engine.jpg",
    };
    private static String[] unquenchableCards = {};
    private static String[] apexPredatorsCards = {};
    
    static void saveExampleCards() {
        File examples = new File(Main.CARDS, "Example");

        File wieldingSteel = new File(examples, "Wielding Steel");
        for (String e : wieldingSteelCards) {
            save("/resources/cards/" + e, new File(wieldingSteel, e));
        }
        
        File realmOfIllusion = new File(examples, "Realm of Illusion");
        for (String e : realmOfIllusionCards) {
            save("/resources/cards/" + e, new File(realmOfIllusion, e));
        }
        
        File strengthOfStone = new File(examples, "Strength of Stone");
        for (String e : strengthOfStoneCards) {
            save("/resources/cards/" + e, new File(strengthOfStone, e));
        }
        
        File guardiansOfTheWood = new File(examples, "Guardians of the Wood");
        for (String e : guardiansOfTheWoodCards) {
            save("/resources/cards/" + e, new File(guardiansOfTheWood, e));
        }
        
        File ancientDepths = new File(examples, "Ancient Depths");
        for (String e : ancientDepthsCards) {
            save("/resources/cards/" + e, new File(ancientDepths, e));
        }
        
        File dragonsRoar = new File(examples, "Dragon's Roar");
        for (String e : dragonsRoarCards) {
            save("/resources/cards/" + e, new File(dragonsRoar, e));
        }
        
        File bloodHunger = new File(examples, "Blood Hunger");
        for (String e : blooHungerCards) {
            save("/resources/cards/" + e, new File(bloodHunger, e));
        }
        
        File machinations = new File(examples, "Machinations");
        for (String e : machinationsCards) {
            save("/resources/cards/" + e, new File(machinations, e));
        }
        
        File unquenchableFire = new File(examples, "Unquenchable Fire");
        for (String e : unquenchableCards) {
            save("/resources/cards/" + e, new File(unquenchableFire, e));
        }
        
        File apexPredators = new File(examples, "Apex Predators");
        for (String e : apexPredatorsCards) {
            save("/resources/cards/" + e, new File(apexPredators, e));
        }
    }

    static void saveExampleDecks() {
        String[] decks = {
            "Ancient Depths.txt",
            "Blood Hunger.txt",
            "Guardians of the Wood.txt",
            "Machinations.txt",
            "Wielding Steel.txt",
        }; //TODO more decks!

        for (String e : decks) {
            save("/resources/decks/" + e, new File(Main.DECKS, e));
        }
    }

    private static void save(String resource, File file) {
        file.getParentFile().mkdirs();
        try (InputStream is = Main.class.getResourceAsStream(resource);
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(file))) {

            byte[] b = new byte[256];
            int read = -1;
            while ((read = is.read(b)) >= 0) {
                bos.write(b, 0, read);
            }
            bos.close();
            is.close();
        } catch (IOException ex) {
            Debug.p("Resource " + resource + " could not be saved: "
                    + ex, Debug.E);
        }
    }
    
}
