package com.searchservice.app.config.aspect;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class CidFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		if (req instanceof HttpServletRequest) {

			HttpServletRequest request = (HttpServletRequest) req;
			String requestCid = request.getHeader("CID");

			if (requestCid == null) {
				requestCid = generateUniqueCorrelationId();
			}

			MDC.put("CID", requestCid);
		}

		try {
			// call filter(s) upstream for the real processing of the request
			chain.doFilter(req, res);
		} finally {
			// it's important to always clean the cid from the MDC,
			// this Thread goes to the pool but it's loglines would still contain the cid.
			MDC.remove("CID");
		}

	}

	@Override
	public void destroy() {
		// nothing
	}

	@Override
	public void init(FilterConfig fc) throws ServletException {
		// nothing
	}

	public static String generateUniqueCorrelationId() {
		return UUID.randomUUID().toString();
	}

}
