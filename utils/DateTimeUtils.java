import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
