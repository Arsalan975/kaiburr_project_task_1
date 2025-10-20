package com.example.taskrunner.service;

import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class CommandValidator {
  private static final Set<String> BLOCKLIST = Set.of(
      ";", "&&", "||", "|", "`", "$(", ")", ">", "<",
      "rm", "sudo", "chmod", "chown", "mkfs", "dd", "kill", "reboot", "shutdown", "mount", "umount"
  );
  private static final Pattern CONTROL_CHARS = Pattern.compile("[\\p{Cntrl}&&[^\\n\\t]]");

  public void validateOrThrow(String command) {
    if (command == null || command.isBlank())
      throw new IllegalArgumentException("Command must not be empty.");
    if (CONTROL_CHARS.matcher(command).find())
      throw new IllegalArgumentException("Command contains control characters.");
    var lower = command.toLowerCase();
    for (String bad : BLOCKLIST) {
      if (lower.contains(bad)) {
        throw new IllegalArgumentException("Command contains disallowed token: " + bad);
      }
    }
  }
}
