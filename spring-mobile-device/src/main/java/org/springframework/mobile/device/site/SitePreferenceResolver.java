/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.mobile.device.site;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.mvc.DeviceResolverHandlerInterceptor;
import org.springframework.web.context.request.RequestAttributes;

/**
 * A helper that resolves the user's site preference and makes it available as a request attribute.
 * Primarily used in support of the case where a user on a mobile device prefers to use the normal site.
 * The site preference may be changed on behalf of a user by submitting the 'site_preference' query parameter.
 * The preference value is saved in a repository so it can be remembered until the user decides to change it again.
 * If no site preference is specified, preference to the mobile site will be given if the current device is a mobile device.
 * The current user SitePreference is exported as a request attribute with the name {@link #CURRENT_SITE_PREFERENCE_ATTRIBUTE}.
 * This allows handler mappings and view resolvers further down the line to vary their logic by site preference.
 * @author Keith Donald
 */
public class SitePreferenceResolver {

	public static final String CURRENT_SITE_PREFERENCE_ATTRIBUTE = "currentSitePreference";

	private final SitePreferenceRepository sitePreferenceRepository;

	/**
	 * Creates a new site preference interceptor.
	 * @param sitePreferenceRepository the store for recording user site preference
	 */
	public SitePreferenceResolver(SitePreferenceRepository sitePreferenceRepository) {
		this.sitePreferenceRepository = sitePreferenceRepository;
	}

	public SitePreference resolveSitePreference(HttpServletRequest request, HttpServletResponse response) {
		SitePreference preference = getSitePreferenceQueryParameter(request);
		if (preference != null) {
			sitePreferenceRepository.saveSitePreference(preference, request, response);
		} else {
			preference = sitePreferenceRepository.loadSitePreference(request);
		}
		if (preference == null) {
			preference = getDefaultSitePreferenceForDevice(DeviceResolverHandlerInterceptor.getCurrentDevice(request));
		}
		if (preference != null) {
			request.setAttribute(CURRENT_SITE_PREFERENCE_ATTRIBUTE, preference);
		}
		return preference;
	}

	// static factory methods

	/**
	 * Get the current site preference for the user that originated this web request.
	 */
	public static SitePreference getCurrentSitePreference(HttpServletRequest request) {
		return (SitePreference) request.getAttribute(CURRENT_SITE_PREFERENCE_ATTRIBUTE);
	}

	/**
	 * Get the current site preference for the user from the request attributes map.
	 */
	public static SitePreference getCurrentSitePreference(RequestAttributes attributes) {
		return (SitePreference) attributes.getAttribute(CURRENT_SITE_PREFERENCE_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
	}
	
	// internal helpers
	
	private SitePreference getSitePreferenceQueryParameter(HttpServletRequest request) {
		String string = request.getParameter(SITE_PREFERENCE_PARAMETER);
		return string != null && string.length() > 0 ? SitePreference.valueOf(string.toUpperCase()) : null;
	}

	private SitePreference getDefaultSitePreferenceForDevice(Device device) {
		if (device == null) {
			return null;
		}
		return device.isMobile() ? SitePreference.MOBILE : SitePreference.NORMAL;
	}
	
	private static final String SITE_PREFERENCE_PARAMETER = "site_preference";
	
}