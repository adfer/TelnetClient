package com.adfer;

import com.adfer.telnet.TelnetClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("telnet")
public class TelnetClientRestController {

  private TelnetClientService service;

  public TelnetClientRestController(TelnetClientService service) {
    this.service = service;
  }

  @RequestMapping(value = "connect")
  public ResponseEntity<String> connect(@RequestParam String hostname, @RequestParam int port) {
    service.connect(hostname, port);
    boolean isConnected = service.isConnected();
    service.disconnect();
    return ResponseEntity.ok(String.valueOf(isConnected));
  }

  @RequestMapping(value = "send", method = RequestMethod.POST)
  public ResponseEntity<String> sendCommand(@RequestParam String hostname, @RequestParam int port, @RequestParam String command) throws IOException {
    service.connect(hostname, port);
    String response = service.send(command);
//    service.disconnect();
    return ResponseEntity.ok(response);
  }
}
