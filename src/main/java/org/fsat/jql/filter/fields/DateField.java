package org.fsat.jql.filter.fields;

import java.util.ArrayList;
import java.util.Date;
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
@JsonTypeName("date")
public class DateField extends FilterRequest {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3389743086386119820L;

	protected final static Type type=Type.DATE;

    protected Operand operand;

    protected List<Date> value;

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
	        retval.setQuery(Strings.camelToUnder(name)  + mapper.convert(type, operand, value.size()));
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

    @Override
    public List<Date> getValue() {
        return value;
    }

    public void setValue(List value) {
    	this.value=new ArrayList<>();
    	for(Object o:value) {
    		if(o instanceof Date) {
    			this.value.add((Date)o);
    		} else if (o instanceof Number) {
    			Date val=new Date();
    			val.setTime(((Number)o).longValue());
    			this.value.add(val);
    		}
    	}
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
