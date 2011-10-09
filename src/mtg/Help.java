package mtg;

import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

/**
 * @author Jaroslaw Pawlak
 */
public class Help extends JTextArea {

    private Help(String text) {
        super(text);
        this.setFont(new Font("Arial", Font.PLAIN, 12));
        this.setEditable(false);
        this.setLineWrap(true);
        this.setWrapStyleWord(true);
        this.setRows(15);
        this.setColumns(60);
    }
    
    public static void show(JFrame parent) {
        JTabbedPane contentPane = new JTabbedPane(JTabbedPane.TOP,
                JTabbedPane.WRAP_TAB_LAYOUT);
        contentPane.setOpaque(true);
        
        
        JTextArea general = new Help(
                "This application is not a real Magic the Gathering game. "
                + "It does not provide any game functions - there are no "
                + "turns and phases, there is no mana pool, "
                + "nothing is done automatically. "
                + "It is just a virtual table where players can play cards "
                + "however they want.\n"
                + "\n"
                + "There is also no AI, you cannot play against computer opponent, "
                + "you will need a live one. If you are interested how it "
                + "looks, you can start a single game with no other players. "
                + "You will be able to play cards, destroy them, draw cards "
                + "from library, create tokens, toss a coin and everything "
                + "else you would normally do in Magic the Gathering, "
                + "but no one will disturb you."
                );
        contentPane.addTab("General", general);
        
        JTextArea deckCreator = new Help(
                "The greatest fun in Magic the Gathering is building your own "
                + "deck. Deck creator is a simple tool which lets you build "
                + "your own deck. You can easily search through cards you have, "
                + "add them to a deck and save it. Your active deck used for "
                + "play is defined in settings.\n"
                + "\n"
                + "Application contains cards and decks from "
                + "Magic the Gathering: Duel of the Planewalkers 2012. "
                + "Decks are saved in human readable text so you can also "
                + "modify existing decks in a notepad. \n"
                + "\n"
                + "Is there your favorite card missing? That's not a problem! "
                + "Simply visit http://magiccards.info/search.html, find a card "
                + "you want, download its image and save as a jpg file anywhere in "
                + Main.CARDS + ", add to your deck and enjoy it! "
                + " And what happens if your opponent does not have this card? "
                + "You do not have to care about it, because application will "
                + "send missing card to him, before game starts! Therefore "
                + "it is important to name a file you save using exact card name."
                );
        contentPane.addTab("Deck creator", deckCreator);
        
        JTextArea gameStart = new Help(
                "Before you play, you need to join a server or create a one. "
                + "Don't be scared! It is extremaly simple! Press \"Create game\", "
                + "specify the number of players and press \"Create\" twice "
                + "and it's done. At this point your IP address and port you "
                + "specified are in your system clipboard, so you can easily "
                + "send it to a friend. And that's what you need "
                + "if you are joining someone else's game.\n"
                + "\n"
                + "You join a game with a name specified in settings. "
                + "Before all players are connected, all game features are disabled. "
                + "You can only chat with already connected players.\n"
                + "\n"
                + "If you are in inner network (i.e. your computer does not have its "
                + "own IP address) you may encounter some difficulties with "
                + "creating a server and others will not be able to join "
                + "your game. In such a case, ask your opponent to host a game "
                + "or even any other friend - he does not have to play!"
                );
        contentPane.addTab("Starting a game", gameStart);
        
        JTextArea gameplay1 = new Help(
                "Cards you see at the bottom is your hand - it is visible only "
                + "to you. If you cannot read card's description, you can "
                + "view it in its original sizes using mouse wheel. "
                + "You can play cards via right clicking them and "
                + "choosing a proper option. Once played, card will appear on "
                + "the table above. You can drag cards, tap them with left "
                + "double click or choose different option with right mouse "
                + "button. You can zoom in and out entire table with your "
                + "mouse wheel. To see other parts of table, just drag it - "
                + "exactly as you did with cards but press in a place where there "
                + "is no card. You can do it with any of your mouse buttons "
                + "to move with different speed.\n"
                + "\n"
                + "In the top right corner you can see information about other "
                + "players and yourself as well. You can change players' health "
                + "and poison counters or view their graveyards and exiled cards. "
                + "In both those zones, you can use right mouse button to view "
                + "available options or mouse wheel to enlarge the card.\n"
                + "\n"
                + "Whatever you do, a notification is shown to all players in "
                + "the bottom right corner. If notification says about a card on "
                + "a table, you can press \"show\" to automatically move to this "
                + "card. If a card is not on a table, its image of original sizes "
                + "is displayed. Notifications area works also as a chat."
                );
        contentPane.addTab("Gameplay 1/2", gameplay1);
        
        JTextArea gameplay2 = new Help(
                "In \"Your library\" menu you can draw a card from the top, "
                + "put it directly onto a table or just reveal it. You can also "
                + "look at given number of cards from the top (it will not be "
                + "visible to other players) using \"Search\". "
                + "Use right mouse button to see "
                + "a popup menu with available options.\n"
                + "\n"
                + "In the bottom left corner there are some other useful "
                + "features. You can untap all yours cards with just one click. "
                + "You can create a token (will be completely removed "
                + "from the game once taken from the table), choose a card at "
                + "random from your hand or return a random number. You can "
                + "also destroy all your tokens, shuffle all cards you own "
                + "into your library and draw seven cards by using \"Restart\".\n"
                + "\n"
                + "Keep in mind that every notification you see is also visible "
                + "to other players, but it may not display card's name, "
                + "depending on your "
                + "choices. E.g. if you draw a card, you see in notifications "
                + "what card it was, but your opponents do not. When you move "
                + "cards between your hand and library you can choose an option "
                + "to just move a card, without revealing it. In every other case "
                + "your opponents will see card's details. Searching a library "
                + "is also private and other players only see that you do this."
                );
        contentPane.addTab("Gameplay 2/2", gameplay2);
        
        JTextArea other = new Help(
                "Version: " + Main.VERSION + "\n"
                + "Date: " + "09.10.2011" + "\n"
                + "Author: Jaroslaw Pawlak\n"
                + "\n"
                + "© All names and cards' images "
                + "are the property of Wizards of the Coast\n"
                + "\n"
                + "For any questions or suggestions visit "
                + "jaroslawpawlak.wordpress.com\n"
                + "or email me at jarcionek@gmail.com"
                );
        contentPane.addTab("About", other);
        
        JOptionPane.showMessageDialog(parent, contentPane, Main.TITLE,
                JOptionPane.PLAIN_MESSAGE);
    }
}
