package org.ceil.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class CeilEvent {
    public enum Connection {
        CONNECTED,
        DISCONNECTED
    }

    private Timestamp time;
    private Connection status;
    private String ceilReference;
    ;
}
