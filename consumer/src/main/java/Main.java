import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Main {

  private static final String RABBITMQ_IP = "localhost";

  private static final String RMQ_USERNAME = "guest";

  private static final String RMQ_PASSWORD = "guest";

  private static final String QUEUE_NAME_1 = "queue-1";

  private static final String QUEUE_NAME_2 = "queue-2";

  private static final String EXCHANGE_NAME = "fan-out-exchange";

  public static void main(String[] args) throws Exception {
    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost(RABBITMQ_IP);
    connectionFactory.setUsername(RMQ_USERNAME);
    connectionFactory.setPassword(RMQ_PASSWORD);

    Connection connection = connectionFactory.newConnection();

    MQConsumeTask mqConsumer1 = new MQConsumeTask(connection.createChannel(), QUEUE_NAME_1, EXCHANGE_NAME, 1);
    new Thread(mqConsumer1).start();

    MQConsumeTask mqConsumer2 = new MQConsumeTask(connection.createChannel(), QUEUE_NAME_2, EXCHANGE_NAME, 2);
    new Thread(mqConsumer2).start();
  }

}
