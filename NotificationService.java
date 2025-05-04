import javax.swing.JOptionPane;

public class NotificationService {
  public static void sendEmailNotification(String to, String subject, String message)
    system.out.println(" EMAIL NOTIFICATION " );
    system.out.println( " TO : " + to);
    system.out.println( " Subject : " + subject);
    system.out.println( " Message : " + message);
    system.out.println( " ------------------");
}
public static void sendInAppNotification(String userId, String message) {
        System.out.println("IN-APP NOTIFICATION");
        System.out.println("User ID : " + userId);
        System.out.println("Message : " + message);
        System.out.println("--------------------------------------");

        JOptionPane.showMessageDialog(null,
                "Notification for user: " + userId + "\n" + message,
                "In-App Notification",
                JOptionPane.INFORMATION_MESSAGE);
}
public static void sendBookingConfirmation( String email, String sessionInfo){
  String subject = " Booking Confirmation ";
  String message = " Your session has been booked succesfully : \n" + sessionInfo;
  sendEmailNotification(email, subject, message);
}
public static void sendSessionReminder(String email, String sessionInfo) {
        String subject = "Session Reminder";
        String message = "This is a reminder for your upcoming session:\n" + sessionInfo;
        sendEmailNotification(email, subject, message);
}
public static void sendCancellationNotification(String email, String sessionInfo){
  String subject= "Sesssion Cancelled";
  String message= "The following session has been cancelled:\n" + sessionInfo;
  sendEmailNotification(email, subject, message);
}
public static void sendSystemMessage(String email, String userId, String subject, String message)
  sendEmailNotification(email, subject, message);
  sendAppNotification(userId, message);
}

}
  
