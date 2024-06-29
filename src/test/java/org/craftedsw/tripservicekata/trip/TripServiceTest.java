package org.craftedsw.tripservicekata.trip;

import org.craftedsw.tripservicekata.exception.UserNotLoggedInException;
import org.craftedsw.tripservicekata.user.User;
import org.craftedsw.tripservicekata.factories.UserBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TripServiceTest {

    private static final User GUEST_USER = null;
    private static final User ANY_USER = new User();
    private static final User REGISTERED_USER = new User();
    private static final User ANOTHER_USER = new User();
    private static final Trip LONDON = new Trip();
    private static final Trip BARCELONA = new Trip();
    private User loggedInUser;
    private TripService tripService;

    @BeforeAll
    public void setUp() {
        tripService = new TestableTripService();
    }

    @Test
    void validate_the_logged_in_user() {
        loggedInUser = GUEST_USER;
        assertThrows(UserNotLoggedInException.class, () -> tripService.getTripsByUser(ANY_USER));
    }

    @Test
    public void return_no_trips_when_users_are_not_frieds() {
        loggedInUser = REGISTERED_USER;

        User stranger = UserBuilder.aUser()
                .friendsWith(ANOTHER_USER)
                .withTripsTo(LONDON)
                .build();

        List<Trip> trips = tripService.getTripsByUser(stranger);

        assertEquals(0, trips.size());
    }

    @Test
    public void return_trips_when_users_are_friends() {
        loggedInUser = REGISTERED_USER;

        User friend = UserBuilder.aUser()
                .friendsWith(ANOTHER_USER, REGISTERED_USER)
                .withTripsTo(LONDON, BARCELONA)
                .build();

        List<Trip> trips = tripService.getTripsByUser(friend);

        assertEquals(2, trips.size());
        assertTrue(trips.contains(LONDON));
        assertTrue(trips.contains(BARCELONA));
    }

    private class TestableTripService extends TripService {
        @Override
        User loggedInUser() {
            return loggedInUser;
        }

        @Override
        List<Trip> tripsBy(User user) {
            return user.trips();
        }
    }
}
