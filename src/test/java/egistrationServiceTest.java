import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RegistrationServiceTest {

    private RegistrationService registrationService;

    @BeforeEach
    public void setUp() {
        registrationService = new RegistrationService();
    }

    @Test
    public void testSuccessfulRegistration() {
        User user = registrationService.registerUser("U001", "Emily Johnson", "emily.johnson@gmail.com", "securePass123", "member");
        assertNotNull(user);
        assertEquals("Emily Johnson", user.getName());
        assertEquals("member", user.getRole());
    }

    @Test
    public void testDuplicateEmailRegistrationFails() {
        registrationService.registerUser("U002", "Michael Brown", "michael.brown@fitnessapp.com", "trainerPass1", "trainer");
        User second = registrationService.registerUser("U003", "Sophia Green", "michael.brown@fitnessapp.com", "anotherPass", "member");
        assertNull(second); // same email should not be allowed twice
    }

    @Test
    public void testInvalidEmailFormat() {
        User user = registrationService.registerUser("U004", "Daniel Lee", "daniel.lee[at]gmail", "basic123", "member");
        assertNull(user); // invalid email format
    }

    @Test
    public void testInvalidRoleRejected() {
        User user = registrationService.registerUser("U005", "Olivia Smith", "olivia.smith@outlook.com", "pass456", "manager");
        assertNull(user); // role "manager" is not allowed
    }

    @Test
    public void testEmptyFieldsRejected() {
        User user = registrationService.registerUser(null, "", "", "", "member");
        assertNull(user); // empty fields should fail
    }
}
