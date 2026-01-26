package com.eclept.andjelazoric_eclept_be_labflow.notification;


import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TestNotificationDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyHospitalTestCompleted(TestRequest testRequest) {
        TestNotificationDTO dto = TestNotificationDTO.builder()
                .testRequestId(testRequest.getId())
                .testType(testRequest.getTestType().getName())
                .completedAt(testRequest.getCompletedAt())
                .status(testRequest.getStatus())
                .build();
        log.info("Sending WS notification for completed test {}", testRequest.getId());

        messagingTemplate.convertAndSend(
                "/topic/tests/" + testRequest.getId(),
                dto
        );
        log.info(
                "WebSocket notification sent. destination=/topic/tests/{}, payload={}",
                testRequest.getId(),
                dto
        );
    }

}
