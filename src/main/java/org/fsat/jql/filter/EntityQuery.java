package org.fsat.jql.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

/**
 * @since Jan 28, 2015
 **/
public class EntityQuery {
	
	protected final Logger log = LogManager.getLogger(getClass());

	protected SortRequest sort;
    protected String query;
    protected List parameters;

    public EntityQuery() {}

    public EntityQuery(String query, List parameters) {
        this.query = query;
        this.parameters = parameters;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List getParameters() {
    	if(parameters==null) {
    		parameters=new ArrayList();
    	}
        return parameters;
    }

    public void setParameters(List parameters) {
        this.parameters = parameters;
    }
    
    public String getPlainSql() {
		String retval = String.format(query.replace("%", "%%").replace("?", "%s"), getEscapedValues(parameters));
		if (!StringUtils.isEmpty(retval)) {
			retval = " AND " + retval;
		}

		if (null != sort) {
			if (null == retval) {
				retval = "";
			}
			retval = retval + " " + sort.getSortSql();
		}

		log.debug("Using plain sql: {}", retval);
		return retval;
    }
    
    protected String[] getEscapedValues(List params) {
    	if(params==null) {
    		return new String[0];
    	}
    	Iterator it=params.iterator();
    	String[] retval=new String[params.size()];
    	int i=0;
    	while(it.hasNext()) {
    		Object param=it.next();
    		if(param!=null) {
	    		if(param instanceof Number) {
	    			retval[i]=param.toString();
	    		} else if (param instanceof Date) {
	    			Date d=(Date)param;
	    			retval[i]="to_timestamp("+d.getTime()/1000+")";
	    		} else if (param instanceof String) {
					retval[i] = "E'" + param.toString().replace("'", "\\'") + "'";
				} else if (param instanceof Boolean) {
					retval[i]=param.toString();
				} else { // TODO: Strange case
	    			// Maybe blob?
	    		}
    		}
    		i++;
    	}
    	return retval;
    }

	public SortRequest getSort() {
		return sort;
	}

	public void setSort(SortRequest sort) {
		this.sort = sort;
	}

}
