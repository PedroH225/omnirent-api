package br.com.omnirent.config;

import java.lang.reflect.Type;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import br.com.omnirent.common.formatter.SanitizationUtils;
import br.com.omnirent.item.dto.ItemRequestDTO;
import br.com.omnirent.item.dto.UpdateItemRequestDTO;
import br.com.omnirent.security.dto.RegisterDTO;
import br.com.omnirent.user.dto.UserRequestDTO;

@ControllerAdvice(annotations = Controller.class)
public class SanitizationRequestBodyAdvice extends RequestBodyAdviceAdapter {

    @Override
    public boolean supports(
            MethodParameter methodParameter,
            java.lang.reflect.Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) {

        return true;
    }

    @Override
    public Object afterBodyRead(
            Object body,
            HttpInputMessage inputMessage,
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) {

        if (body instanceof RegisterDTO dto) {
            return sanitizeRegisterDTO(dto);
        }
        
        if (body instanceof UserRequestDTO dto) {
            return sanitizeUserRequestDTO(dto);
        }
        
        if (body instanceof ItemRequestDTO dto) {
			return sanitizeItemFields(dto);
		}
        
        if (body instanceof UpdateItemRequestDTO dto) {
			return sanitizeUpdateItemFields(dto);
		}

        return body;
    }

	private RegisterDTO sanitizeRegisterDTO(RegisterDTO registerDTO) {
		return new RegisterDTO(
				SanitizationUtils.name(registerDTO.name()), 
				SanitizationUtils.username(registerDTO.username()),
				SanitizationUtils.email(registerDTO.email()),
				registerDTO.birthDate(), registerDTO.password(), registerDTO.repeatedPassword());
	}
	
	private UserRequestDTO sanitizeUserRequestDTO(UserRequestDTO userDTO) {
		return new UserRequestDTO(
				SanitizationUtils.name(userDTO.name()),
				SanitizationUtils.username(userDTO.username()),
				SanitizationUtils.email(userDTO.email()),
				userDTO.birthDate());
	}
	
	private ItemRequestDTO sanitizeItemFields(ItemRequestDTO itemDTO) {
		return new ItemRequestDTO(
				itemDTO.id(),
				SanitizationUtils.text(itemDTO.name()),
				SanitizationUtils.text(itemDTO.model()),
				SanitizationUtils.text(itemDTO.brand()),
				SanitizationUtils.description(itemDTO.description()),
				itemDTO.basePrice(),
				itemDTO.itemCondition(),
				SanitizationUtils.identifier(itemDTO.subCategoryId()),
				SanitizationUtils.identifier(itemDTO.addressId())
		);
	}
	
	public UpdateItemRequestDTO sanitizeUpdateItemFields(UpdateItemRequestDTO itemDTO) {
		return new UpdateItemRequestDTO(
				SanitizationUtils.identifier(itemDTO.id()),
				SanitizationUtils.text(itemDTO.name()),
				SanitizationUtils.text(itemDTO.model()),
				SanitizationUtils.text(itemDTO.brand()),
				SanitizationUtils.description(itemDTO.description()),
				itemDTO.basePrice(),
				itemDTO.itemCondition()
		);
	}
}