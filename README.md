# rabbitmq-fan-out-example

## Fan Out

In this [doc](https://www.rabbitmq.com/tutorials/amqp-concepts.html#exchange-fanout) in RabbitMQ's website, it said:
> ### Fanout Exchange
> A fanout exchange routes messages to all of the queues that are bound to it and the routing key is ignored. If N queues are bound to a fanout exchange, when a new message is published to that exchange a copy of the message is delivered to all N queues. Fanout exchanges are ideal for the broadcast routing of messages.

This repo provides an example of fanout in Java client.

## How to Run

1. Run the producer. It will send one message to the "fan out" exchange.  
   You should see two queues `"queue-1"` and `"queue-2"` in the RabbitMQ management page.
2. Run the consumer. It will start two consumers consuming from two different queues `"queue-1"` and `"queue-2"`.
   Both consumer should receive the one piece message from the producer.
3. Run producer multiple times or do further changes to verify it works.


## Producer

In the [producer](https://github.com/6650-ziweiwang/rabbitmq-fan-out-example/blob/main/producer/src/main/java/Main.java), two queues are created and bound to the `fan out` exchange.
```java
// Declare fanout exchange
channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

// Declare two queues and bind them to the fanout exchange
channel.queueDeclare(QUEUE_NAME_1, false, false, false, null);
channel.queueBind(QUEUE_NAME_1, EXCHANGE_NAME, "");
channel.queueDeclare(QUEUE_NAME_2, false, false, false, null);
channel.queueBind(QUEUE_NAME_2, EXCHANGE_NAME, "");
```

In one run of the [main method](https://github.com/6650-ziweiwang/rabbitmq-fan-out-example/blob/main/producer/src/main/java/Main.java), one message is sent to the fan out exchange:
```java
String message = "This message should be fanned out to all bound queues.";
channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
```

## Consumer

In the [consumer](https://github.com/6650-ziweiwang/rabbitmq-fan-out-example/blob/main/consumer/src/main/java/Main.java), two consumers are created to consume from the two queues bound to the `fan-out` exchange:
```java
MQConsumeTask mqConsumer1 = new MQConsumeTask(connection.createChannel(), QUEUE_NAME_1, EXCHANGE_NAME, 1);
new Thread(mqConsumer1).start();

MQConsumeTask mqConsumer2 = new MQConsumeTask(connection.createChannel(), QUEUE_NAME_2, EXCHANGE_NAME, 2);
new Thread(mqConsumer2).start();
```

When the consumer is running, if one message is sent from the producer, both consumers should see the message.
