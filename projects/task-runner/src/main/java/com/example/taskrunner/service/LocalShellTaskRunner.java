package com.example.taskrunner.service;

import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class LocalShellTaskRunner implements TaskRunner {
  @Override
  public String run(String command) throws Exception {
    String[] cmd = isWindows()
        ? new String[]{"cmd.exe", "/c", command}
        : new String[]{"/bin/sh", "-c", command};

    Process process = new ProcessBuilder(cmd).redirectErrorStream(true).start();

    StringBuilder out = new StringBuilder();
    try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = r.readLine()) != null) out.append(line).append("\n");
    }
    int exit = process.waitFor();
    if (exit != 0) out.append("(exit code ").append(exit).append(")\n");
    return out.toString().trim();
  }

  private static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }
}
