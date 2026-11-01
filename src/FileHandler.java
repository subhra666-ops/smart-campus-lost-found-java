import java.io.*;

/**
 * FileHandler.java
 * All file handling in one place: items, students and the exported report.
 * Data is stored inside the "data" folder as plain text files.
 */
public class FileHandler {

    private static final String DATA_FOLDER = "data";
    private static final String ITEMS_FILE = DATA_FOLDER + File.separator + "items.txt";
    private static final String STUDENTS_FILE = DATA_FOLDER + File.separator + "students.txt";
    public static final String REPORT_FILE = DATA_FOLDER + File.separator + "report.txt";

    /** Creates the data folder if it does not exist. */
    private static void makeDataFolder() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    // ================= ITEMS =================

    /** Reads items.txt into the array. Returns how many records were loaded. */
    public static int loadItems(Item[] items) {
        int count = 0;
        File file = new File(ITEMS_FILE);
        if (!file.exists()) {
            return 0;                       // first run: nothing saved yet
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null && count < items.length) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Item item = Item.fromFileString(line);
                if (item != null) {
                    items[count++] = item;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading items file: " + e.getMessage());
        }
        return count;
    }

    /** Writes all records in the array back to items.txt. */
    public static void saveItems(Item[] items, int count) {
        makeDataFolder();
        try (PrintWriter writer = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            for (int i = 0; i < count; i++) {
                writer.println(items[i].toFileString());
            }
        } catch (IOException e) {
            System.out.println("Error saving items file: " + e.getMessage());
        }
    }

    // ================= STUDENTS =================

    /**
     * Looks up a student ID in students.txt.
     * Returns the saved line "id|name|password" or null if not found.
     */
    public static String findStudent(String studentId) {
        File file = new File(STUDENTS_FILE);
        if (!file.exists()) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length == 3 && parts[0].equalsIgnoreCase(studentId)) {
                    return line;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading students file: " + e.getMessage());
        }
        return null;
    }

    /** Adds a new student (appends one line to students.txt). */
    public static void registerStudent(String studentId, String name, String password) {
        makeDataFolder();
        try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_FILE, true))) {
            writer.println(studentId + "|" + name + "|" + password);
        } catch (IOException e) {
            System.out.println("Error saving student: " + e.getMessage());
        }
    }

    // ================= REPORT =================

    /** Writes any text (the full report) to report.txt. Returns true on success. */
    public static boolean writeReport(String reportText) {
        makeDataFolder();
        try (PrintWriter writer = new PrintWriter(new FileWriter(REPORT_FILE))) {
            writer.print(reportText);
            return true;
        } catch (IOException e) {
            System.out.println("Error writing report: " + e.getMessage());
            return false;
        }
    }
}
