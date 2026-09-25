package co.icesi.buscaminas;

import co.icesi.buscaminas.controllers.TCPController;
import co.icesi.buscaminas.services.ServicesImpl;

public class Main {

    public static void main(String[] args) {

        ServicesImpl serv = new ServicesImpl();

        int port = args.length > 0
                ? Integer.parseInt(args[0])
                : 12345;

        TCPController controller = new TCPController(serv, port);

        controller.startService();
    }
}