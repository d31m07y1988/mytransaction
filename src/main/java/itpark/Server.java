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
            Integer clientId = null;
            try (Connection connection = new Connection(socket)) {
                clientId = serverHandshake(connection);
                while (authorized) {
                    String command = connection.receive();
                    if (command == null || command.equalsIgnoreCase("exit")) {
                        connectedClients.remove(clientId);
                        logger.info(clientId + " ушел из магазина");
                        break;
                    } else if (command.equalsIgnoreCase("showall")) {
                        connection.send(outputFormatting(productService.findAll()));
                    } else if (command.startsWith("get ")) {
                        try {
                            int id = Integer.parseInt(command.substring(command.indexOf(' ') + 1));
                            connection.send(productService.get(id).toString());
                        } catch (NumberFormatException e) {
                            connection.send("Не верно введена команда");
                        }
                    } else if (command.startsWith("buy ")) {
                        String[] buyParam = command.split(" ");
                        if (buyParam.length == 3) {
                            try {
                                Product productId = productService.get(Integer.parseInt(buyParam[1]));
                                int boughtProduct = Integer.parseInt(buyParam[2]);
                                productService.buy(productId, customerService.get(clientId), boughtProduct);
                                connection.send("Спасибо за покупку!");
                            } catch (NumberFormatException e) {
                                connection.send("не верно заданы параметры запроса.");
                            } catch (TransactionException e) {
                                connection.send("транзакция не прозведена. " + e.getMessage());
                            }

                        } else {
                            connection.send("параметры запроса введены не верно");
                        }
                    } else if (command.equalsIgnoreCase("showbalance")) {
                        connection.send("Текущий баланс:" + customerService.get(clientId).getBalance());
                    } else if (command.equalsIgnoreCase("help")) {
                        connection.send("Для просмотра ассортимента: showall \n" +
                                "\t Для просмотра 1 товара: get <id> \n" +
                                "\t Для покупки: buy <id> <count> \n" +
                                "\t Показать текущий остаток: showbalance \n" +
                                "\t Показать список команд: help");
                    } else {
                        connection.send("комманда введена не верно. help - для просмотра команд");
                    }
                }
            } catch(SocketException e){
                System.err.println("Связь с клиентом утеряна");
                if (clientId != null)
                    connectedClients.remove(clientId);
            } catch(IOException e){
                e.printStackTrace();
            }

        }

        private int serverHandshake(Connection connection) throws IOException {
            logger.info("Запрос имени");
            String userName = connection.receive();
            Customer authorizedCustomer = null;
            while (userName != null && !userName.equalsIgnoreCase("exit")) {
                authorizedCustomer = customerService.getByName(userName);
                System.out.println("_________________________________");
                System.out.println(authorizedCustomer);
                System.out.println("_________________________________");
                if (authorizedCustomer == null) {
                    connection.send("Такого пользователя не существует");
                    userName = connection.receive();
                    continue;
                } else if (connectedClients.containsKey(authorizedCustomer.getId())) {
                    connection.send("Пользователь с таким именем уже залогинен. Введите другое имя.");
                    userName = connection.receive();
                    continue;
                }
                break;
            }
            if (userName != null && !userName.equalsIgnoreCase("exit")) {
                connectedClients.put(authorizedCustomer.getId(), connection);
                connection.send("Добро пожаловать в магазин! список доступных команд help");
                logger.info("Пользователь авторизован");
            } else {
                authorized = false;
                logger.info("Пользователь не авторизован");
            }
            return authorizedCustomer.getId();
        }
    }

    private static String outputFormatting(List list) {
        StringBuilder allElements = new StringBuilder();
        list.forEach(p -> allElements.append(p).append("\n"));
        return allElements.toString();
    }
}