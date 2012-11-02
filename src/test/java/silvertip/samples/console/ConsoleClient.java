/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package silvertip.samples.console;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;

import silvertip.CommandLine;
import silvertip.Connection;
import silvertip.Events;
import silvertip.samples.pingpong.PingPongMessageParser;

public class ConsoleClient {
  public static void main(String[] args) throws IOException {
    String hostname = "localhost";
    int port = 4444;

    final Events events = Events.open();

    final Connection<String> connection = Connection.connect(new InetSocketAddress(hostname, port),
        new PingPongMessageParser(), new Connection.Callback<String>() {
          @Override public void connected(Connection<String> connection) {
          }

          @Override public void messages(Connection<String> connection, Iterator<String> messages) {
            while (messages.hasNext()) {
              String m = messages.next();
              if ("GBAI\n".equals(m)) {
                connection.send("GBAI\n".getBytes());
                events.stop();
              }
            }
          }

          @Override public void idle(Connection<String> connection) {
            System.out.println("Idle detected.");
          }

          @Override public void closed(Connection<String> connection) {
          }

          @Override public void garbledMessage(Connection<String> connection, String message, byte[] data) {
          }
        });
    final CommandLine commandLine = CommandLine.open(new CommandLine.Callback() {
      @Override public void commandLine(String commandLine) {
        connection.send((commandLine + "\n").getBytes());
      }
    });

    events.register(commandLine);
    events.register(connection);
    events.dispatch(30 * 1000);
  }
}