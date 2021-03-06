/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.config.aggregate;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.cloud.stream.aggregate.AggregateApplicationBuilder;
import org.springframework.cloud.stream.binder.BinderFactory;
import org.springframework.cloud.stream.config.aggregate.processor.TestProcessor;
import org.springframework.cloud.stream.config.aggregate.source.TestSource;
import org.springframework.cloud.stream.test.binder.TestSupportBinder;
import org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.notNullValue;

/**
 * @author Ilayaperumal Gopinathan
 * @author Oleg Zhurakousky
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AggregateApplicationTests {

	@Before
	public void before() {
		System.setProperty("server.port", "0");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testAggregateApplication() throws Exception {
		ConfigurableApplicationContext context = new AggregateApplicationBuilder(
				TestSupportBinderAutoConfiguration.class).from(TestSource.class).to(TestProcessor.class).run();
		TestSupportBinder testSupportBinder = (TestSupportBinder) context.getBean(BinderFactory.class).getBinder(null,
				MessageChannel.class);
		MessageChannel processorOutput = testSupportBinder.getChannelForName("output");
		Message<String> received = (Message<String>) (testSupportBinder.messageCollector().forChannel(processorOutput)
				.poll(5, TimeUnit.SECONDS));
		Assert.assertThat(received, notNullValue());
		Assert.assertTrue(received.getPayload().endsWith("processed"));
	}
}
