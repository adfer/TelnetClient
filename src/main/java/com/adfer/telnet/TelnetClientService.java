package com.adfer.telnet;

import com.google.common.base.Preconditions;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

@Service
public class TelnetClientService {

  private final static Logger LOGGER = LoggerFactory.getLogger(TelnetClientService.class);

  private static final String prompt = "\n";

  private TelnetClient tc;
  private InputStream in = null;
  private PrintStream out = null;

  public TelnetClientService() {
    setTelnetClient(new TelnetClient());
  }

  void setTelnetClient(TelnetClient telnetClient) {
    this.tc = telnetClient;
  }

  public boolean connect(String hostname, int port) {
    Preconditions.checkNotNull(tc, "Telnet client must not be null");
    try {
      if (!tc.isConnected()) {
        tc.connect(hostname, port);
        // Get input and output stream references
        in = tc.getInputStream();
        out = new PrintStream(tc.getOutputStream());
        readUntil("\r\n\r\n.");
      }
    } catch (IOException e) {
      LOGGER.error("Error while connecting to {} on port {}", hostname, port);
      LOGGER.error(e.getMessage(), e);
      return false;
    }
    return tc.isConnected();
  }

  public boolean isConnected() {
    return tc.isConnected();
  }

  public void disconnect() {
    try {
      tc.disconnect();
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  /**
   * Sends the command string to the CAI system.
   */
  public String send(String command) {
    String result = null;
    try {
      write(command);
      readUntil("\n"); // read past echo
      result = readUntil(prompt);
      // drop trailing '\n'
      result = result.substring(0, result.length() - 1);
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
    return result;
  }

  /**
   * Reads input stream until the given pattern is reached. The
   * pattern is discarded and what was read up until the pattern is
   * returned.
   */
  public String readUntilStartsWith(String pattern) throws IOException {
    char lastChar = pattern.charAt(pattern.length() - 1);
    StringBuilder sb = new StringBuilder();
    int c;

    while ((c = in.read()) != -1) {
      char ch = (char) c;
      System.out.print(ch);
      sb.append(ch);
      if (ch == lastChar) {
        String str = sb.toString();
        if (str.startsWith(pattern)) {
          return str.substring(0, str.length() -
              pattern.length());
        }
      }
    }

    return null;
  }


  /**
   * Reads input stream until the given pattern is reached. The
   * pattern is discarded and what was read up until the pattern is
   * returned.
   */
  public String readUntil(String pattern) throws IOException {
    char lastChar = pattern.charAt(pattern.length() - 1);
    StringBuilder sb = new StringBuilder();
    int c;

    while ((c = in.read()) != -1) {
      char ch = (char) c;
      System.out.print(ch);
      sb.append(ch);
      if (ch == lastChar) {
        String str = sb.toString();
        if (str.endsWith(pattern)) {
          return str.substring(0, str.length() -
              pattern.length());
        }
      }
    }

    return null;
  }

  /**
   * Writes the value to the output stream.
   */
  private void write(String value) {
    out.println(value);
    out.flush();
    //System.out.println(value);
  }

}
