package com.payline.payment.ideal.service.impl;

import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.IgnoreNotificationResponse;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void parse() {
        NotificationResponse response = service.parse(null);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(IgnoreNotificationResponse.class, response.getClass());
    }

    @Test
    void notifyTransactionStatus() {
        Assertions.assertDoesNotThrow(()-> service.notifyTransactionStatus(Mockito.mock(NotifyTransactionStatusRequest.class)));
    }
}