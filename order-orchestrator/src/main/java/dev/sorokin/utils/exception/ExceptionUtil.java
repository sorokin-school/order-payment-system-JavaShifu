package dev.sorokin.utils.exception;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ExceptionUtil {
    public static String getStackTraceWithMessage(Throwable exception) {
        return exception.getClass() + " : " + exception.getMessage() + "\n" +
                Arrays.stream(exception.getStackTrace())
                        .map(Objects::toString)
                        .collect(Collectors.joining("\n"));
    }
}