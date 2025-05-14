package com.lucasvm.animtrackerv2.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Converter
@Component
public class AttributeEncryptor implements AttributeConverter<String, String> {

    // Encriptador de texto configurado via propriedades do Spring
    private final TextEncryptor textEncryptor;

    // Injeta segredo e salt do application.properties
    public AttributeEncryptor(@Value("${app.encryption.secret}") String secret, @Value("${app.encryption.salt}") String salt) {
        this.textEncryptor = Encryptors.text(secret, salt);
    }

    // Converte o atributo para valor encriptado antes de persistir no banco
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        return textEncryptor.encrypt(attribute);
    }

    // Descriptografa o valor do banco para uso na aplicação
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return textEncryptor.decrypt(dbData);
    }
}
