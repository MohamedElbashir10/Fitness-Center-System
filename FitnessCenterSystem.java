import java.util.Scanner;

public class FitnessCenterSystem{
  public static void main(String [] args) {
    Scanner scanner= new Scanner(System.in);

    RegistrationService registrationService = new RegistrationService();
    AuthService authService = new AuthService();
    Schedule schedule = new Schedule ();
    Reservation reservation = new Reservation();
    User loggedInUser = null;
    boolean running = true;

    system.out.println(" Welcome to the Fitness Center System! ");

    while(running){
      system.out.println("\n ----Main Menu----");
      system.out.println(" 1 - Register");
      system.out.println(" 2 - Login");
      system.out.println(" 3 - View Schedule");
      system.out.println(" 4 - Make Reservation ");
      system.out.println(" 0 - Exit");
      system.out.println(" Your choice: ");
      String choice = scanner.nextLine();

    switch (choice){
      case "1":
          System.out.print("ID: ");
          String id = scanner.nextLine();

          System.out.print("Full Name: ");
          String name = scanner.nextLine();

          System.out.print("Email: ");
          String name = scanner.nextLine();

          System.out.print("Email: ");
          String email = scanner.nextLine();

          System.out.print("Password: ");
          String password = scanner.nextLine();

          System.out.print("Role (member/trainer/admin): ");
          String role = scanner.nextLine();

          registrationService.registerUser(id, name, email, password, role);
          break;

        case "2":
          System.out.print("Email: ");
          String loginEmail = scanner.nextLine();

          System.out.print("Password: ");
          String loginPassword = scanner.nextLine();

          loggedInUser = authService.login(loginEmail, loginPassword);

          if (loggedInUser != null) {
              System.out.println("[SUCCESS] Logged in as: " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")");
              // Role-based routing - handled by UserInteractionHandler later
              String roleType = loggedInUser.getRole().toLowerCase();
              switch (roleType) {
                  case "member":
                      System.out.println("→ Redirecting to Booking Menu... (To be handled by Session Booking Coordinator)");
                      break;
                  case "trainer":
                      System.out.println("→ Redirecting to Trainer Schedule View... (To be handled by User Interaction Handler)");
                      break;
                  case "admin":
                      System.out.println("→ Redirecting to Admin Management Panel... (Optional)");
                      break;
                  default:
                      System.out.println("[ERROR] Unknown role.");
              }
        }else {
            System.out.println("[ERROR] Login failed. Invalid credentials.");
        }
        break;

          case "3":
            schedule.displaySchedule(); // Placeholder
            break;

          case "4":
              if (loggedInUser == null) {
                  System.out.println("[ERROR] Please log in first.");
            } else {
                reservation.makeReservation(loggedInUser); // Placeholder
            }
            break;

            case "0":
                running = false;
                System.out.println("Goodbye! ");
                break;

            default:
                System.out.println("[ERROR] Invalid choice.");
          }
      }

      scanner.close();
  }
}
                    
