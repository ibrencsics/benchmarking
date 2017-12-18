package org.ib.eval.jaxrs;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

public class JaxrsServer {

    public static void main(String[] args) {
        startServer();
    }

    private static void startServer() {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setServiceBean(new JsonService());
        sf.setAddress("http://localhost:9080/");
        sf.create();
    }
}
