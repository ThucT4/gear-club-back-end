package com.pw.controller;

import com.pw.model.Subscriber;
import com.pw.service.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("api/subscriber")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SubscriberController {

    @Autowired
    SubscriberService subscriberService;

    @PostMapping(value = "/{email}")
    public ResponseEntity<HashMap<Object, Object>> subscribeEmail(@PathVariable(name = "email") String email) {
        return subscriberService.subscribeEmail(email);
    }

    @GetMapping(value = "/all")
    public List<Subscriber> getAllSubscribers() {
        return subscriberService.getAllSubscribers();
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<HashMap<Object, Object>> deleteSubscriber(@PathVariable(name = "id") Integer id) {
        return subscriberService.deleteSubscriber(id);
    }
}
