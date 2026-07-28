package client;

import com.google.gson.Gson;
import websocket.messages.*;
import websocket.commands.*;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WsCommunicator extends Endpoint{

    Session session;


    public WsCommunicator(String serverURL) {
    }
}
