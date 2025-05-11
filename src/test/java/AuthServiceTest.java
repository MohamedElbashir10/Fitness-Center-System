import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private AuthService authService;
    private User testUser;

    @BeforeEach
    public void setUp() {
        authService = new AuthService();
        testUser = new Member("001", "Test User", "elizabeth@example.com", "1234");
        authService.registerUser(testUser);
    }

    @Test
    public void testSuccessfulAuthentication() {
        User result = authService.authenticate("iremarda@example.com", "1234");
        assertNotNull(result);
    }

    @Test
    public void testAuthenticationWithWrongPassword() {
        User result = authService.authenticate("tolgagumusten@example.com", "wrong");
        assertNull(result);
    }
}
