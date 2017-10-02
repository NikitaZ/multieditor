package org.multieditor.rest.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class MultieditorJSONMapper implements ContextResolver<ObjectMapper> {

    private ObjectMapper objectMapper;

    public MultieditorJSONMapper() {
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(objectMapper.getTypeFactory());
        this.objectMapper.setAnnotationIntrospector(introspector);
    }

    public MultieditorJSONMapper(SerializationFeature... serializationFeatures) {
        this();
        for (SerializationFeature f : serializationFeatures) {
            objectMapper.enable(f);
        }
    }

    @Override
    public ObjectMapper getContext(Class<?> objectType) {
        return objectMapper;
    }

    public <T> T readValue(String value, Class<T> valueType) throws IOException {
        return objectMapper.readValue(value, valueType);
    }

    public <T> T readValue(File src, Class<T> valueType) throws IOException {
        return objectMapper.readValue(src, valueType);
    }

    public <T> List<T> readList(String value, Class<T> valueType) throws IOException {
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, valueType);
        List<T> list = objectMapper.readValue(value, collectionType);
        return list;
    }

    public <T> List<T> readList(File src, Class<T> valueType) throws IOException {
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, valueType);
        List<T> list = objectMapper.readValue(src, collectionType);
        return list;
    }


    public String writeValueAsString(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }


    public void writeValue(File resultFile, Object value) throws IOException {
        objectMapper.writeValue(resultFile, value);
    }

}