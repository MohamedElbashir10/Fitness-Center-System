import java.time.LocalDate;
import java.time.LocalTime;

public class Availability {
  private LocalDate date;
  private LocalTime startTime;
  private LocalTime endTime;

  public Availability(LocalDate date, LocalTime startTime, LocalTime endTime) {
    this.date = date;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public void addAvailability() {
  }

  public void displayAvailability() {
    System.out.println("Availability Details: ");
    System.out.println("Date: " + date);
    System.out.println("Start Time: " + startTime);
    System.out.println("End Time: " + endTime);
  }

}
