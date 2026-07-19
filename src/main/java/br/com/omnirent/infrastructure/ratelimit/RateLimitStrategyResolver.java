package br.com.omnirent.infrastructure.ratelimit;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public final class RateLimitStrategyResolver {

	private static final PathPatternParser PARSER = new PathPatternParser();
	
	private RateLimitStrategyResolver() {}
	
	public static RateLimitStrategy resolve(HttpMethod method, String uri) {
			for (RateLimitStrategy policy : RateLimitStrategy.values()) {
				PathPattern policyPath = PARSER.parse(policy.getUri());
			    if (policy.getMethod() == null || policy.getMethod() == method &&
			    		policyPath.matches(PathContainer.parsePath(uri))) {
			        return policy;
			    }
			}
			return RateLimitStrategy.DEFAULT;
	}
}
