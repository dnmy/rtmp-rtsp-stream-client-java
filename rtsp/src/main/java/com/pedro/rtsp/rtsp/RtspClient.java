package com.pedro.rtsp.rtsp;

/**
 * Created by pedro on 10/02/17.
 */

public class RtspClient {

    private String host = "127.0.0.1";
    private int port = 1935;
    private String path = "/live/pedro";

    private int trackVideo = 0;
    private int trackAudio = 1;
    private boolean isUDP = true;
    private int mCSeq = 0;
    private String authorization = null;

    //get on sendAnnounce()
    private String sessionId;

    //TODO socket para conectarse
    //TODO usar respuesta del servidor
    public RtspClient() {
    }

    public RtspClient(String authorization) {
        this.authorization = authorization;
    }

    public void connect() {
        sendAnnounce();
        sendSetup(trackVideo, isUDP);
        sendSetup(trackAudio, isUDP);
        sendRecord();
    }

    public void disconnect() {
        sendTearDown();
    }

    private String sendAnnounce() {
        //TODO body string
        String body = "";   //esto es la informacion de los canales de video y audio (resolucion, samplerate, canal audio, etc)
        String request;
        if (authorization == null) {
            request = "ANNOUNCE rtsp://" + host + ":" + port + path + " RTSP/1.0\r\n" +
                    "CSeq: " + (++mCSeq) + "\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    "Content-Type: application/sdp\r\n\r\n" +
                    body;
        } else {
            request = "ANNOUNCE rtsp://" + host + ":" + port + path + " RTSP/1.0\r\n" +
                    "CSeq: " + (++mCSeq) + "\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    "Authorization: " + authorization + "\r\n" +
                    "Content-Type: application/sdp\r\n\r\n" +
                    body;
        }
        return request;
    }

    private String sendSetup(int track, boolean isUDP) {
        String params = (isUDP) ?
                ("UDP;unicast;client_port=" + (5000 + 2 * track) + "-" + (5000 + 2 * track + 1) + ";mode=receive") :
                ("TCP;interleaved=" + 2 * track + "-" + (2 * track + 1));
        return "SETUP rtsp://" + host + ":" + port + path + "/trackID=" + track + " RTSP/1.0\r\n" +
                "Transport: RTP/AVP/" + params + "\r\n" + addHeaders(authorization);
    }

    private String sendOptions() {
        return "OPTIONS rtsp://" + host + ":" + port + path + " RTSP/1.0\r\n" + addHeaders(authorization);
    }

    private String sendRecord() {
        return "RECORD rtsp://" + host + ":" + port + path + " RTSP/1.0\r\n" + "Range: npt=0.000-\r\n" +
                addHeaders(authorization);
    }

    private String sendTearDown() {
        return "TEARDOWN rtsp://" + host + ":" + port + path + " RTSP/1.0\r\n" + addHeaders(authorization);
    }

    private String addHeaders(String authorization) {
        String header = "CSeq: " + (++mCSeq) + "\r\n" +
                "Content-Length: 0\r\n" +
                "Session: " + sessionId + "\r\n" +
                // For some reason you may have to remove last "\r\n" in the next line to make the RTSP client work with your wowza server :/
                (authorization != null ? "Authorization: " + authorization + "\r\n" : "") + "\r\n";
        return header;
    }
}

