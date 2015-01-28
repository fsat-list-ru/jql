package org.fsat.jql.filter.fields;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fsat.jql.filter.EntityQuery;
import org.fsat.jql.filter.FilterRequest;
import org.fsat.jql.filter.mapper.FilterRequestMapper;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @since Jan 28, 2015
 **/
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonTypeName("group")
public class GroupField extends FilterRequest {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1435977559737800629L;
	
	protected static final Type type=Type.GROUP;
    protected Logical logical;

    @XmlElement(type=FilterRequest.class)
    protected FilterRequest[] fields;

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type t) {
    }

    @Override
    public EntityQuery getQuery(List<String> possibleFields, FilterRequestMapper mapper) {
        List<EntityQuery> subQueries=new ArrayList<>();
        for(FilterRequest r:fields) {
            subQueries.add(r.getQuery(possibleFields, mapper));
        }
        EntityQuery retval=mapper.composeList(subQueries, logical);
        retval.setSort(sort);
        return retval;
    }

    public Logical getLogical() {
        return logical;
    }

    public void setLogical(Logical logical) {
        this.logical = logical;
    }

    public FilterRequest[] getFields() {
        return fields;
    }

    public void setFields(FilterRequest[] fields) {
        this.fields = fields;
    }

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setName(String name) {
		
	}

	@Override
	public List getValue() {
		return null;
	}

	@Override
	public void setValue(List<? extends Object> value) {
		
	}

	@Override
	public Operand getOperand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOperand(Operand o) {
		// TODO Auto-generated method stub
		
	}

}
