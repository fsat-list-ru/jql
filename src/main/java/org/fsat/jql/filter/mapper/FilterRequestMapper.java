package org.fsat.jql.filter.mapper;

import java.util.List;

import org.fsat.jql.filter.EntityQuery;
import org.fsat.jql.filter.FilterRequest;
import org.fsat.jql.filter.FilterRequest.Operand;

/**
 * @since Jan 28, 2015
 **/
public interface FilterRequestMapper {

	/**
	 * Преобразовать в строку логическое выражение.
	 * 
	 * @param t
	 *            Тип данных
	 * @param o
	 *            Логический оператор
	 * @param attributeCount
	 *            кол-во операндов.
	 * @return Строку в подходящем диалекте.
	 */
    public String convert(FilterRequest.Type t, FilterRequest.Operand o, int attributeCount);

	/**
	 * Собрать запрос верхнего уровня из набора подзапросов.
	 * 
	 * @param subQueries
	 *            Списрк подзапросов.
	 * @param logical
	 *            Логический оператор объединения
	 * @return Строку в соответствующем диалекте.
	 */
    public EntityQuery composeList(List<EntityQuery> subQueries,  FilterRequest.Logical logical);
    
    /**
     * Формирование логического выражения, если значение параметра null
     * @param o
     * @param attributeCount
     * @return
     */
    public String convertNull(FilterRequest.Operand o, int attributeCount);
    
    /**
     * Заворачивание значения в понятный вид.
     * В основном надо для like/ilike/not like выражений.
     * @param s
     * @param operand
     * @return
     */
    public String wrapValue(String s, Operand operand);

}
