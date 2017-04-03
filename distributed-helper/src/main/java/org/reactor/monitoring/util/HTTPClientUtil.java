package org.reactor.monitoring.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.InputStreamResponseListener;
import org.reactor.monitoring.common.ApplicationConfig;
import org.reactor.monitoring.common.HTTPType;
import org.reactor.monitoring.common.JSONElement;
import org.reactor.monitoring.common.ServiceType;
import org.reactor.monitoring.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPClientUtil {
	private final static Logger LOGGER = LoggerFactory.getLogger(HTTPClientUtil.class);

	private static final Map<ServiceType, HttpClient> CLIENT_MAP = new ConcurrentHashMap<>();
	private static final Map<ServiceType, String> REQUEST_URLS = new ConcurrentHashMap<>();

	public static void init(final Map<String, String> config, final ServiceType type) {

		String enabledApps = (String) config.get(ApplicationConfig.WHITE_LIST_APPLICATIONS);
		if (enabledApps == null || StringUtils.isEmpty(enabledApps)) {
			LOGGER.warn("============================= Applications are not enabled =========================");
		}
		try {
			HttpClient client = new HttpClient();
			client.setConnectBlocking(false);
			client.setMaxConnectionsPerDestination(50); // max 200 concurrent
														// connections to every
														// address
			// client.setThreadPool(new QueuedThreadPool(250)); // max 250
			// threads
			client.setConnectTimeout(30000); // 30 seconds timeout; if no server
												// reply, the request expires
			client.setIdleTimeout(1000 * 60 * 5);
			CLIENT_MAP.put(type, client);
			client.start();
		} catch (Throwable ex) {
			LOGGER.error("********************** Error while start HTTP Client **********************"
					+ ApplicationException.getStackTrace(ex));
		}

	}

	public static void setURL(final ServiceType type, final String url) {
		REQUEST_URLS.put(type, url);
	}

	public static JSONElement callService(final Map<String, String> config, final ServiceType type,
			final HTTPType httpType, final String path, final Map<String, String> headers)
			throws InterruptedException, ExecutionException, TimeoutException {
		String url = REQUEST_URLS.get(type);
		HttpClient client = CLIENT_MAP.get(type);

		int kiloBytes = 1000;
		final JSONElement element = new JSONElement();

		switch (httpType) {
		case GET:
			ContentResponse response = client.GET(url + (StringUtils.isEmpty(path) ? "" : path));
			element.setElement(JSONUtils.convertToJSON(response.getContent()));
			// response.getContentAsString();
			break;
		case OPTIONS:
			break;
		case PUT:
			break;
		case DELETE:
			break;
		default:
			// default POST
			Request request = client.POST(url + (StringUtils.isEmpty(path) ? "" : path));
			if(headers != null && !headers.isEmpty()){
				Set<Entry<String, String>> keyValues = headers.entrySet();
				for(Entry<String, String> entry : keyValues){
					request.getHeaders().add(entry.getKey(), entry.getValue());
				}
			}
			
			request.send(new InputStreamResponseListener(kiloBytes * 1024L) {
				@Override
				public void onComplete(Result result) {
					super.onComplete(result);
					if (!result.isFailed()) {
						InputStream stream = this.getInputStream();
						element.setElement(JSONUtils.convertToJSON(stream));
					}
				}
			});
			break;

		}
		
		System.out.println(element.getElement().toString());

		return element;

	}

	public static void main(String... arg) throws InterruptedException, ExecutionException, TimeoutException {

		init(new HashMap<String, String>(), ServiceType.SYNTHETIC);
		setURL(ServiceType.SYNTHETIC, "http://localhost:8080/");
		callService(new HashMap<String, String>(), ServiceType.SYNTHETIC, HTTPType.GET, "admin.testlocation/all",java.util.Collections.singletonMap("Content-Type", "application/json"));
	}
}
