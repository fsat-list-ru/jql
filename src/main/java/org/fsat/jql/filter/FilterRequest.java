package org.fsat.jql.filter;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.beanutils.PropertyUtils;
import org.fsat.jql.filter.fields.BooleanField;
import org.fsat.jql.filter.fields.DateField;
import org.fsat.jql.filter.fields.GroupField;
import org.fsat.jql.filter.fields.NumberField;
import org.fsat.jql.filter.fields.StringField;
import org.fsat.jql.filter.mapper.FilterRequestMapper;
import org.fsat.jql.filter.mapper.SqlMapper;
import org.fsat.jql.helpers.json.deserializers.LogicalDeserializer;
import org.fsat.jql.helpers.json.deserializers.OperandDeserializer;
import org.fsat.jql.helpers.json.deserializers.TypeDeserializer;
import org.fsat.jql.provider.EntityProvider;
import org.fsat.jql.util.Strings;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @since Jan 28, 2015
 **/
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(value = { @JsonSubTypes.Type(value = DateField.class, name = "date"),
		@JsonSubTypes.Type(value = StringField.class, name = "string"),
		@JsonSubTypes.Type(value = NumberField.class, name = "number"),
		@JsonSubTypes.Type(value = GroupField.class, name = "group"),
		@JsonSubTypes.Type(value = BooleanField.class, name = "boolean") })
public abstract class FilterRequest {
	protected int hash;

	/**
     *
     */
	private static final long serialVersionUID = -5019294734830817713L;

	@JsonIgnore
	protected SortRequest sort;
	
	@JsonIgnore
	protected static ObjectMapper om=new ObjectMapper();

	/**
	 * Тип данных.
	 */
	@JsonDeserialize(using = TypeDeserializer.class)
	public enum Type {
		GROUP, DATE, NUMBER, STRING, BOOLEAN
	}

	/**
	 * Оператор
	 */
	@JsonDeserialize(using = OperandDeserializer.class)
	public enum Operand {
		EQ, NE, GT, LT, LTE, GTE, NC, CONTAINS, BETWEEN, IN
	}

	/**
	 * Оператор объединения.
	 */
	@JsonDeserialize(using = LogicalDeserializer.class)
	public enum Logical {
		AND("$and", "and"), OR("$or", "or"), NOT("$not", "not");

		protected String mongoNotation;
		protected String sqlNotation;

		Logical(String mongoNotation, String sqlNotation) {
			this.mongoNotation = mongoNotation;
			this.sqlNotation = sqlNotation;
		}

		public String getMongoNotation() {
			return mongoNotation;
		}

		public String getSqlNotation() {
			return sqlNotation;
		}

	}

	protected FilterRequestMapper defaultMapper = new SqlMapper();
	
	@SuppressWarnings("rawtypes")
	protected Class queryingEntity;

	public abstract Type getType();

	public abstract void setType(Type t);
	
	public abstract String getName();
	
	public abstract void setName(String name);
	
	public abstract Operand getOperand();
	
	public abstract void setOperand(Operand o);
	
	@SuppressWarnings("rawtypes")
	public abstract List getValue();
	
	public abstract void setValue(List<? extends Object> value);

	public static FilterRequest fromString(String str) throws IOException {
		FilterRequest fr = om.readValue(Strings.decodeURIComponent(Strings.jsEscape(str)), FilterRequest.class);
		fr.setHash(str.hashCode());
		return fr;
	}
	
	public static FilterRequest lightFromString(String str) throws IOException {
		FilterRequest retval=om.readValue(str, FilterRequest.class);
		retval.setHash(str.hashCode());
		return retval;
	}

	/**
	 * Получить запрос для использования дальше в EntityProvider.
	 *
	 * @param possibleFields
	 *            Список полей, допустимых при создании запроса.
	 * @param mapper
	 *            Маппер для сбора самого запроса.
	 * @return
	 * @see EntityProvider
	 * @see FilterRequestMapper
	 */
	@JsonIgnore
	public abstract EntityQuery getQuery(List<String> possibleFields, FilterRequestMapper mapper);

	@JsonIgnore
	public EntityQuery getQuery() {
		List<String> possibleFields = new ArrayList<>();
		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(queryingEntity);
		for (PropertyDescriptor f : descriptors) {
			possibleFields.add(f.getName());
		}
		if (null != sort) {
			sort.check(possibleFields);
		}
		return this.getQuery(possibleFields, defaultMapper);
	}

	public void setRequestMapper(FilterRequestMapper mapper) {
		this.defaultMapper = mapper;
	}

	@SuppressWarnings("rawtypes")
	public void setQueryingEntity(Class c) {
		this.queryingEntity = c;
	}

	public void setSort(SortRequest sort) {
		this.sort = sort;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	protected void setHash(int hash) {
		this.hash = hash;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FilterRequest && o.toString().equals(this.toString())) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		try {
            return om.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

	@SuppressWarnings("rawtypes")
	public static FilterRequest buildByIdFilter(Integer id, Class entity) {
		NumberField retval = new NumberField();
		retval.setName("id");
		retval.setOperand(Operand.EQ);
		retval.setQueryingEntity(entity);
		List<Number> value=new ArrayList<>();
		value.add(id);
		retval.setValue(value);
		return retval;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static FilterRequest buildSimpleFilter(String field, Type type, Operand operand, Object value, Class queryingEntity) {
		FilterRequest ret=null;
		List vals=new ArrayList();
		if (value != null) {
			switch (type) {
			case DATE:
				ret=new DateField();
				break;
			case NUMBER:
				ret=new NumberField();
				break;
			case STRING:
				ret=new StringField();
				break;
			case BOOLEAN:
				ret=new BooleanField();
				break;
			default:
				throw new IllegalArgumentException("Data type " + type.toString() + " is not allowed here.");
			}
		} else {
			vals = null;
		}
		
		vals.add(value);
		ret.setName(field);
		ret.setValue(Arrays.asList(value));
		ret.setOperand(operand);
		
		ret.setQueryingEntity(queryingEntity);
		

		return ret;
	}

	public static FilterRequest addFilter(FilterRequest filter, String field, Type type, Operand operand, Object value) {
		String firstFilter = null;

		if (null != filter) {
			firstFilter = filter.toString();
		}

		String resultFilter;
		if (StringUtils.isEmpty(firstFilter)) {
			resultFilter = "{\"name\":\"%s\",\"type\":\"%s\",\"operand\":\"%s\",\"value\":[%s]}";
		} else {
			resultFilter = "{\"type\":\"group\",\"logical\":\"and\",\"fields\":[{\"name\":\"%s\",\"type\":\"%s\",\"operand\":\"%s\",\"value\":[%s]}," + firstFilter.replaceAll("%", "%%") + "]}";
		}

		resultFilter = String.format(resultFilter, field, type.toString().toLowerCase(), operand.toString().toLowerCase(), value);

		FilterRequest request = null;
		try {
			request = FilterRequest.lightFromString(resultFilter);
		} catch (IOException ignored) {
		}

		return request;
	}

}
