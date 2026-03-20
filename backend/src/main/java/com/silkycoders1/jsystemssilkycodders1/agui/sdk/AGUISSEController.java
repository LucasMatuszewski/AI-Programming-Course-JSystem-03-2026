package com.silkycoders1.jsystemssilkycodders1.agui.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.agui.core.event.BaseEvent;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class AGUISSEController {

    private final LoanCopilotAgentService agentService;
    private final ObjectMapper objectMapper;

    public AGUISSEController(LoanCopilotAgentService agentService, ObjectMapper objectMapper) {
        this.agentService = agentService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/sse/{agentId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> runAgent(
            @PathVariable("agentId") String agentId,
            @RequestHeader(value = "Accept", required = false) String accept,
            @RequestBody AGUIParameters parameters
    ) {
        var body = agentService.run(parameters.toRunAgentParameters())
                .map(this::serializeSseEvent);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(body);
    }

    private String serializeSseEvent(BaseEvent event) {
        try {
            return "data: %s\n\n".formatted(objectMapper.writeValueAsString(event));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to serialize AG-UI event", exception);
        }
    }
}
