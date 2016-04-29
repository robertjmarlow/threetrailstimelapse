package com.marlowsoft.threetrailstimelapse.mock;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.CharSource;
import com.google.common.io.Files;

import fi.iki.elonen.NanoHTTPD;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Always serves up an image or webpage from the resources directory.
 */
public class FakeWebServer extends NanoHTTPD {
    private static String RESOURCE_ROOT = "src/test/resources";

    public FakeWebServer(final int port) throws IOException {
        super(port);

        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(final IHTTPSession session) {
        Response response = null;
        final String uri = session.getUri();
        try {
            if (uri.endsWith(".png")) {
                response = newChunkedResponse(Response.Status.OK, "image/png",
                        new FileInputStream(RESOURCE_ROOT + uri));
            } else {
                final CharSource source = Files.asCharSource(new File(RESOURCE_ROOT + uri), Charsets.UTF_8);
                Joiner joiner = Joiner.on("");
                response = newFixedLengthResponse(joiner.join(source.readLines()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
