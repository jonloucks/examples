package io.github.jonloucks.examples.basic.contracts;

import java.time.Duration;

interface MyProgram {
    Duration getUptime();
    
    void runCommand(String[] args);
}
