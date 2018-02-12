package com.gd.cps.ecom.http;

import org.apache.cxf.continuations.Continuation;
import org.apache.cxf.continuations.ContinuationProvider;
import org.apache.cxf.jaxrs.ext.MessageContext;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;


public class HttpService {

    private static final int ASYNCH_CALL_TIMEOUT = 30000; // milliseconds

    @Context
    private MessageContext context;

    @POST
    @Path("provision")
    public Response provision(final String json) {

        final ContinuationProvider continuationProvider = (ContinuationProvider)
            context.get(ContinuationProvider.class.getName());
        final Continuation continuation = continuationProvider.getContinuation();

        synchronized (continuation) {

            if (continuation.isNew()) {
                continuation.setObject(null);
                continuation.suspend(ASYNCH_CALL_TIMEOUT);

                return null;
            } else {
                return Response.status(Response.Status.OK).build();
            }
        }
    }
}
