package org.fsat.jql.helpers.json.deserializers;

import java.io.IOException;

import org.fsat.jql.filter.FilterRequest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * @since Jan 28, 2015
 **/
public class TypeDeserializer extends JsonDeserializer<FilterRequest.Type> {
    @Override
    public FilterRequest.Type deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return FilterRequest.Type.valueOf(FilterRequest.Type.class, jp.getText().toUpperCase());
    }
}
