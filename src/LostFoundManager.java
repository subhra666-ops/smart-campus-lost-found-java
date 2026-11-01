import java.util.Scanner;

/**
 * LostFoundManager.java
 * Holds the array of records and contains the logic for every module.
 */
public class LostFoundManager {

    private static final int MAX_ITEMS = 500;
    private static final String[] CATEGORIES = {"ID Card", "Wallet", "Keys", "Laptop", "Mobile Phone", "Other"};

    private Item[] items = new Item[MAX_ITEMS];   // array of records
    private int count;                            // how many records are stored
    private Scanner sc;

    public LostFoundManager(Scanner sc) {
        this.sc = sc;
        this.count = FileHandler.loadItems(items);
    }

    // =====================================================
    // Input helpers
    // =====================================================

    /** Reads a non-empty line. The '|' character is replaced because it is our file separator. */
    private String readText(String prompt) {
        String text;
        do {
            System.out.print(prompt);
            text = sc.nextLine().trim().replace('|', '/');
            if (text.isEmpty()) {
                System.out.println("  Input cannot be empty. Try again.");
            }
        } while (text.isEmpty());
        return text;
    }

    /** Reads an integer between min and max (inclusive). */
    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // fall through to the message below
            }
            System.out.println("  Please enter a number between " + min + " and " + max + ".");
        }
    }

    /** Reads a date in yyyy-MM-dd format. */
    private String readDate(String prompt) {
        while (true) {
            String date = readText(prompt);
            if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return date;
            }
            System.out.println("  Invalid format. Use yyyy-MM-dd (example: 2026-09-15).");
        }
    }

    /** Shows the category list and returns the chosen category. */
    private String chooseCategory() {
        System.out.println("Choose category:");
        for (int i = 0; i < CATEGORIES.length; i++) {
            System.out.println("  " + (i + 1) + ". " + CATEGORIES[i]);
        }
        int choice = readInt("Enter choice (1-" + CATEGORIES.length + "): ", 1, CATEGORIES.length);
        return CATEGORIES[choice - 1];
    }

    /** Next free record ID = highest existing ID + 1. */
    private int nextId() {
        int max = 0;
        for (int i = 0; i < count; i++) {
            if (items[i].getId() > max) {
                max = items[i].getId();
            }
        }
        return max + 1;
    }

    // =====================================================
    // Module 1: Student Login
    // =====================================================

    /** Logs a student in (or registers a new one). Returns the student ID, or null if login failed. */
    public String studentLogin() {
        System.out.println("\n===== STUDENT LOGIN =====");
        String studentId = readText("Enter Student ID: ").toUpperCase();
        String record = FileHandler.findStudent(studentId);

        if (record == null) {
            System.out.println("New student. Let's register you.");
            String name = readText("Enter your name: ");
            String password = readText("Create a password: ");
            FileHandler.registerStudent(studentId, name, password);
            System.out.println("Registered successfully! Welcome, " + name + ".");
            return studentId;
        }

        String[] parts = record.split("\\|", -1);
        for (int attempt = 1; attempt <= 3; attempt++) {
            String password = readText("Enter password: ");
            if (password.equals(parts[2])) {
                System.out.println("Login successful! Welcome back, " + parts[1] + ".");
                return studentId;
            }
            System.out.println("Wrong password. Attempts left: " + (3 - attempt));
        }
        System.out.println("Too many wrong attempts.");
        return null;
    }

    // =====================================================
    // Module 2 & 3: Report Lost / Found item
    // =====================================================

    public void reportItem(String type, String studentId) {
        if (count >= MAX_ITEMS) {
            System.out.println("Storage is full. Delete some records first.");
            return;
        }
        System.out.println("\n===== REPORT " + type + " ITEM =====");
        String name = readText("Item name (e.g. Black Wallet): ");
        String category = chooseCategory();
        String description = readText("Description (colour, brand, marks): ");
        String location = readText("Location where it was " + type.toLowerCase() + ": ");
        String date = readDate("Date (yyyy-MM-dd): ");

        Item item = new Item(nextId(), type, name, category, description,
                location, date, studentId, "OPEN", 0);
        items[count++] = item;
        FileHandler.saveItems(items, count);

        System.out.println("\n" + type + " item reported successfully! Record ID = " + item.getId());
    }

    // =====================================================
    // Module 4: Search Item (linear search)
    // =====================================================

    public void searchItem() {
        System.out.println("\n===== SEARCH ITEM =====");
        String keyword = readText("Enter keyword (name, category, description or location): ").toLowerCase();

        int found = 0;
        for (int i = 0; i < count; i++) {
            Item it = items[i];
            String allText = (it.getName() + " " + it.getCategory() + " "
                    + it.getDescription() + " " + it.getLocation()).toLowerCase();
            if (allText.contains(keyword)) {
                System.out.println(it.toDisplayString());
                found++;
            }
        }
        if (found == 0) {
            System.out.println("No records match \"" + keyword + "\".");
        } else {
            System.out.println("----------------------------------------");
            System.out.println(found + " record(s) found.");
        }
    }

    // =====================================================
    // Module 5: Auto Match Lost & Found
    // =====================================================

    /** Two names are similar if one contains the other or they share a word longer than 2 letters. */
    private boolean namesAreSimilar(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        if (a.contains(b) || b.contains(a)) {
            return true;
        }
        String[] wordsA = a.split("\\s+");
        String[] wordsB = b.split("\\s+");
        for (int i = 0; i < wordsA.length; i++) {
            for (int j = 0; j < wordsB.length; j++) {
                if (wordsA[i].length() > 2 && wordsA[i].equals(wordsB[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    public void autoMatch() {
        System.out.println("\n===== AUTO MATCH LOST & FOUND =====");
        int matches = 0;

        for (int i = 0; i < count; i++) {
            Item lost = items[i];
            if (!lost.getType().equals("LOST") || !lost.getStatus().equals("OPEN")) {
                continue;
            }
            for (int j = 0; j < count; j++) {
                Item found = items[j];
                if (!found.getType().equals("FOUND") || !found.getStatus().equals("OPEN")) {
                    continue;
                }
                // Rule: same category AND similar item name
                if (lost.getCategory().equalsIgnoreCase(found.getCategory())
                        && namesAreSimilar(lost.getName(), found.getName())) {

                    lost.setStatus("MATCHED");
                    lost.setMatchedId(found.getId());
                    found.setStatus("MATCHED");
                    found.setMatchedId(lost.getId());
                    matches++;

                    System.out.println("MATCH #" + matches);
                    System.out.println("  LOST  (ID " + lost.getId() + "): " + lost.getName()
                            + " | reported by " + lost.getReportedBy());
                    System.out.println("  FOUND (ID " + found.getId() + "): " + found.getName()
                            + " | at " + found.getLocation() + " | reported by " + found.getReportedBy());
                    System.out.println("  -> Owner " + lost.getReportedBy() + " should collect the item.");
                    break;   // this lost item is matched, go to next lost item
                }
            }
        }

        if (matches == 0) {
            System.out.println("No new matches found.");
        } else {
            FileHandler.saveItems(items, count);
            System.out.println("\n" + matches + " new match(es) saved.");
        }
    }

    // =====================================================
    // Module 6: View All Records (with sorting)
    // =====================================================

    public void viewAll() {
        System.out.println("\n===== VIEW ALL RECORDS =====");
        if (count == 0) {
            System.out.println("No records yet.");
            return;
        }
        System.out.println("Sort by:  1. ID   2. Item Name (A-Z)   3. Date (oldest first)");
        int choice = readInt("Enter choice (1-3): ", 1, 3);

        // Make a copy so the original order stays unchanged
        Item[] sorted = new Item[count];
        for (int i = 0; i < count; i++) {
            sorted[i] = items[i];
        }

        // Bubble sort
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - 1 - i; j++) {
                boolean swap = false;
                if (choice == 1) {
                    swap = sorted[j].getId() > sorted[j + 1].getId();
                } else if (choice == 2) {
                    swap = sorted[j].getName().compareToIgnoreCase(sorted[j + 1].getName()) > 0;
                } else {
                    swap = sorted[j].getDate().compareTo(sorted[j + 1].getDate()) > 0;
                }
                if (swap) {
                    Item temp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = temp;
                }
            }
        }

        for (int i = 0; i < count; i++) {
            System.out.println(sorted[i].toDisplayString());
        }
        System.out.println("----------------------------------------");
        System.out.println("Total records: " + count);
    }

    // =====================================================
    // Module 7: Delete Record
    // =====================================================

    public void deleteRecord(String studentId) {
        System.out.println("\n===== DELETE RECORD =====");
        if (count == 0) {
            System.out.println("No records to delete.");
            return;
        }
        int id = readInt("Enter record ID to delete: ", 1, Integer.MAX_VALUE);

        int position = -1;
        for (int i = 0; i < count; i++) {
            if (items[i].getId() == id) {
                position = i;
                break;
            }
        }
        if (position == -1) {
            System.out.println("No record with ID " + id + ".");
            return;
        }
        if (!items[position].getReportedBy().equalsIgnoreCase(studentId)) {
            System.out.println("You can delete only the records reported by you.");
            return;
        }

        System.out.println(items[position].toDisplayString());
        System.out.print("Delete this record? (y/n): ");
        String answer = sc.nextLine().trim();
        if (!answer.equalsIgnoreCase("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        // Shift the remaining records one place to the left
        for (int i = position; i < count - 1; i++) {
            items[i] = items[i + 1];
        }
        items[count - 1] = null;
        count--;

        // If other records were matched with the deleted one, reopen them
        for (int i = 0; i < count; i++) {
            if (items[i].getMatchedId() == id) {
                items[i].setMatchedId(0);
                items[i].setStatus("OPEN");
            }
        }
        FileHandler.saveItems(items, count);
        System.out.println("Record deleted.");
    }

    // =====================================================
    // Module 8: Item Statistics
    // =====================================================

    private String buildStatistics() {
        int lost = 0, found = 0, matched = 0, open = 0;
        int[] perCategory = new int[CATEGORIES.length];

        for (int i = 0; i < count; i++) {
            if (items[i].getType().equals("LOST")) {
                lost++;
            } else {
                found++;
            }
            if (items[i].getStatus().equals("MATCHED")) {
                matched++;
            } else {
                open++;
            }
            for (int c = 0; c < CATEGORIES.length; c++) {
                if (CATEGORIES[c].equalsIgnoreCase(items[i].getCategory())) {
                    perCategory[c]++;
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Total records   : ").append(count).append("\n");
        sb.append("Lost items      : ").append(lost).append("\n");
        sb.append("Found items     : ").append(found).append("\n");
        sb.append("Matched records : ").append(matched).append("\n");
        sb.append("Open records    : ").append(open).append("\n");
        sb.append("\nItems by category:\n");
        for (int c = 0; c < CATEGORIES.length; c++) {
            sb.append(String.format("  %-14s: %d%n", CATEGORIES[c], perCategory[c]));
        }
        return sb.toString();
    }

    public void showStatistics() {
        System.out.println("\n===== ITEM STATISTICS =====");
        System.out.print(buildStatistics());
    }

    // =====================================================
    // Module 9: Export Report
    // =====================================================

    public void exportReport() {
        System.out.println("\n===== EXPORT REPORT =====");
        StringBuilder sb = new StringBuilder();
        sb.append("SMART CAMPUS LOST & FOUND - REPORT\n");
        sb.append("========================================\n");
        sb.append(buildStatistics());
        sb.append("\nALL RECORDS\n");
        for (int i = 0; i < count; i++) {
            sb.append(items[i].toDisplayString()).append("\n");
        }
        if (FileHandler.writeReport(sb.toString())) {
            System.out.println("Report exported to: " + FileHandler.REPORT_FILE);
        }
    }
}
