import java.time.LocalTime;

public class SessionUtils {

    public static boolean isOverlapping(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }
}
