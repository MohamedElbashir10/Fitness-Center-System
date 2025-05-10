import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerUtils {

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT));
    }

    public static void logSuccess(String message) {
        System.out.println("[SUCCESS] [" + getTimestamp() + "] " + message);
    }

    public static void logError(String message) {
        System.out.println("[ERROR]   [" + getTimestamp() + "] " + message);
    }

    public static void logWarning(String message) {
        System.out.println("[WARNING] [" + getTimestamp() + "] " + message);
    }

    public static void logInfo(String message) {
        System.out.println("[INFO]    [" + getTimestamp() + "] " + message);
    }

    public static void logSection(String sectionTitle) {
        System.out.println("\n=== " + sectionTitle.toUpperCase() + " ===");
    }
} 
