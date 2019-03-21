/*
 * Copyright 2010-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.mobile.device.annotation;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DevicePlatform;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roy Clarkson
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = DefaultDeviceResolverConfiguration.class)
public class DefaultDeviceResolverAnnotationTest {

	@Autowired
	ApplicationContext context;

	@Test
	public void deviceResolverHandlerInterceptorCreated() {
		assertThat(this.context.getBean("deviceResolverHandlerInterceptor")).isNotNull();
	}

	@Test
	public void deviceHandlerMethodArgumentResolverCreated() {
		assertThat(this.context.getBean("deviceHandlerMethodArgumentResolver"))
				.isNotNull();
	}

	@Test
	public void resolveDevice() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		DeviceResolverHandlerInterceptor interceptor = (DeviceResolverHandlerInterceptor) this.context
				.getBean("deviceResolverHandlerInterceptor");
		interceptor.preHandle(request, response, null);
		Device device = DeviceUtils.getCurrentDevice(request);
		assertThat(device.isNormal()).isTrue();
		assertThat(device.isMobile()).isFalse();
		assertThat(device.isTablet()).isFalse();
		assertThat(device.getDevicePlatform()).isEqualByComparingTo(DevicePlatform.UNKNOWN);
	}

}
