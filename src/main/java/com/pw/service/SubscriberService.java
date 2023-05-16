package com.pw.service;

import com.pw.model.Subscriber;
import com.pw.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class SubscriberService {

    @Autowired
    private SubscriberRepository subscriberRepository;


    public ResponseEntity<HashMap<Object, Object>> subscribeEmail(String email) {
        Subscriber subscriber = Subscriber.builder()
                .email(email)
                .build();

        try {
            subscriberRepository.save(subscriber);
            HashMap<Object, Object> body = new HashMap<>();
            body.put("status", "subscribe_ok");
            return ResponseEntity.status(HttpStatus.OK).body(body);
        } catch (Exception exception) {
            HashMap<Object, Object> body = new HashMap<>();
            body.put("status", "subscribe_failed");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(body);
        }
    }

    public List<Subscriber> getAllSubscribers() {
        return subscriberRepository.findAll();
    }

    public ResponseEntity<HashMap<Object, Object>> deleteSubscriber(Integer id) {
        try {
            subscriberRepository.deleteById(id);
            HashMap<Object, Object> body = new HashMap<>();
            body.put("status", "delete_ok");
            return ResponseEntity.status(HttpStatus.OK).body(body);
        } catch (Exception e) {
            HashMap<Object, Object> body = new HashMap<>();
            body.put("status", "delete_failed");
            return ResponseEntity.status(HttpStatus.OK).body(body);
        }
    }
}
