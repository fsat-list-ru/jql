package org.fsat.jql.filter;

/**
 * @since Jan 28, 2015
 **/
public class SortField {
    protected SortRequest.Order order;
    protected String name;

    public SortRequest.Order getOrder() {
        return order;
    }

    public void setOrder(SortRequest.Order order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
