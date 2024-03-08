package org.example;

import java.io.Serializable;

public record Message(int number, String content) implements Serializable {
}
