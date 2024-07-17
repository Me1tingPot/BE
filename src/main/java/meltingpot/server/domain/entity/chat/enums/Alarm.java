package meltingpot.server.domain.entity.chat.enums;

public enum Alarm {
    ON, OFF;
    public Alarm toggle() {
        return this == ON ? OFF : ON;
    }
}
