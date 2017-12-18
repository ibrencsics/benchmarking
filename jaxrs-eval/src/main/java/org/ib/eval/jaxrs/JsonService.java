package org.ib.eval.jaxrs;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class JsonService {

    private static final Logger logger = LogManager.getLogger(JsonService.class);

    @Path("streaming")
    @POST
    public Response streaming(String json) {

        logger.debug(json);
        try {
            parseJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok().build();
    }

    private void parseJson(String json) throws IOException {
        JsonFactory jfactory = new JsonFactory();
        JsonParser jParser = jfactory.createParser(json);

        String parsedName = null;
        Integer parsedAge = null;

        List<String> addresses = new LinkedList<>();

        while (jParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = jParser.getCurrentName();
            if ("name".equals(fieldname)) {
                jParser.nextToken();
                parsedName = jParser.getText();
            }

            if ("age".equals(fieldname)) {
                jParser.nextToken();
                parsedAge = jParser.getIntValue();
            }

            if ("address".equals(fieldname)) {
                jParser.nextToken();
                while (jParser.nextToken() != JsonToken.END_ARRAY) {
                    addresses.add(jParser.getText());
                }
            }
        }
        jParser.close();

        logger.debug("age {}", parsedAge);
        logger.debug(parsedName);
    }
}

// curl -X POST http://localhost:9080/streaming -d '{"name":"sdf", "age":12, "cucc":"ddff"}'