package com.olivar.springbootmongodbdocker;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.SocketUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class MongodbSpringIntegrationTest {
	private static final String CONNECTION_STRING = "mongodb://%s:%d";

	private MongodExecutable mongodExecutable;
	private MongoTemplate mongoTemplate;
	@AfterEach
	void clean() {
		mongodExecutable.stop();
	}

	@BeforeEach
	void setup() throws Exception {
		String ip = "localhost";
		int randomPort = SocketUtils.findAvailableTcpPort();

		ImmutableMongodConfig mongodConfig = MongodConfig
				.builder()
				.version(Version.Main.PRODUCTION)
				.net(new Net(ip, randomPort, Network.localhostIsIPv6()))
				.build();

		MongodStarter starter = MongodStarter.getDefaultInstance();
		mongodExecutable = starter.prepare(mongodConfig);
		mongodExecutable.start();
		mongoTemplate = new MongoTemplate(MongoClients.create(String.format(CONNECTION_STRING, ip, randomPort)),"test");
	}
	@DisplayName("When I request all manufacturer then I obtain a list")
	@Test
	public void getManufacturerTest(@Autowired MongoTemplate mongoTemplate) {

		DBObject objectToSave = BasicDBObjectBuilder.start()
						.add("Ferrari","F01")
								.get();

		mongoTemplate.save(objectToSave, "collection");

		assertThat(mongoTemplate.findAll(DBObject.class, "collection")).extracting("Ferrari")
				.containsOnly("value");
	}

}
