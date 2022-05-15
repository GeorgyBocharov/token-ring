package com.atp.hw4.task2.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class DataPackage {
    @EqualsAndHashCode.Include
    private final UUID id;
    @ToString.Include
    private final String data;

    private long transitionStartTime = 0;

    public DataPackage() {
        data = RandomStringUtils.randomAlphabetic(10);
        id = UUID.randomUUID();
    }

    public void transit() {
        transitionStartTime = System.currentTimeMillis();
    }
}

