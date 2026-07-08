package com.hansenvillage.hansenapp;

import com.hansenvillage.hansenapp.dto.FamilyRegistrationRequest;
import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.dto.PoolBookingResponse;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.PoolBookingRepository;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.hansenvillage.hansenapp.entity.SessionStatus.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BookingFlowE2ETest extends BaseE2ETest {
    @Autowired
    private PoolSessionRepository poolSessionRepository;

    @Autowired
    private PoolBookingRepository poolBookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @BeforeEach
    void cleanUp() {
        poolBookingRepository.deleteAll();
        poolSessionRepository.deleteAll();
        familyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldExecuteFullUserJourneySuccessfully() {
        String email = "admin@gmail.com";
        String password = "admin";
        String phoneNumber = "+380959595089";

        FamilyRegistrationRequest familyRegistrationRequest = new FamilyRegistrationRequest();
        familyRegistrationRequest.setEmail(email);
        familyRegistrationRequest.setPassword(password);
        familyRegistrationRequest.setAddress("B12K9");
        familyRegistrationRequest.setPhoneNumber(phoneNumber);

        FamilyRegistrationRequest.MemberRequest memberOne =  new FamilyRegistrationRequest.MemberRequest();
        memberOne.setName("Kirill");
        memberOne.setAge(18);

        FamilyRegistrationRequest.MemberRequest memberTwo =  new FamilyRegistrationRequest.MemberRequest();
        memberTwo.setName("Stepan");
        memberTwo.setAge(14);
        familyRegistrationRequest.setMembers(List.of(memberOne,memberTwo));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<FamilyRegistrationRequest> request = new HttpEntity<>(familyRegistrationRequest,headers);

        ResponseEntity<String> response = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/api/auth/register",
                request,
                String.class
        );
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.CREATED);

        loginAs(email, password);

        assertThat(authenticatedHeaders).isNotNull();
        assertThat(authenticatedHeaders.getFirst(HttpHeaders.AUTHORIZATION)).startsWith("Bearer ");

        PoolSession poolSession = new PoolSession();
        poolSession.setSessionDate(LocalDate.now().plusDays(1));
        poolSession.setStartTime(LocalTime.of(10, 0));
        poolSession.setEndTime(LocalTime.of(11, 0));
        poolSession.setBookedCount(0);
        poolSession.setMaxCapacity(40);
        poolSession.setStatus(ACTIVE);
        poolSession = poolSessionRepository.save(poolSession);


        User kirill = userRepository.findAll().stream()
                .filter(u -> "Kirill".equals(u.getName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Kirill not found"));

        PoolBookingRequest bookingRequest = new PoolBookingRequest();
        bookingRequest.setPoolSessionId(poolSession.getId());
        bookingRequest.setUserId(kirill.getId());

        HttpEntity<PoolBookingRequest> bookingHttpEntity = new HttpEntity<>(bookingRequest, authenticatedHeaders);

        ResponseEntity<PoolBookingResponse> bookingResponse = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/api/pool/booking",
                bookingHttpEntity,
                PoolBookingResponse.class
        );

        assertThat(bookingResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(bookingResponse.getBody()).isNotNull();
        UUID bookingId = bookingResponse.getBody().getBookingId();

        PoolSession sessionFromDbAfterBook = poolSessionRepository.findById(poolSession.getId()).orElseThrow();
        assertThat(sessionFromDbAfterBook.getBookedCount()).isEqualTo(1);

        HttpEntity<Void> deleteHttpEntity = new HttpEntity<>(authenticatedHeaders);


        ResponseEntity<Void> deleteResponse = testRestTemplate.exchange(
                "http://localhost:" + port + "/api/pool/booking/" + bookingId,
                HttpMethod.DELETE,
                deleteHttpEntity,
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NO_CONTENT);

        assertThat(poolBookingRepository.findById(bookingId)).isEmpty();
        PoolSession sessionFromDbAfterDelete = poolSessionRepository.findById(poolSession.getId()).orElseThrow();
        assertThat(sessionFromDbAfterDelete.getBookedCount()).isEqualTo(0);

        poolSessionRepository.deleteById(poolSession.getId());
        assertThat(poolSessionRepository.findById(poolSession.getId())).isEmpty();
    }
}
