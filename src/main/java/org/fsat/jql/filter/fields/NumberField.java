package org.fsat.jql.filter.fields;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.fsat.jql.filter.EntityQuery;
import org.fsat.jql.filter.FilterRequest;
import org.fsat.jql.filter.mapper.FilterRequestMapper;
import org.fsat.jql.util.Strings;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @since Jan 28, 2015
 **/
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonTypeName("number")
public class NumberField extends FilterRequest {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5567681756801695578L;
	
	protected final Type type=Type.NUMBER;
    protected Operand operand;
    protected List<Number> value;
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
        if(!possibleFields.contains(name)) {
            throw new IllegalArgumentException(name);
        }
        EntityQuery retval=new EntityQuery();
        if(value==null || value.size()==0 || value.get(0) == null) {
        	retval.setQuery(Strings.camelToUnder(name) + mapper.convertNull(operand, 0));
        } else {
	        retval.setQuery(Strings.camelToUnder(name) + mapper.convert(type, operand, value.size()));
	        retval.setParameters(new ArrayList());
	        retval.getParameters().addAll(value);
        }
        retval.setSort(sort);
        return retval;
    }

    public Operand getOperand() {
        return operand;
    }

    public void setOperand(Operand operand) {
        this.operand = operand;
    }

    public List<Number> getValue() {
        return value;
    }

    public void setValue(List value) {
    	this.value=new ArrayList<>();
    	for(Object o:value) {
    		if(o instanceof Number) {
    			this.value.add((Number)o);
    		} else {
    			this.value.add(new BigInteger(o.toString()));
    		}
    	}
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Strings.dotToCamel(name);
    }
}
