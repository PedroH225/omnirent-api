package br.com.omnirent.exception.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Component;

import br.com.omnirent.config.i18n.MessageService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class ApiErrorResponseWriter {
	
	private final MessageService messageService;
	
    private final ObjectMapper objectMapper;
    
    private final Clock clock;
    
	public void onApiError(
			HttpServletRequest request, 
			HttpServletResponse response,
			ApiException apiException
			) throws IOException, ServletException {

        String localizedMessage = messageService.get(
                apiException.getMessageKey(),
                apiException.getArgs()
        );

        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(clock),
                apiException.getHttpStatus().value(),
                apiException.getErrorType(),
                apiException.getErrorCode(),
                localizedMessage,
                request.getRequestURI()
        );

        response.setStatus(apiException.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        response.getWriter().write(
                objectMapper.writeValueAsString(body)
        );
	}
}
