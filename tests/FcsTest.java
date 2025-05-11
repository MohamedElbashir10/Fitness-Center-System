package tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import db.Admin;
import db.Trainer;
import db.Room;
import db.WorkoutSession;
import db.Schedule;

public class FcsFunctionalTest {

    @Test
    public void testT_SRS_REQ_005_AddAndRemoveExercise() {
        Admin admin = new Admin("1", "Admin User", "admin", "admin123", "Admin");
        Schedule schedule = new Schedule();

        WorkoutSession pilates = new WorkoutSession("Pilates", "Core workout");

        schedule.addWorkoutSession(pilates); // добавление
        assertTrue(schedule.getScheduledSessions().contains(pilates), "Pilates should be added");

        schedule.removeWorkoutSession(pilates); // нужен метод в Schedule
        assertFalse(schedule.getScheduledSessions().contains(pilates), "Pilates should be removed");
    }

    @Test
    public void testT_SRS_REQ_006_AssignRoomAndDetectConflict() {
        Admin admin = new Admin("1", "Admin User", "admin", "admin123", "Admin");
        Room room1 = new Room("Room 1");
        Room room2 = new Room("Room 2");

        WorkoutSession yoga = new WorkoutSession("Yoga", LocalDateTime.of(2025, 4, 17, 10, 0));
        WorkoutSession pilates = new WorkoutSession("Pilates", LocalDateTime.of(2025, 4, 17, 10, 0));
        WorkoutSession spin = new WorkoutSession("Spin", LocalDateTime.of(2025, 4, 17, 14, 0));

        Schedule schedule = new Schedule();

        assertTrue(schedule.scheduleWorkout(admin, yoga, null, room1));
        assertFalse(schedule.scheduleWorkout(admin, pilates, null, room1));
        assertTrue(schedule.scheduleWorkout(admin, spin, null, room2));
    }

    @Test
    public void testT_SRS_REQ_007_AssignTrainerAndDetectConflict() {
        Admin admin = new Admin("1", "Admin User", "admin", "admin123", "Admin");
        Trainer trainer = new Trainer("2", "Jane Smith", "trainer1", "trainer123");

        WorkoutSession yoga = new WorkoutSession("Yoga", LocalDateTime.of(2025, 3, 17, 10, 0));
        WorkoutSession pilates = new WorkoutSession("Pilates", LocalDateTime.of(2025, 3, 17, 10, 0));
        WorkoutSession spin = new WorkoutSession("Spin", LocalDateTime.of(2025, 3, 17, 14, 0));

        Schedule schedule = new Schedule();

        assertTrue(schedule.scheduleWorkout(admin, yoga, trainer, null));
        assertFalse(schedule.scheduleWorkout(admin, pilates, trainer, null));
        assertTrue(schedule.scheduleWorkout(admin, spin, trainer, null));
    }

    @Test
    public void testT_SRS_REQ_008_TrainerViewsAssignedSessions() {
        Trainer trainer = new Trainer("2", "Trainer One", "trainer1", "trainer123");
        WorkoutSession yoga = new WorkoutSession("Yoga", LocalDateTime.of(2025, 4, 20, 9, 0));
        yoga.assignTrainer(trainer);

        Schedule schedule = new Schedule();
        schedule.addWorkoutSession(yoga);

        List<WorkoutSession> assigned = schedule.getSessionsForTrainer(trainer);
        assertEquals(1, assigned.size());
        assertEquals("Yoga", assigned.get(0).getName());
    }
}

