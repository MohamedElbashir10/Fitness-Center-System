package tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

public class FcsTest {

    @Test
    public void testT_SRS_REQ_005_AddAndRemoveExercise() {
        Admin admin = new Admin("1", "Admin User", "admin", "admin123", "Admin");
        ExerciseManager manager = new ExerciseManager();

        WorkoutSession pilates = new WorkoutSession("Pilates", "Core workout");

        boolean added = manager.addExercise(admin, pilates);
        assertTrue(added, "Exercise should be added");

        boolean exists = manager.getExercises().contains(pilates);
        assertTrue(exists, "Exercise should appear in the list");

        boolean removed = manager.removeExercise(admin, pilates);
        assertTrue(removed, "Exercise should be removed");

        boolean notExists = manager.getExercises().contains(pilates);
        assertFalse(notExists, "Exercise should not be in the list anymore");
    }

    @Test
    public void testT_SRS_REQ_006_AssignRoomAndDetectConflict() {
        Admin admin = new Admin("1", "Admin User", "admin", "admin123", "Admin");
        Room room1 = new Room("Room 1");
        Room room2 = new Room("Room 2");

        WorkoutSession yoga = new WorkoutSession("Yoga",
                LocalDateTime.of(2025, 4, 17, 10, 0),
                LocalDateTime.of(2025, 4, 17, 11, 0));
        WorkoutSession pilates = new WorkoutSession("Pilates",
                LocalDateTime.of(2025, 4, 17, 10, 0),
                LocalDateTime.of(2025, 4, 17, 11, 0));
        WorkoutSession spin = new WorkoutSession("Spin",
                LocalDateTime.of(2025, 4, 17, 14, 0),
                LocalDateTime.of(2025, 4, 17, 15, 0));

        Schedule schedule = new Schedule();

        assertTrue(schedule.scheduleWorkout(admin, yoga, null, room1));
        assertFalse(schedule.scheduleWorkout(admin, pilates, null, room1), "Room 1 is already booked at this time");
        assertTrue(schedule.scheduleWorkout(admin, spin, null, room2));
    }

    @Test
    public void testT_SRS_REQ_007_AssignTrainerAndDetectConflict() {
        Admin admin = new Admin("1", "Admin User", "admin", "admin123", "Admin");
        Trainer jane = new Trainer("2", "Jane Smith", "trainer1", "trainer123");

        WorkoutSession yoga = new WorkoutSession("Yoga",
                LocalDateTime.of(2025, 3, 17, 10, 0),
                LocalDateTime.of(2025, 3, 17, 11, 0));
        WorkoutSession pilates = new WorkoutSession("Pilates",
                LocalDateTime.of(2025, 3, 17, 10, 0),
                LocalDateTime.of(2025, 3, 17, 11, 0));
        WorkoutSession spin = new WorkoutSession("Spin",
                LocalDateTime.of(2025, 3, 17, 14, 0),
                LocalDateTime.of(2025, 3, 17, 15, 0));

        Schedule schedule = new Schedule();

        assertTrue(schedule.scheduleWorkout(admin, yoga, jane, null));
        assertFalse(schedule.scheduleWorkout(admin, pilates, jane, null), "Trainer already has Yoga at this time");
        assertTrue(schedule.scheduleWorkout(admin, spin, jane, null));
    }

    @Test
    public void testT_SRS_REQ_008_TrainerViewsAssignedSessions() {
        Trainer trainer = new Trainer("2", "Trainer One", "trainer1", "trainer123");
        WorkoutSession session = new WorkoutSession("Yoga",
                LocalDateTime.of(2025, 4, 20, 9, 0),
                LocalDateTime.of(2025, 4, 20, 10, 0));
        session.assignTrainer(trainer);
        Schedule schedule = new Schedule();
        schedule.addWorkoutSession(session);

        List<WorkoutSession> assigned = schedule.getSessionsForTrainer(trainer);

        assertEquals(1, assigned.size(), "Trainer should see 1 assigned session");
        assertEquals("Yoga", assigned.get(0).getName(), "Session name should be Yoga");
    }
}

