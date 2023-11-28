package com.ix8oio8xi.commands;

import java.lang.annotation.*;

/**
 * Annotation for automatic server/client command processing.
 * The annotated class must implement the Processor interface.
 * opCode - unique non-zero byte operation code.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandProcessor {
    byte opCode();
}
