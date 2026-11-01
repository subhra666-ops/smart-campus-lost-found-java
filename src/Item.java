/**
 * Item.java
 * Structure (class) that stores ONE lost or found record.
 */
public class Item {

    private int id;              // unique record number
    private String type;         // "LOST" or "FOUND"
    private String name;         // e.g. "Black Wallet"
    private String category;     // e.g. "Wallet"
    private String description;  // colour, brand, marks, etc.
    private String location;     // where it was lost / found
    private String date;         // yyyy-MM-dd
    private String reportedBy;   // student ID of the reporter
    private String status;       // "OPEN" or "MATCHED"
    private int matchedId;       // id of the matching record (0 = none)

    public Item(int id, String type, String name, String category, String description,
                String location, String date, String reportedBy, String status, int matchedId) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.category = category;
        this.description = description;
        this.location = location;
        this.date = date;
        this.reportedBy = reportedBy;
        this.status = status;
        this.matchedId = matchedId;
    }

    // ---------- Getters ----------
    public int getId() { return id; }
    public String getType() { return type; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getReportedBy() { return reportedBy; }
    public String getStatus() { return status; }
    public int getMatchedId() { return matchedId; }

    // ---------- Setters (only the fields that can change) ----------
    public void setStatus(String status) { this.status = status; }
    public void setMatchedId(int matchedId) { this.matchedId = matchedId; }

    /** Converts this record into one line of text for saving in items.txt. */
    public String toFileString() {
        return id + "|" + type + "|" + name + "|" + category + "|" + description + "|"
                + location + "|" + date + "|" + reportedBy + "|" + status + "|" + matchedId;
    }

    /** Builds an Item from one line of items.txt. Returns null if the line is damaged. */
    public static Item fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length != 10) {
            return null;
        }
        try {
            return new Item(Integer.parseInt(p[0]), p[1], p[2], p[3], p[4],
                    p[5], p[6], p[7], p[8], Integer.parseInt(p[9]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Returns a nicely formatted multi-line description of the record. */
    public String toDisplayString() {
        String text = "----------------------------------------\n"
                + "ID          : " + id + "\n"
                + "Type        : " + type + "\n"
                + "Item Name   : " + name + "\n"
                + "Category    : " + category + "\n"
                + "Description : " + description + "\n"
                + "Location    : " + location + "\n"
                + "Date        : " + date + "\n"
                + "Reported By : " + reportedBy + "\n"
                + "Status      : " + status;
        if (matchedId != 0) {
            text += " (matched with ID " + matchedId + ")";
        }
        return text;
    }
}
