package org.edward.pandora.event_bus;

import org.edward.pandora.common.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class EventController {
    @Autowired
    private IEventService eventService;

    @PostMapping("/register")
    public Response register(@RequestParam("app") String app) throws Exception {
        this.eventService.register(app);
        return Response.ok();
    }

    @PostMapping("/subscribe")
    public Response subscribe(
            @RequestParam("app") String app,
            @RequestParam("event_name") String eventName
    ) throws Exception {
        this.eventService.subscribe(app, eventName);
        return Response.ok();
    }
}