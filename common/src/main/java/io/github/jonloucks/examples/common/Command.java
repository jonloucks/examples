package io.github.jonloucks.examples.common;

import java.util.List;

public interface Command {
    String execute(List<String> arguments);
    
    String getName();
}
