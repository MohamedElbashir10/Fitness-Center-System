public class Availabiltiy;
  private LocalDate date;
  private LocalTime startTime;
  private LocalTime endTime;

  public Availablity(LocalDate date, LocalTime startTime, LocalTime endTime){
      this.date=date;
      this.startTime=startTime;
      this.endTime=endTime;
  }

  public void addAvailability(){
  }

  public void displayAvailability(){
    system.out.println.("Availability Details: ");
    system.out.println.("Date: " + date);
    system.out.println.(" Start Time: " + startTime);
    system.out.println.("End Time: " + endTime);
  }

