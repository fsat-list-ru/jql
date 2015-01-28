package org.fsat.jql.filter.mapper;

import java.util.ArrayList;
import java.util.List;

import org.fsat.jql.filter.EntityQuery;
import org.fsat.jql.filter.FilterRequest;
import org.fsat.jql.filter.FilterRequest.Operand;

/**
 * @since Jan 28, 2015
 **/
public class SqlMapper implements FilterRequestMapper {

    @Override
    public String convert(FilterRequest.Type t, FilterRequest.Operand o, int attributeCount) {
        switch(t) {
            case STRING:
                return convertString(o, attributeCount);
            case BOOLEAN:
                return convertBoolean(o, attributeCount);
            default:
                return convertNonString(o, attributeCount);
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public EntityQuery composeList(List<EntityQuery> subQueries,  FilterRequest.Logical logical) {
    	EntityQuery retval=new EntityQuery();
    	StringBuilder sb=new StringBuilder("(");
    	List parameters=new ArrayList();
    	int i=0;
    	for(EntityQuery sub:subQueries) {
    		if(i++>0) sb.append(" " + logical.getSqlNotation() + " ");
    		sb.append(sub.getQuery());
    		parameters.addAll(sub.getParameters());
    	}
    	sb.append(")");
    	retval.setQuery(sb.toString());
    	retval.setParameters(parameters);
        return retval;
    }

    protected String convertNonString(FilterRequest.Operand o, int attributeCount) {
        if ((o == Operand.BETWEEN && attributeCount != 2)
                || (o != Operand.IN && attributeCount > 1)) {
            throw new IllegalArgumentException(o.toString());
        }
        String query="";
        switch(o) {
            case BETWEEN:
                query=" between ? and ? ";
                break;
            case LT:
                query = " < ? ";
                break;
            case GT:
                query = " > ? ";
                break;
            case EQ:
                query = " = ? ";
                break;
            case GTE:
                query = " >= ? ";
                break;
            case LTE:
                query= " <= ? ";
                break;
            case NE:
                query = " <> ? ";
                break;
            case IN:
                query = " in (";
                for(int i=0;i<attributeCount;i++) query+=i>0?",?":"?";
                query += ")";
                break;
            default:
                throw new IllegalArgumentException(o.name());
        }
        return query;
    }

    protected String convertString(FilterRequest.Operand o, int attributeCount) {
        String query;
        switch(o) {
            case NC:
                query = " not like ? ";
                break;
            case EQ:
                query = " = ? ";
                break;
            case IN:
                query = " in (";
                for(int i=0;i<attributeCount;i++) query+=i>0?",?":"?";
                query += ")";
                break;
            default:
                query = " ilike ? ";
                break;
        }
        return query;
    }

    protected String convertBoolean(FilterRequest.Operand o, int attributeCount) {

        if (attributeCount != 1) {
            throw new IllegalArgumentException(o.name());
        }

        String query;
        switch (o) {
            case NE:
                query = " is not ? ";
                break;
            case EQ:
                query = " is ? ";
                break;
            default:
                throw new IllegalArgumentException(o.name());
        }
        return query;
    }

	@Override
	public String wrapValue(String s, Operand operand) {
		if(operand == Operand.NC || operand == Operand.CONTAINS) {
			return "%"+s+"%";
		}
		
		return s;
	}

	@Override
	public String convertNull(Operand o, int attributeCount) {
		String query="";
		switch(o) {
		case EQ:
			query=" is null ";
			break;
		case NE:
			query=" is not null ";
			break;
		default:
			throw new IllegalArgumentException(o.toString());
		}
		return query;
	}
}
