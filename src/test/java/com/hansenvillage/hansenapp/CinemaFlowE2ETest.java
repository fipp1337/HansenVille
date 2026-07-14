//package com.hansenvillage.hansenapp;
//
//import com.hansenvillage.hansenapp.dto.*;
//import com.hansenvillage.hansenapp.entity.*;
//import com.hansenvillage.hansenapp.repository.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.*;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//import java.util.UUID;
//
//import static com.hansenvillage.hansenapp.entity.SessionStatus.ACTIVE;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//public class CinemaFlowE2ETest extends BaseE2ETest {
//
//    @Autowired
//    private CinemaSessionRepository cinemaSessionRepository;
//
//    @Autowired
//    private CinemaBookingRepository cinemaBookingRepository;
//
//    @Autowired
//    private CinemaSeatRepository cinemaSeatRepository;
//
//    @Autowired
//    CinemaHallRepository cinemaHallRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private FamilyRepository familyRepository;
//
//    @BeforeEach
//    void cleanUp() {
//        cinemaBookingRepository.deleteAll();
//        cinemaSeatRepository.deleteAll();
//        cinemaHallRepository.deleteAll();
//        cinemaSessionRepository.deleteAll();
//        familyRepository.deleteAll();
//        userRepository.deleteAll();
//    }
//
//    @Test
//    void shouldExecuteFullUserJourneySuccessfully() {
//        String email = "test@gmail.com";
//        String password = "tes123";
//
//        FamilyRegistrationRequest familyRegistrationRequest = new FamilyRegistrationRequest();
//        familyRegistrationRequest.setEmail(email);
//        familyRegistrationRequest.setPassword(password);
//        familyRegistrationRequest.setAddress("B12K9");
//
//        FamilyRegistrationRequest.MemberRequest memberOne = new FamilyRegistrationRequest.MemberRequest();
//        memberOne.setName("John Doe");
//        memberOne.setAge(18);
//
//        FamilyRegistrationRequest.MemberRequest memberTwo = new FamilyRegistrationRequest.MemberRequest();
//        memberTwo.setName("Jane Doe");
//        memberTwo.setAge(20);
//
//        familyRegistrationRequest.setMembers(List.of(memberOne, memberTwo));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<FamilyRegistrationRequest> request = new HttpEntity<>(familyRegistrationRequest, headers);
//
//        ResponseEntity<String> response = testRestTemplate.postForEntity(
//                "http://localhost:" + port + "/api/auth/register",
//                request,
//                String.class
//        );
//        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.CREATED);
//
//        loginAs(email, password);
//
//        assertThat(authenticatedHeaders).isNotNull();
//        assertThat(authenticatedHeaders.getFirst(HttpHeaders.AUTHORIZATION)).startsWith("Bearer ");
//
//        CinemaSession session = new CinemaSession();
//        session.setMovieName("Inside Out 2");
//        session.setStartTime(LocalTime.of(10, 0));
//        session.setDuration(150);
//        session.setMaxCapacity(36);
//        session.setStatus(ACTIVE);
//        session.setSessionDate(LocalDate.of(2026, 7, 6));
//        session = cinemaSessionRepository.save(session);
//
//        CinemaHall hall = new CinemaHall();
//        hall.setName("Домашній зал");
//        hall = cinemaHallRepository.save(hall);
//
//        CinemaSeat seat = new CinemaSeat();
//        seat.setHallId(hall.getId());
//        seat.setSofaNumber("A1");
//        seat = cinemaSeatRepository.save(seat);
//
//        User johnDoe = userRepository.findAll().stream()
//                .filter(u -> "John Doe".equals(u.getName()))
//                .findFirst()
//                .orElseThrow(() -> new AssertionError("John Doe not found"));
//
//        CinemaBookingRequest bookingRequest = new CinemaBookingRequest();
//        bookingRequest.setUserId(johnDoe.getId());
//        bookingRequest.setCinemaSessionId(session.getId());
//        bookingRequest.setSeatId(seat.getId());
//
//        HttpEntity<CinemaBookingRequest> bookingHttpEntity = new HttpEntity<>(bookingRequest, authenticatedHeaders);
//
//        ResponseEntity<CinemaBookingResponse> bookingResponse = testRestTemplate.postForEntity(
//                "http://localhost:" + port + "/api/cinema/booking",
//                bookingHttpEntity,
//                CinemaBookingResponse.class
//        );
//
//        assertThat(bookingResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(bookingResponse.getBody()).isNotNull();
//        UUID bookingId = bookingResponse.getBody().getBookingId();
//
//        CinemaSession sessionFromDbAfterBook = cinemaSessionRepository
//                .findById(session.getId())
//                .orElseThrow();
//        assertThat(sessionFromDbAfterBook
//                .getBookedCount())
//                .isEqualTo(1);
//
//        HttpEntity<Void> deleteHttpEntity = new HttpEntity<>(authenticatedHeaders);
//
//        ResponseEntity<Void> deleteResponse = testRestTemplate.exchange(
//                "http://localhost:" + port + "/api/cinema/booking/" + bookingId,
//                HttpMethod.DELETE,
//                deleteHttpEntity,
//                Void.class
//        );
//
//        assertThat(deleteResponse.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NO_CONTENT);
//
//        assertThat(cinemaBookingRepository.findById(bookingId)).isEmpty();
//        CinemaSession sessionFromDbAfterDelete = cinemaSessionRepository.findById(session.getId()).orElseThrow();
//        assertThat(sessionFromDbAfterDelete.getBookedCount()).isEqualTo(0);
//    }
//}
