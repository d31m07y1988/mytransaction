package itpark;

import itpark.model.Customer;
import itpark.service.CustomerService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.TransactionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by user on 06.12.16.
 */
public class Main {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        CustomerService customerService = applicationContext.getBean("customerService", CustomerService.class);

        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            String command;
            while ((command = consoleReader.readLine()) != null) {
                if (command.equalsIgnoreCase("findall")) {
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
                    System.out.println("комманда введена не верно. Доступно findall, get(<id>) и withdraw <id from> <id to> <amount>");
                }
            }
        }catch(IOException e){
                e.printStackTrace();
            }
        }

    private static String outputFormatting(List<Customer> customers) {
        StringBuilder allCustomers = new StringBuilder();
        customers.forEach(p -> allCustomers.append(p).append("\n"));
        return allCustomers.toString();
    }
}
