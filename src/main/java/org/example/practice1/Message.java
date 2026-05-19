package org.example.practice1;

public record Message(byte uniqueIdentifier,
                      long messageNumber,
                      int commandId,
                      int userId,
                      String messageString) {
}
