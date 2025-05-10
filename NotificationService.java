import javax.swing.JOptionPane;

public class NotificationService {
    public static void sendEmailNotification(String to, String subject, String message) {
        System.out.println("EMAIL NOTIFICATION");
        System.out.println("To      : " + to);
        System.out.println("Subject : " + subject);
        System.out.println("Message : " + message);
        System.out.println("--------------------------------------");
    }


    public static void sendBookingConfirmation(String email, String sessionInfo) {
        String subject = "Booking Confirmation";
        String message = "Your session has been booked successfully:\n" + sessionInfo;
        sendEmailNotification(email, subject, message);
    }


    public static void sendSessionReminder(String email, String sessionInfo) {
        String subject = "Session Reminder";
        String message = "This is a reminder for your upcoming session:\n" + sessionInfo;
        sendEmailNotification(email, subject, message);
    }


    public static void sendCancellationNotification(String email, String sessionInfo) {
        String subject = "Session Cancelled";
        String message = "The following session has been cancelled:\n" + sessionInfo;
        sendEmailNotification(email, subject, message);
    }


    public static void sendSystemMessage(String email, String userId, String subject, String message) {
        sendEmailNotification(email, subject, message);
        sendInAppNotification(userId, message);
    }

    public static void sendInAppNotification(String userId, String message) {
        // Console log (debug amaçlı)
        System.out.println("IN-APP NOTIFICATION");
        System.out.println("User ID : " + userId);
        System.out.println("Message : " + message);
        System.out.println("--------------------------------------");


        JOptionPane.showMessageDialog(null,
                "Notification for user: " + userId + "\n" + message,
                "In-App Notification",
                JOptionPane.INFORMATION_MESSAGE);
    }

}
