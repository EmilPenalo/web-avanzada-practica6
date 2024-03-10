package webavanzada.pucmm.practica6.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import webavanzada.pucmm.practica6.entities.Mockup;
import webavanzada.pucmm.practica6.entities.User;
import webavanzada.pucmm.practica6.services.AuthService;
import webavanzada.pucmm.practica6.services.JwtService;
import webavanzada.pucmm.practica6.services.MockupService;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Controller
@AllArgsConstructor
@RequestMapping(path = "/access")
public class AccessController {

    private final MockupService mockupService;
    private final AuthService authService;
    private final JwtService jwtService;

    @RequestMapping(
            value = "/{project_id}/{project_path}/{mockup_id}/{mockup_path}",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS}
    )
    public ResponseEntity<String> doMockupRequest(
            @PathVariable Long project_id,
            @PathVariable String project_path,
            @PathVariable Long mockup_id,
            @PathVariable String mockup_path,
            HttpServletRequest httpServletRequest
    ) {
        Optional<Mockup> optionalMockup = mockupService.getMockupByIdForEndpoint(mockup_id);

        if (optionalMockup.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Mockup mockup = optionalMockup.get();

        if (mockup.isJwt_validation()) {
            Optional<User> loggedUser = authService.getLoggedUser();

            if (loggedUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user authenticated");
            }

            if (jwtService.isTokenValid(mockup.getToken(), loggedUser.get())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no longer valid");
            }

            String tokenUsername = jwtService.getUsernameFromToken(mockup.getToken());
            String loggedUsername = loggedUser.get().getUsername();

            if (!Objects.equals(loggedUsername, tokenUsername)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The authenticated user doesnt match with the token associated with the mockup");
            }
        }

        if (!Objects.equals(mockup.getMethod().toString(), httpServletRequest.getMethod())) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        Instant now = Instant.now();
        Instant creationTime = mockup.getCreationTime();
        Instant expirationTime = creationTime.plusSeconds(mockup.getExpirationTime().getSeconds());

        if (now.isAfter(expirationTime)) {
            return ResponseEntity.status(HttpStatus.GONE).build();
        }

        int delay = mockup.getDelay();

        CompletableFuture<ResponseEntity<String>> futureResponse = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            int responseCode = mockup.getCode();
            String responseBody = mockup.getBody();

            HttpHeaders headers = getHttpHeaders(mockup);

            String contentType = mockup.getContent_type().getValue();
            if (contentType != null && !contentType.isEmpty()) {
                MediaType mediaType = MediaType.parseMediaType(contentType);
                headers.setContentType(mediaType);
            }

            return ResponseEntity.status(HttpStatus.valueOf(responseCode))
                    .headers(headers)
                    .body(responseBody);
        });

        return futureResponse.join();
    }

    private static HttpHeaders getHttpHeaders(Mockup mockup) {
        HttpHeaders headers = new HttpHeaders();
        String headersString = mockup.getHeaders();

        if (headersString != null && !headersString.isEmpty()) {
            JSONObject headersJson = new JSONObject(headersString);

            for (String key : headersJson.keySet()) {
                headers.add(key, headersJson.getString(key));
            }
        }
        return headers;
    }
}
