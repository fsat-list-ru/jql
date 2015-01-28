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
public class LogicalDeserializer extends JsonDeserializer<FilterRequest.Logical> {
    @Override
    public FilterRequest.Logical deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return FilterRequest.Logical.valueOf(FilterRequest.Logical.class, jp.getText().toUpperCase());
    }
}
