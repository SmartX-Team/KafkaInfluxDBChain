package chainlinker;
import java.util.Properties;
import java.util.Arrays;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ChainLinker {
	private static final Logger logger = LogManager.getLogger(ChainLinker.class);

	public static void main(String[] args) {
		logger.info("Starting...");

		// Loading configurations from config file. More details are in ConfigLoader class.
		logger.debug("Loading config file...");
		ConfigLoader config = ConfigLoader.getInstance();
		if (config == null) {
			logger.fatal("Error during loading config file.");
			return;
		}

		// Setting up a KafkaConsumer instance
		logger.debug("Setting up a KafkaConsumer instance...");
		ConfigLoader.KafkaConfig kafkaConf = config.getKafkaConfig();	   
		String topicName = kafkaConf.getTopicName();
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaConf.getBootstrapServers());
		props.put("group.id", kafkaConf.getGroupID());
		props.put("enable.auto.commit", kafkaConf.getAutoCommit());
		props.put("auto.commit.interval.ms", kafkaConf.getAutoCommitIntervalMS());
		props.put("session.timeout.ms", kafkaConf.getSessionTimeoutMS());
		props.put("key.deserializer", kafkaConf.getKeyDeserializer());
		props.put("value.deserializer", kafkaConf.getValueDeserializer());
		KafkaConsumer<String, String> consumer = new KafkaConsumer
				<String, String>(props);

		// Reserving graceful shutdown
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				// When tested on Eclipse, this will not be executed.
				// But still on console this will work.
				logger.info("Commencing shutdown...");
				consumer.close();
				logger.debug("Kafka consumer instance is closed.");
				logger.debug("Shutdown complete.");
			}
		});

		// Kafka Consumer subscribes list of topics here.
		// TODO: What about multiple topics?
		consumer.subscribe(Arrays.asList(topicName));

		// print the topic name
		logger.debug("Subscribed to topic '" + topicName + "'");

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records) {
				logger.debug("MSG. Offset = " + record.offset());
				JSONParser parser = new JSONParser();

				SnapParser dataParser = new SnapParser();
				String value = record.value();
				try {
					dataParser.processMessage((JSONArray)parser.parse(value));
				} catch (ParseException e) {
					logger.error("Failed to parse given message. Is it correctly encoded?", e);
					logger.debug("Parser failed full message : " + value);
					break;
				}
			}

		}

	}
}
