package tum.ret.rity.minor.consent.domain;

import lombok.SneakyThrows;

public enum IdentifierTypeEnum {
    ID, PASSPORT;

    @SneakyThrows
    public static IdentifierTypeEnum fromString(String text) {
        return IdentifierTypeEnum.valueOf(text);
    }
}
