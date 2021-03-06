package io.scalecube.distributed.example;

import io.scalecube.Main;
import io.scalecube.rapidoid.http.gateway.ApiRoutes;
import io.scalecube.rapidoid.http.gateway.RapidoidHttpGateway;
import io.scalecube.services.Microservices;
import io.scalecube.services.example.api.GreetingRequest;
import io.scalecube.transport.Address;

import org.rapidoid.setup.On;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class GatewayMain {

  public static void main(String[] args) {

    // run this main() as java application and in the browser call http://localhost:8080/
    internalWebServer();

    // seed node to the cluster on known port 8000.
    Microservices gateway = null;
    if (args.length > 1) {
      gateway = Microservices.builder()
          .seeds(Address.create(args[0], Integer.parseInt(args[1])))
          .build();
      
      System.out.println(gateway.cluster().members());
    } else {
      System.out.println( "please specify the seed ip as arg[0] and seed-ip arg[1] param, ** NOTE: ** if you havent run a seed node yet please run seed node first!.");
      System.out.println( "example: java -cp target/rapidoid-http-gateway-0.9.1-SNAPSHOT.jar io.scalecube.distributed.example.GatewayMain 10.150.4.47 8000");
    }

    // create Rapidoid HTTP gateway and specify the routes to the service(s).
    RapidoidHttpGateway.builder().port(8080)
        .proxy(gateway.dispatcher().create())
        .routes(new ApiRoutes()
            .addRoute("POST", "/hello-world-service/sayHello", "hello-world-service", "sayHello", GreetingRequest.class)
            .addRoute("POST", "/hello-world-service/sayHello-v1", "hello-world-service", "sayHello",
                GreetingRequest.class))
        .build();



  }

  private static void internalWebServer() {
    String content =
        new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/index.html"))).lines()
            .collect(Collectors.joining("\n"));
    On.port(8080).get("/").html(content);
  }

}
