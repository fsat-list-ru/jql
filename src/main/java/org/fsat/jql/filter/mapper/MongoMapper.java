package org.fsat.jql.filter.mapper;

import java.util.ArrayList;
import java.util.List;

import org.fsat.jql.filter.EntityQuery;
import org.fsat.jql.filter.FilterRequest;
import org.fsat.jql.filter.FilterRequest.Operand;

/**
 * @since Jan 28, 2015
 **/
public class MongoMapper implements FilterRequestMapper {

    @Override
    public String convert(FilterRequest.Type t, FilterRequest.Operand o, int attributeCount) {
        switch(t) {
            case STRING:
                return convertString(o, attributeCount);
            default:
                return convertNonString(o, attributeCount);
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public EntityQuery composeList(List<EntityQuery> subQueries, FilterRequest.Logical logical) {
        int i=0;
        StringBuilder query=new StringBuilder();
        String ending;
        if(logical == FilterRequest.Logical.NOT) {
            if(subQueries.size()>1) {
                throw new IllegalArgumentException(logical.name());
            }
            query.append("{"+logical.getMongoNotation() + ":");
            ending="}";
        } else {
            if(subQueries.size()<2) {
                throw new IllegalArgumentException(logical.name());
            }
            query.append("{" + logical.getMongoNotation() + ":[");
            ending="]}";
        }
        List<Object> params=new ArrayList<>();
        for(EntityQuery q:subQueries) {
            if(i++ > 0) query.append(",");
            query.append("{" + q.getQuery() + "}");
            params.addAll(q.getParameters());
        }
        query.append(ending);
        EntityQuery retval=new EntityQuery();
        retval.setQuery(query.toString());
        retval.setParameters(params);
        return retval;
    }

    protected String convertNonString(FilterRequest.Operand o, int attributeCount) {
        String query="";
        switch(o) {
            case BETWEEN:
                query = " : {$gte:#,$lt:#} ";
                break;
            case LT:
                query = " : {$lt:#}";
                break;
            case GT:
                query = " : {$gt:#}";
                break;
            case EQ:
                query = " : #";
                break;
            case GTE:
                query = " : {$gte:#}";
                break;
            case LTE:
                query = " : {$lte:#}";
                break;
            case NE:
                query = " : {$ne:#}";
                break;
            case IN:
                query = "{$in:[";
                for(int i=0;i<attributeCount;i++) query+=i>0?",#":"#";
                query += "]}";
                break;
            default:
                throw new IllegalArgumentException(o.name());
        }
        return query;
    }

    protected String convertString(FilterRequest.Operand o, int attributeCount) {
        String query="";
        switch(o) {
            case NC:
                query = " : {$not: {$regex: #, $options: 'i'}}";
                break;
            case EQ:
                query = " : #";
                break;
            case IN:
                query = "{$in:[";
                for(int i=0;i<attributeCount;i++) query+=i>0?",#":"#";
                query += "]}";
                break;
            default:
                query = " : {$regex: #, $options: 'i'}";
                break;
        }
        return query;
    }

	@Override
	public String wrapValue(String s, Operand operand) {
		if(operand == Operand.NC || operand == Operand.CONTAINS) {
			return ".*"+s+".*";
		}
		
		return s;
	}

	@Override
	public String convertNull(Operand o, int attributeCount) {
		// TODO Auto-generated method stub
		return null;
	}


}
