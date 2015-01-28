package org.fsat.jql.helpers.json.deserializers;

import java.io.IOException;

import org.fsat.jql.filter.SortRequest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * @since Jan 28, 2015
 **/
public class OrderDeserializer extends JsonDeserializer<SortRequest.Order> {
    @Override
    public SortRequest.Order deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return SortRequest.Order.valueOf(jp.getText().toUpperCase());
    }
}
