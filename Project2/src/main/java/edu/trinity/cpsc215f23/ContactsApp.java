package edu.trinity.cpsc215f23;

import edu.trinity.cpsc215f23.map.Entry;
import edu.trinity.cpsc215f23.treemap.BinarySearchTreeMap;

import java.util.*;

/**
 * This is the entry point for the command line application.
 * This class also provides a testing driver for the dependent classes.
 *
 * @author Takunari Miyazaki
 * @author Saqlain Anjum
 * @version 1.0.0
 */
public class ContactsApp {

    /**
     * The collection of contacts managed by this application.
     */
    private final BinarySearchTreeMap<String, HashMap<Communications, String>> contacts = new BinarySearchTreeMap<>();


    /**
     * The application entry point.
     *
     * @param args The command line arguments. If no argument provided, then the application menu is shown.
     *             If the argument is "-test", then the unit tests are executed and the application exists.
     */
    public static void main(String... args) {
        ContactsApp contactsApp = new ContactsApp();
        contactsApp.menu();
    }

    /**
     * Converts a string of comma separated keys and values of communication options into a
     * HashMap of the communication options. For instance the string "email: me@trinity.com, link: me, snap: @me" is
     * converted into a map of keys and values where the keys are the Communications enums (EMAIL, SNAPCHAT, etc.),
     * and the values are the associated communication ids on that platform, e.g. "me@trinity.com".
     *
     * @param platforms The string representing the options
     * @return The parsed communication options as a HashMap collection
     */
    public HashMap<Communications, String> parseCommunications(String platforms) throws IllegalArgumentException {
        HashMap<Communications, String> communications = new HashMap<>();

        // Parse the string at the commas into an array of tokens
        for (String platform : platforms.split(",")) {

            // Parse each token at the colon, to separate the communication types and its associated id
            String[] platformKeyValue = platform.split(":");

            // Array must have two parts, e.g. "email" and "me@trinity.com"
            if (platformKeyValue.length == 2) {
                // Given the token like "g", find the matching enum, such as "GITHUB", assumes first letter is unique
                String platformLetter = String.valueOf(platformKeyValue[0].trim().toUpperCase().charAt(0));
                Communications com = Arrays.stream(
                                Communications.values()).filter(communication ->
                                communication.name().startsWith(platformLetter)).findFirst()
                        .orElseThrow(IllegalArgumentException::new);
                communications.put(com, platformKeyValue[1].trim());   // Add to map "EMAIL" associated with "me@trinity.com"
            }
        }

        return communications;
    }

    /**
     * Present the application menu.
     */
    private void menuPresent() {
        System.out.println("\nContact Manager Menu");
        System.out.println("--------------------");
        System.out.println("1 - Search for a contact");
        System.out.println("2 - Add a new contact");
        System.out.println("3 - Remove contact");
        System.out.println("4 - List all information for all contacts");
        System.out.println("5 - List all contact names");
        System.out.println("6 - List all contact communications");
        System.out.println("---");
        System.out.println("7 - End this contact manager session.");

        System.out.print("\nMenu choice: ");
    }

    /**
     * Present the application menu and respond to selections.
     */
    public void menu() {
        for (; ; ) {
            menuPresent();
            Scanner input = new Scanner(System.in);

            int selection;
            try {
                selection = input.nextInt();
                input.nextLine(); // Consume the enter key after the number
            } catch (NoSuchElementException ex) {
                selection = 0;
            }
            switch (selection) {
                case 1:
                    searchForContact(input);
                    break;
                case 2:
                    addContact(input);
                    break;
                case 3:
                    removeContact(input);
                    break;
                case 4:
                    System.out.println(listAllContacts());
                    break;
                case 5:
                    System.out.println(listAllContactNames());
                    break;
                case 6:
                    System.out.println(listAllContactCommunications());
                    break;
                case 7:
                    System.exit(0);
                default:
                    System.out.println("Select a menu choice from 1 to 7.");
            }
        }
    }

    /**
     * Prompt user for a contact name and either show the information of the found contact or report the contact
     * was not found with the given name.
     *
     * @param input The input console stream
     */
    private void searchForContact(Scanner input) {
        System.out.println("Search for contact:");
        String name = promptFullName(input);
        if (name.isBlank()) {
            System.out.format("The contact's first or last name (%s) was invalid.%n", name);
            return;
        }

        boolean contactFound = false;
        Iterable<String> contactNameCollection = contacts.keySet();
        for (String contactName : contactNameCollection) {
            if (contactName.equals(name)) {
                System.out.println("Contact Information: " + contacts.get(name));
                contactFound = true;
                break;
            }
        }
        if (!contactFound) {
            System.out.println(name + " was not found!");
        }
    }

    /**
     * Prompt user for a contact name and contact information. If the contact exists, user is prompted to confirm
     * the update. If the input is valid, the contact is either added or updated.
     *
     * @param input The input console stream
     */
    private void addContact(Scanner input) {
        boolean duplicate = false;

        System.out.println("Add contact:");
        String name = promptFullName(input);

        Iterable<String> contactNames = contacts.keySet();
        for (String contactName : contactNames) {
            if (contactName.equals(name)) {
                duplicate = true;
                break;
            }
        }

        if (duplicate) {
            System.out.println("Do you want to update this contact?");
            String response = input.nextLine();
            if (response.equalsIgnoreCase("yes")) {
                System.out.println("Insert new name: ");
                String newName = promptFullName(input);
                HashMap<Communications, String> oldValue = contacts.remove(name);
                contacts.put(newName, oldValue);
            }
        }

        if (name.isBlank()) {
            System.out.format("The contact's first or last name (%s) was invalid.%n", name);
            return;
        }

        String coms = "";
        HashMap<Communications, String> comsCollection;
        if (!duplicate) {
            System.out.println("  Communication options example: website: www.oceanfutures.org, m: 805-899-8899");
            System.out.print("  Communication options: ");
            coms = input.nextLine().trim();
            try {
                comsCollection = parseCommunications(String.join(", ", coms));
            } catch (IllegalArgumentException ex) {
                System.out.format("Media option in '%s' not recognized.%n", coms);
                return;
            }
            contacts.put(name, comsCollection);
        }

    }

    /**
     * Prompt user for a contact name and either remove the contact or report the contact
     * was not found with the given name.
     *
     * @param input The input console stream
     */
    private void removeContact(java.util.Scanner input) {
        System.out.println("Remove contact:");
        String name = promptFullName(input);
        if (name.isBlank()) {
            System.out.format("The contact's first or last name (%s) was invalid.%n", name);
            return;
        }
        if (contacts.get(name) != null) {
            contacts.remove(name);
        } else {
            System.out.println("ERROR: CONTACT NOT FOUND!");
        }
    }

    /**
     * A formatted string for the console containing the list of the contact names in alphabetical order with
     * their associated communication options.
     *
     * @return A string containing the list of the contact names in alphabetical order.
     */
    public String listAllContacts() {
        List<String> elementsAsText = new ArrayList<>(contacts.size());

        for (Entry<String, HashMap<Communications, String>> entry : contacts.entrySet()) {
            elementsAsText.add(String.format("%s: %s", entry.getKey(), entry.getValue()));
        }

        // Return results without extra comma at end
        return "\n" +
                "All Contacts" + "\n" +
                "------------" + "\n" +
                String.join("\n", elementsAsText) +
                "\n";
    }

    /**
     * A formatted string for the console containing the list of the contact names in alphabetical order.
     *
     * @return A string containing the list of the contact names in alphabetical order.
     */
    public String listAllContactNames() {
        Iterable<String> contactNameCollection = contacts.keySet();
        StringBuilder toReturn = new StringBuilder();
        for (String contactName : contactNameCollection) {
            toReturn.append(contactName).append("\n");
        }
        return "\n" +
                "All Contacts" + "\n" +
                "------------" + "\n" +
                String.join("\n", toReturn.toString()) +
                "\n";
    }

    /**
     * A formatted string for the console containing the list of the contact names in alphabetical order.
     *
     * @return A string containing the list of the contact names in alphabetical order.
     */
    public String listAllContactCommunications() {
        StringBuilder toReturn = new StringBuilder();
        Iterable<HashMap<Communications, String>> contactMap = contacts.values();
        for (HashMap<Communications, String> innerMap : contactMap) {
            for (Communications communication : innerMap.keySet()) {
                toReturn.append("{" + communication + ", " + innerMap.get(communication) + "}");
            }
            toReturn.append("\n");
        }
        return "\n" +
                "All Contacts" + "\n" +
                "------------" + "\n" +
                toReturn +
                "\n";
    }

    /**
     * Prompts user for contact first and last name.
     *
     * @param input The input console stream
     * @return The first and last name formatted as 'last, first'. Returns blank if name is invalid.
     */
    private String promptFullName(java.util.Scanner input) {
        System.out.print("  First name: ");
        String firstName = input.nextLine().trim();
        System.out.print("  Last name: ");
        String lastName = input.nextLine().trim();

        return firstName.isEmpty() || lastName.isEmpty() ? "" : lastName + ", " + firstName;
    }

    /**
     * A method for accessing the contacts object being used throughout the class.
     *
     * @return The contacts object defined as a field of the class.
     */
    public BinarySearchTreeMap<String, HashMap<Communications, String>> getContacts() {
        return contacts;
    }
}