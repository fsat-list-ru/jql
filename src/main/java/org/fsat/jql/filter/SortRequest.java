package org.fsat.jql.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.fsat.jql.helpers.json.deserializers.OrderDeserializer;
import org.fsat.jql.util.Strings;
import org.jongo.Find;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @since Jan 28, 2015
 **/
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SortRequest {
    @JsonDeserialize(using = OrderDeserializer.class)
    public enum Order {
        ASC("1", "asc"), DESC("-1", "desc");

        protected String mongoNotation;
        protected String sqlNotation;

        Order(String mongo, String sql) {
            mongoNotation = mongo;
            sqlNotation = sql;
        }
    }

    protected boolean checked;
    protected List<SortField> fields;

    public static SortRequest fromString(String str) throws IOException {
        SortRequest result = null;

        if (!StringUtils.isEmpty(str)) {

            result = new SortRequest();

            ObjectMapper om = new ObjectMapper();

            Map<String, String> request = om.readValue(str, new TypeReference<Map<String, String>>() {
            });
            List<SortField> fieldList = new ArrayList<>();

            for (String key : request.keySet()) {
                SortField sf = new SortField();
                sf.setName(key);
                sf.setOrder(Order.valueOf(request.get(key).toUpperCase()));
                fieldList.add(sf);
            }

            result.setFields(fieldList);
        }
        return result;
    }

    public void check(List<String> possibleFields) {
        if (!checked) {
            fields = fields.stream().filter(f -> possibleFields.contains(f.getName())).collect(Collectors.toList());
            checked = true;
        }
    }

    public List<SortField> getFields() {
        return fields;
    }

    public void setFields(List<SortField> fields) {
        this.fields = fields;
    }

    public boolean isChecked() {
        return checked;
    }

    // This is for mongo DB
    public Find sort(Find source, List<String> possibleFields) {
        if (fields == null || fields.size() == 0)
            return source;

        for (SortField f : fields) {
            if (!possibleFields.contains(f.getName()))
                throw new IllegalArgumentException(f.getName());
            source = source.sort("{" + f.getName() + ":" + f.getOrder().mongoNotation + "}");
        }
        return source;
    }

    public String getSortSql() {
        String sql = "";

        if (!CollectionUtils.isEmpty(fields)) {
            for (SortField f : fields) {
                if (!StringUtils.isEmpty(sql)) {
                    sql += ",";
                }
                sql += Strings.camelToUnder(f.getName()) + " " + f.getOrder().sqlNotation;
            }
        }

        if (!StringUtils.isEmpty(sql)) {
            sql = " ORDER BY " + sql;
        }

        return sql;
    }

}
