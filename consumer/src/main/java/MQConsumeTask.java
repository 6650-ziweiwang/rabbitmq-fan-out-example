import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MQConsumeTask implements Runnable {

  private final Integer id;
  private final Channel channel;
  private final String queueName;
  private final String exchangeName;

  public MQConsumeTask(Channel channel, String queueName, String exchangeName, Integer id) {
    this.channel = channel;
    this.queueName = queueName;
    this.exchangeName = exchangeName;
    this.id = id;
  }

  @Override
  public void run() {
    String consumerTag = String.format("Consumer-%d consuming queue \"%s\"", id, queueName);
    System.out.format("[%s] Awaiting...\n", consumerTag);

    try {
      channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);
      channel.queueBind(queueName, exchangeName, "");
      channel.basicConsume(queueName, false, consumerTag,
          new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                Envelope envelope,
                AMQP.BasicProperties properties,
                byte[] body)
                throws IOException
            {
              String routingKey = envelope.getRoutingKey();
              String contentType = properties.getContentType();
              long deliveryTag = envelope.getDeliveryTag();

              // (process the message components here ...)
              String message = new String(body, StandardCharsets.UTF_8);
              System.out.format("[%s] Received message: %s\n", consumerTag, message);
              channel.basicAck(deliveryTag, false);
            }
          });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
