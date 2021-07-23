package com.example.adapter;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

public class SimpleMockTest extends CamelTestSupport {

    @Test
    public void testFromDirectRestServiceAToMockServiceA() throws Exception {


        getMockEndpoint("mock:serviceA").expectedBodiesReceived("{ \n" +
                " \"msg\": \"Привет\", \n" +
                " \"lng\": \"ru\", \n" +
                " \"coordinates\": { \n" +
                " \"latitude\": \"54.35\", \n" +
                " \"longitude\": \"52.52\" \n" +
                " } \n" +
                " }");

        template.sendBody("direct:restServiceA", "{ \n" +
                " \"msg\": \"Привет\", \n" +
                " \"lng\": \"ru\", \n" +
                " \"coordinates\": { \n" +
                " \"latitude\": \"54.35\", \n" +
                " \"longitude\": \"52.52\" \n" +
                " } \n" +
                " }");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testFromDirectServiceAToMockWeatherService() throws Exception {

        getMockEndpoint("mock:weatherService").expectedBodiesReceived("{\n" +
                "    \"coordinates\": {\n" +
                "        \"latitude\": \"54.35\",\n" +
                "        \"longitude\": \"52.52\"\n" +
                "    }\n" +
                "}");

        template.sendBody("direct:serviceA", "{\n" +
                "    \"coordinates\": {\n" +
                "        \"latitude\": \"54.35\",\n" +
                "        \"longitude\": \"52.52\"\n" +
                "    }\n" +
                "}");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testFromDirectWeatherServiceToMockServiceB() throws Exception {

        getMockEndpoint("mock:serviceB").expectedBodiesReceived("{\n" +
                "    \"txt\": \"Привет\",\n" +
                "    \"createdDt\": \"2020-06-10T10:15:30Z\",\n" +
                "    \"currentTemp\": \"28\"\n" +
                "}");

        template.sendBody("direct:weatherService", "{\n" +
                "    \"txt\": \"Привет\",\n" +
                "    \"createdDt\": \"2020-06-10T10:15:30Z\",\n" +
                "    \"currentTemp\": \"28\"\n" +
                "}");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:restServiceA").to("mock:serviceA");

                from("direct:serviceA").to("mock:weatherService");

                from("direct:weatherService").to("mock:serviceB");
            }
        };
    }
}