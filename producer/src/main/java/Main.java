import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Main {

  private static final String RABBITMQ_IP = "localhost";

  private static final String RMQ_USERNAME = "guest";

  private static final String RMQ_PASSWORD = "guest";

  private static final String QUEUE_NAME_1 = "queue-1";

  private static final String QUEUE_NAME_2 = "queue-2";

  private static final String EXCHANGE_NAME = "fan-out-exchange";

  public static void main(String[] args) throws IOException, TimeoutException {
    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost(RABBITMQ_IP);
    connectionFactory.setUsername(RMQ_USERNAME);
    connectionFactory.setPassword(RMQ_PASSWORD);

    Connection connection = connectionFactory.newConnection();

    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
    channel.queueDeclare(QUEUE_NAME_1, false, false, false, null);
    channel.queueBind(QUEUE_NAME_1, EXCHANGE_NAME, "");
    channel.queueDeclare(QUEUE_NAME_2, false, false, false, null);
    channel.queueBind(QUEUE_NAME_2, EXCHANGE_NAME, "");

    String message = "This message should be fanned out to all bound queues.";
    channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
  }

}
