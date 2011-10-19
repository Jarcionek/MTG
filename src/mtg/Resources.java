package mtg;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
        "Guardian Seraph.jpg",
        "Loxodon Gatekeeper.jpg",
        "Sunblast Angel.jpg",
    };
    private static String[] realmOfIllusionCards = {
        "Æther Adept.jpg",
        "Æther Figment.jpg",
        "Air Elemental.jpg",
        "Blind Phantasm.jpg",
        "Cancel.jpg",
        "Concentrate.jpg",
        "Counterspell.jpg",
        "Disorient.jpg",
        "Divination.jpg",
        "Drake Umbra.jpg",
        "Evacuation.jpg",
        "Fleeting Distraction.jpg",
        "Island.jpg",
        "Jace's Ingenuity.jpg",
        "Kraken's Eye.jpg",
        "Krovikan Mist.jpg",
        "Lord of the Unreal.jpg",
        "Mahamoti Djinn.jpg",
        "Mind Control.jpg",
        "Mind Spring.jpg",
        "Phantasmal Bear.jpg",
        "Phantasmal Dragon.jpg",
        "Phantom Beast.jpg",
        "Phantom Warrior.jpg",
        "Prosperity.jpg",
        "Quicksilver Geyser.jpg",
        "Repulse.jpg",
        "Sower of Temptation.jpg",
        "Summoner's Bane.jpg",
        "Time Warp.jpg",
        "Wall of Air.jpg",
        "Cultural Exchange.jpg",
        "Bribery.jpg",
        "Curfew.jpg",
    };
    private static String[] strengthOfStoneCards = {
        "Act of Treason.jpg",
        "Assault Strobe.jpg",
        "Bloodfire Colossus.jpg",
        "Cerebral Eruption.jpg",
        "Claws of Valakut.jpg",
        "Conquering Manticore.jpg",
        "Darksteel Axe.jpg",
        "Earth Elemental.jpg",
        "Earth Servant.jpg",
        "Fault Line.jpg",
        "Flameborn Hellion.jpg",
        "Flowstone Overseer.jpg",
        "Goblin Mountaineer.jpg",
        "Golden Urn.jpg",
        "Grim Lavamancer.jpg",
        "Hero of Oxid Ridge.jpg",
        "Koth's Courier.jpg",
        "Lavaborn Muse.jpg",
        "Magma Phoenix.jpg",
        "Molten Ravager.jpg",
        "Mountain.jpg",
        "Oxidda Scrapmelter.jpg",
        "Rockslide Elemental.jpg",
        "Spikeshot Elder.jpg",
        "Spire Barrage.jpg",
        "Spitting Earth.jpg",
        "Tephraderm.jpg",
        "Volcanic Strength.jpg",
        "Vulshok Berserker.jpg",
        "Vulshok Heartstoker.jpg",
        "Slagstorm.jpg",
        "Cyclops Gladiator.jpg",
        "Stuffy Doll.jpg",
    };
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
        "Fog.jpg",
        "Elvish Champion.jpg",
        "Hydra Omnivore.jpg",
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
        "Mold Shambler.jpg",
        "Windstorm.jpg",
        "Mass Polymorph.jpg",
    };
    private static String[] dragonsRoarCards = {
        "Assassinate.jpg",
        "Burst Lightning.jpg",
        "Crucible of Fire.jpg",
        "Disfigure.jpg",
        "Dragon Fodder.jpg",
        "Dragon's Claw.jpg",
        "Dragonmaster Outcast.jpg",
        "Dragonspeaker Shaman.jpg",
        "Festering Goblin.jpg",
        "Flameblast Dragon.jpg",
        "Furnace Whelp.jpg",
        "Furyborn Hellkite.jpg",
        "Giant Scorpion.jpg",
        "Goblin Offensive.jpg",
        "Goblin Piker.jpg",
        "Goblin Wardrive.jpg",
        "Gravedigger.jpg",
        "Hellkite Charger.jpg",
        "Manic Vandal.jpg",
        "Mountain.jpg",
        "Pyroclasm.jpg",
        "Raging Goblin.jpg",
        "Rally the Forces.jpg",
        "Rorix Bladewing.jpg",
        "Ruby Medallion.jpg",
        "Slavering Nulls.jpg",
        "Swamp.jpg",
        "Volcanic Dragon.jpg",
        "Voracious Dragon.jpg",
        "Breath of Malfegor.jpg",
        "Malfegor.jpg",
        "Earthquake.jpg",
    };
    private static String[] bloodHungerCards = {
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
        "Butcher of Malakir.jpg",
        "Barter in Blood.jpg",
        "Bloodhusk Ritualist.jpg",
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
        "Damnation.jpg",
        "Sphinx Sovereign.jpg",
        "Cumber Stone.jpg",
    };
    private static String[] unquenchableCards = {
        "Banefire.jpg",
        "Blaze.jpg",
        "Chandra's Outrage.jpg",
        "Chandra's Phoenix.jpg",
        "Cinder Wall.jpg",
        "Dragon's Claw.jpg",
        "Ember Shot.jpg",
        "Fiery Hellhound.jpg",
        "Fire Elemental.jpg",
        "Fire Servant.jpg",
        "Flame Slash.jpg",
        "Flame Wave.jpg",
        "Flameblast Dragon.jpg",
        "Flamekin Brawler.jpg",
        "Flametongue Kavu.jpg",
        "Goblin Arsonist.jpg",
        "Goblin War Paint.jpg",
        "Incinerate.jpg",
        "Inferno Titan.jpg",
        "Kiln Fiend.jpg",
        "Lava Axe.jpg",
        "Mountain.jpg",
        "Prodigal Pyromancer.jpg",
        "Punishing Fire.jpg",
        "Pyroclasm.jpg",
        "Relentless Assault.jpg",
        "Sizzle.jpg",
        "Volcanic Hammer.jpg",
        "Wheel of Fortune.jpg",
        "Flame Rift.jpg",
        "Volcanic Fallout.jpg",
        "Insurrection.jpg",
    };
    private static String[] apexPredatorsCards = {
        "Beast Hunt.jpg",
        "Borderland Ranger.jpg",
        "Centaur Courser.jpg",
        "Craw Wurm.jpg",
        "Cudgel Troll.jpg",
        "Dungrove Elder.jpg",
        "Elephant Guide.jpg",
        "Engulfing Slagwurm.jpg",
        "Forest.jpg",
        "Gaea's Revenge.jpg",
        "Garruk's Companion.jpg",
        "Garruk's Packleader.jpg",
        "Giant Growth.jpg",
        "Giant Spider.jpg",
        "Grazing Gladehart.jpg",
        "Hunted Wumpus.jpg",
        "Hunters' Feast.jpg",
        "Lead the Stampede.jpg",
        "Leatherback Baloth.jpg",
        "Multani, Maro-Sorcerer.jpg",
        "Nature's Lore.jpg",
        "Overrun.jpg",
        "Rites of Flourishing.jpg",
        "Runeclaw Bear.jpg",
        "Serrated Arrows.jpg",
        "Stomper Cub.jpg",
        "Terra Stomper.jpg",
        "Thrun, the last Troll.jpg",
        "Wall of Vines.jpg",
        "Wolfbriar Elemental.jpg",
        "Heartwood Storyteller.jpg",
        "Fresh Meat.jpg",
        "Copperhoof Vorrac.jpg",
    };
    private static String[] graveWhispersCards = {
        "Swamp.jpg",
        "Demon's Horn.jpg",
        "Disentomb.jpg",
        "Quest for the Gravelord.jpg",
        "The Rack.jpg",
        "Unholy Strength.jpg",
        "Consume Spirit.jpg",
        "Doom Blade.jpg",
        "Liliana's Caress.jpg",
        "Marsh Casualties.jpg",
        "Reassembling Skeleton.jpg",
        "Gloomhunter.jpg",
        "Hypnotic Specter.jpg",
        "Liliana's Specter.jpg",
        "Mind Rot.jpg",
        "Quag Sickness.jpg",
        "Blood Tithe.jpg",
        "Guul Draz Specter.jpg",
        "Moan of the Unhallowed.jpg",
        "Mortivore.jpg",
        "Scavenger Drake.jpg",
        "Syphon Mind.jpg",
        "Beacon of Unrest.jpg",
        "Bloodgift Demon.jpg",
        "Monomania.jpg",
        "Ob Nixilis, the Fallen.jpg",
        "Syphon Flesh.jpg",
        "Corrupt.jpg",
        "Grave Titan.jpg",
        "Massacre Wurm.jpg",
    };
    private static String[] cloudburstCards = {
        "Island.jpg",
        "Mountain.jpg",
        "Elixir of Immortality.jpg",
        "Lightning Bolt.jpg",
        "Lightning Serpent.jpg",
        "Shock.jpg",
        "Spark Elemental.jpg",
        "Into the Roil.jpg",
        "Reverberate.jpg",
        "Sparkmage Apprentice.jpg",
        "Storm Crow.jpg",
        "Thunder Strike.jpg",
        "Turn the Tide.jpg",
        "Æther Tradewinds.jpg",
        "Arc Runner.jpg",
        "Ball Lightning.jpg",
        "Electropotence.jpg",
        "Gelectrode.jpg",
        "Wee Dragonauts.jpg",
        "Wind Drake.jpg",
        "Lightning Elemental.jpg",
        "Skirsdag Cultist.jpg",
        "Skizzik.jpg",
        "Air Servant.jpg",
        "Murder of Crows.jpg",
        "Prophetic Bolt.jpg",
        "Spellbound Dragon.jpg",
        "Stormcloud Djinn.jpg",
        "Time Reversal.jpg",
        "Mahamoti Djinn.jpg",
        "Niv-Mizzet, the Firemind.jpg",
        "Thundermare.jpg",
        "Thunder Dragon.jpg",
    };
    private static String[] auramancerCards = {
        "Plains.jpg",
        "Forest.jpg",
        "Hyena Umbra.jpg",
        "Lifelink.jpg",
        "Rancor.jpg",
        "Suntail Hawk.jpg",
        "Wreath of Geists.jpg",
        "Canopy Cover.jpg",
        "Divine Favor.jpg",
        "Femeref Enchantress.jpg",
        "Fists of Ironwood.jpg",
        "Heroes' Reunion.jpg",
        "Kor Spiritdancer.jpg",
        "Nature's Spiral.jpg",
        "Pacifism.jpg",
        "Silhana Ledgewalker.jpg",
        "Silvercoat Lion.jpg",
        "Spectral Rider.jpg",
        "Stormfront Pegasus.jpg",
        "Armadillo Cloak.jpg",
        "Auramancer.jpg",
        "Aura Gnarlid.jpg",
        "Boar Umbra.jpg",
        "Griffin Guide.jpg",
        "Lure.jpg",
        "Mesa Enchantress.jpg",
        "Oakenform.jpg",
        "Sacred Wolf.jpg",
        "Snake Umbra.jpg",
        "Angelic Destiny.jpg",
        "Retether.jpg",
        "Bramble Elemental.jpg",
        "Gigantiform.jpg",
        "Mammoth Umbra.jpg",
        "Siege Mastodon.jpg",
        "Three Dreams.jpg",
        "Totem-Guide Hartebeest.jpg",
        "Mythic Proportions.jpg",
    };
    
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
        for (String e : bloodHungerCards) {
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
        
        File graveWhispers = new File(examples, "Grave Whispers");
        for (String e : graveWhispersCards) {
            save("/resources/cards/" + e, new File(graveWhispers, e));
        }
        
        File cloudburst = new File(examples, "Cloudburst");
        for (String e : cloudburstCards) {
            save("/resources/cards/" + e, new File(cloudburst, e));
        }
        
        File auramancer = new File(examples, "Auramancer");
        for (String e : auramancerCards) {
            save("/resources/cards/" + e, new File(auramancer, e));
        }
    }

    static void saveExampleDecks() {
        String[] decks = {
            "Apex Predators.txt",
            "Ancient Depths.txt",
            "Blood Hunger.txt",
            "Dragon's Roar.txt",
            "Guardians of the Wood.txt",
            "Machinations.txt",
            "Realm of Illusion.txt",
            "Strength of Stone.txt",
            "Unquenchable Fire.txt",
            "Wielding Steel.txt",
            "Auramancer.txt",
            "Cloudburst.txt",
            "Grave Whispers.txt",
        };

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
        } catch (Exception ex) {
            Debug.p("Resource " + resource + " could not be saved: "
                    + ex, Debug.E);
        }
    }
    
}
