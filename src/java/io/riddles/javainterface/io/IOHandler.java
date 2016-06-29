/*
 * Copyright 2016 riddles.io (developers@riddles.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     For the full copyright and license information, please view the LICENSE
 *     file that was distributed with this source code.
 */

package io.riddles.javainterface.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * io.riddles.javainterface.io.IOHandler - Created on 2-6-16
 *
 * DO NOT EDIT THIS FILE
 *
 * Handles all communication from and to the game wrapper
 * (so also to the bots as well)
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class IOHandler {
    protected final static Logger LOGGER = Logger.getLogger(IOHandler.class.getName());
    private Scanner scanner;
    protected BufferedReader reader;

    public IOHandler() {
        this.scanner = new Scanner(System.in);
    }

    // used for debugging only
    public IOHandler(String inputFile) {
        try {
            InputStream fis = new FileInputStream(inputFile);
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            this.reader = new BufferedReader(isr);
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
        }
    }

    /**
     * Get next line from one of the two input streams
     * @return The next line
     * @throws IOException
     */
    public String getNextMessage() throws IOException {
        if (this.reader != null)
            return getNextMessageFromFile();

        return getNextMessageFromInStream();
    }

    /**
     * Waits until expected message is read, all
     * messages received while waiting for expected message
     * are ignored.
     * @param expected Message that is waited on
     */
    public void waitForMessage(String expected) {
        String message = null;

        while (!expected.equals(message)) {
            try {
                message = getNextMessage();
                try { Thread.sleep(2); } catch (InterruptedException ignored) {}
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, ex.toString(), ex);
                break;
            }
        }
    }

    /**
     * Send a message to the game wrapper
     * @param message Message to send
     */
    public void sendMessage(String message) {
        System.out.println(message);
        System.out.flush();
    }

    /**
     * Send a message that will be received by all bots,
     * no response from bots is expected
     * @param message Message to send
     */
    public void broadcastMessage(String message) {
        sendMessage(String.format("bot all send %s", message));
    }

    /**
     * Get next message from given file
     * @return The next line in the file
     * @throws IOException
     */
    private String getNextMessageFromFile() throws IOException {
        String line = reader.readLine();

        if (line != null) {
            LOGGER.info(line);
            return line;
        }

        throw new IOException("No more input.");
    }

    /**
     * Get next message from game wrapper
     * @return The received message
     * @throws IOException
     */
    private String getNextMessageFromInStream() throws IOException {
        if (this.scanner.hasNextLine()) {
            String line = "";
            while (line.length() == 0) {
                line = this.scanner.nextLine().trim();
            }
            return line;
        }

        throw new IOException("No more input.");
    }
}