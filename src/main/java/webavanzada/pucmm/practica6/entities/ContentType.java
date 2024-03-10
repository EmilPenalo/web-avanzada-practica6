package webavanzada.pucmm.practica6.entities;

import lombok.Getter;

@Getter
public enum ContentType {
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    TEXT_XML("text/xml"),
    TEXT_JSON("text/json"),
    TEXT_PLAIN("text/plain"),
    OTHER("other");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }
}
