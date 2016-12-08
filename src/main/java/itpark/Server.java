package itpark;

import itpark.model.Customer;
import itpark.model.Product;
import itpark.service.CustomerService;
import itpark.service.ProductService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.TransactionException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by Ramil on 30.11.2016.
 */
public class Server {
    public final int serverPort;
    private static Logger logger = Logger.getLogger(Server.class.getSimpleName());
    private Map<Integer, Connection> connectedClients = new ConcurrentHashMap<>();

    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
    CustomerService customerService = applicationContext.getBean("customerService", CustomerService.class);
    ProductService productService = applicationContext.getBean("productService", ProductService.class);


    public Server(int port) {
        serverPort = port;
    }

    public static void main(String[] args) {
        Server server = new Server(13666);
        try (ServerSocket clientListener = new ServerSocket(server.serverPort)) {
            logger.info("Сервер стартовал");
            while (true) {
                Socket socket = clientListener.accept();
                logger.info("Соединение с клиентом установлено");
                incomeClients newClient = server.new incomeClients(socket);
                newClient.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class incomeClients extends Thread {
        private Socket socket;
        private boolean authorized = true;

        public incomeClients(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String clientName = null;
            try (Connection connection = new Connection(socket)) {
                clientName = serverHandshake(connection);
                while (authorized) {
                    String command = connection.receive();
                    if ((command == null || command.equalsIgnoreCase("exit")) {
                        if (command.equalsIgnoreCase("showProducts")) {
                            System.out.println(outputFormatting(customerService.findAll()));
                        } else if (command.startsWith("get(")) {
                            try {
                                int id = Integer.parseInt(command.substring(command.indexOf('(') + 1, command.length() - 1));
                                System.out.println(customerService.get(id));
                            } catch (NumberFormatException e) {
                                System.out.println("Не верно введена команда");
                            }
                        } else if (command.startsWith("transfer ")) {
                            String[] withdrawParametrs = command.split(" ");

                            if (withdrawParametrs.length == 4) {
                                try {
                                    Customer sender = customerService.get(Integer.parseInt(withdrawParametrs[1]));
                                    Customer receiver = customerService.get(Integer.parseInt(withdrawParametrs[2]));
                                    int amount = Integer.parseInt(withdrawParametrs[3]);
                                    customerService.transfer(sender, receiver, amount);
                                    System.out.println("операция произведена успешно");
                                } catch (NumberFormatException e) {
                                    System.out.println("не верно заданы параметры запроса.");
                                } catch (TransactionException e) {
                                    System.out.println("транзакция не прозведена. " + e.getMessage());
                                }
                            } else {
                                System.out.println("параметры запроса введены не верно");
                            }
                        } else {
                            System.out.println("комманда введена не верно. \n Доступно showProducts, get(<id>) и withdraw <id from> <id to> <amount>");
                        }
                    }
                    /*String clientMessage = connection.receive();
                    if (clientMessage==null || clientMessage.equalsIgnoreCase("exit")) {
                        connectedClients.remove(clientName);
                        logger.info(clientName + " покинул чат");
                        break;
                    }*/
                }

            } catch (SocketException e) {
                System.err.println("Связь с клиентом утеряна");
                if(clientName!=null) {
                    connectedClients.remove(clientName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private String serverHandshake(Connection connection) throws IOException {
            logger.info("Запрос имени");
            String userName = connection.receive();
            while (userName!=null) {
                if(customerService.getByName(userName)==null) {
                    connection.send("Такого пользователя не существует");
                } else if(connectedClients.containsKey(customerService.getByName(userName).getId()))
                connection.send("Пользователь с таким именем уже залогинен. Введите другое имя.");
                userName = connection.receive();
            }
            if (userName!=null && !userName.equalsIgnoreCase("exit")) {
                connectedClients.put(customerService.getByName(userName).getId(), connection);
                logger.info("Пользователь авторизован");
            } else {
                authorized = false;
                logger.info("Пользователь не авторизован");
            }
            return userName;
        }
    }

    private static String outputFormatting(List<Customer> customers) {
        StringBuilder allCustomers = new StringBuilder();
        customers.forEach(p -> allCustomers.append(p).append("\n"));
        return allCustomers.toString();
    }
}