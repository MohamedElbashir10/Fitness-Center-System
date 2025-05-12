package utils;

public class NotifService {

    public static void sendEmail(String to, String subject, String message) {
        // Placeholder for email logic
    }
        public static void sendNotification (String userId, String message){
            System.out.println("Notified " + userId + ": " + message);
        }

        public static void sendBookingConfirmation (String username, String message){
            // Simulate sending a booking confirmation
            System.out.println("Booking confirmation sent to " + username + ": " + message);
        }
    }
