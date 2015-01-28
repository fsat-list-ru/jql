package org.fsat.jql.filter.fields;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.fsat.jql.filter.EntityQuery;
import org.fsat.jql.filter.FilterRequest;
import org.fsat.jql.filter.mapper.FilterRequestMapper;
import org.fsat.jql.util.Strings;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @since Jan 28, 2015
 **/
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonTypeName("boolean")
public class BooleanField extends FilterRequest {

    private static final long serialVersionUID = -4894549435574511945L;

    protected final Type type = Type.BOOLEAN;
    protected Operand operand;
    protected List<Boolean> value;
    protected String name;


    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type t) {

    }

    @SuppressWarnings("unchecked")
	@Override
    public EntityQuery getQuery(List<String> possibleFields, FilterRequestMapper mapper) {
        if (!possibleFields.contains(name)) {
            throw new IllegalArgumentException(name);
        }

        if (operand != Operand.EQ && operand != Operand.NE) {
            throw new IllegalArgumentException(operand.name());
        }

        EntityQuery query = new EntityQuery();
        if (CollectionUtils.isEmpty(value)) {
            query.setQuery(Strings.camelToUnder(name) + mapper.convertNull(operand, 0));
        } else {
            query.setQuery(Strings.camelToUnder(name) + mapper.convert(type, operand, value.size()));
            query.setParameters(new ArrayList());
            query.getParameters().addAll(value);
        }
        query.setSort(sort);
        return query;
    }

    public Operand getOperand() {
        return operand;
    }

    public void setOperand(Operand operand) {
        this.operand = operand;
    }

    @Override
    public List<Boolean> getValue() {
        return value;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void setValue(List value) {
        this.value = (List<Boolean>)value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
