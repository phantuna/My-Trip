package org.example.demo_datn.quartz.playground;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/timer")
public class PlaygroundController {

    private final PlayGroundService service;
    @Autowired
    public PlaygroundController(PlayGroundService service) {
        this.service = service;
    }

    @PostMapping("/runHelloWorld")
    public void runHelloWorld() {
        service.runHelloWorldJob();
    }
}
