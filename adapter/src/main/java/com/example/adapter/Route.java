package com.example.adapter;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class Route extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.auto);

        rest().post("/serviceA").type(MsgA.class).to("direct:serviceA");

        from("direct:serviceA")
                .process(exchange -> {
                    Message in = exchange.getIn();
                    MsgA msgA = in.getBody(MsgA.class);
                    if (msgA.getLng().equals("ru") && !(msgA.getMsg().equals(""))) {
                        in.setBody(msgA.getCoordinates());
                        in.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
                    } else {
                        in.setBody(null);
                        in.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500));
                    }
                    exchange.setMessage(in);
                })
                .log(LoggingLevel.INFO, "${body}")
                .to("direct:weatherService");

        from("direct:weatherService")
                .process(exchange -> {
                    Message in = exchange.getIn();
                    Coordinates body = in.getBody(Coordinates.class);
                    MsgB msgB;
                    if (body == null) {
                        msgB = null;
                        in.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500));
                        in.setBody(msgB);
                    } else {
                        //якобы взяли данные с сервиса погоды без MockRestServiceServer
                        msgB = new MsgB("Привет", "2020-06-10T10:15:30Z", 28);
                        in.setBody(msgB);
                        in.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
                    }
                    exchange.setMessage(in);
                })
                .log(LoggingLevel.INFO, "${body}")
                .to("direct:serviceB");

        from("direct:serviceB").inputType(MsgB.class).setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200)).setBody(() -> null);
    }
}
