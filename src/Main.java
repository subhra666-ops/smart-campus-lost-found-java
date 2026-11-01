import java.util.Scanner;

/**
 * Main.java
 * Entry point: shows the menu and calls the right module.
 */
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LostFoundManager manager = new LostFoundManager(sc);

        System.out.println("========================================");
        System.out.println("  SMART CAMPUS LOST & FOUND SYSTEM");
        System.out.println("  COER University, Roorkee");
        System.out.println("========================================");

        // A student must log in before using the system
        String currentStudent = null;
        while (currentStudent == null) {
            currentStudent = manager.studentLogin();
        }

        int choice = 0;
        while (choice != 10) {
            printMenu(currentStudent);
            System.out.print("Enter your choice (1-10): ");
            String line = sc.nextLine().trim();
            try {
                choice = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                choice = 0;
            }

            switch (choice) {
                case 1:
                    String newStudent = manager.studentLogin();
                    if (newStudent != null) {
                        currentStudent = newStudent;
                    }
                    break;
                case 2:
                    manager.reportItem("LOST", currentStudent);
                    break;
                case 3:
                    manager.reportItem("FOUND", currentStudent);
                    break;
                case 4:
                    manager.searchItem();
                    break;
                case 5:
                    manager.autoMatch();
                    break;
                case 6:
                    manager.viewAll();
                    break;
                case 7:
                    manager.deleteRecord(currentStudent);
                    break;
                case 8:
                    manager.showStatistics();
                    break;
                case 9:
                    manager.exportReport();
                    break;
                case 10:
                    System.out.println("Thank you for using the system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 10.");
            }
        }
        sc.close();
    }

    private static void printMenu(String studentId) {
        System.out.println("\n========== MAIN MENU (Logged in: " + studentId + ") ==========");
        System.out.println(" 1. Student Login (switch user)");
        System.out.println(" 2. Report Lost Item");
        System.out.println(" 3. Report Found Item");
        System.out.println(" 4. Search Item");
        System.out.println(" 5. Auto Match Lost & Found");
        System.out.println(" 6. View All Records");
        System.out.println(" 7. Delete Record");
        System.out.println(" 8. Item Statistics");
        System.out.println(" 9. Export Report");
        System.out.println("10. Exit");
    }
}
