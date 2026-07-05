package br.com.omnirent.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.exception.common.ApiErrorResponse;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.CommonErrorType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class CustomAccessDeniedHandler
        implements AccessDeniedHandler {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex)
            throws IOException {
        ApiException apiException =
                ex.getCause() instanceof ApiException ae
                        ? ae
                        : new ApiException(CommonErrorType.FORBIDDEN);

        String localizedMessage = messageService.get(
                apiException.getMessageKey(),
                apiException.getArgs()
        );

        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                apiException.getHttpStatus().value(),
                apiException.getErrorType(),
                apiException.getErrorCode(),
                localizedMessage,
                request.getRequestURI()
        );

        response.setStatus(apiException.getHttpStatus().value());
        response.setContentType("application/json");

        response.getWriter().write(
                objectMapper.writeValueAsString(body)
        );
    }
}
