public class NotificationService {

    public static void sendEmail(String to, String subject, String message) {
        // Placeholder for email logic
        System.out.println("Email to " + to + ": " + subject + " - " + message);
    }

    public static void sendNotification(String userId, String message) {
        System.out.println("Notified " + userId + ": " + message);
    }
}
