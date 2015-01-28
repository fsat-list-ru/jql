package org.fsat.jql.provider;

import java.io.IOException;
import java.util.List;

import org.fsat.jql.filter.FilterRequest;

/**
 * @since Jan 28, 2015
 **/
public interface EntityProvider<T> {
	/**
	 * Поиск сущности.
	 * 
	 * @param request
	 *            Запрос для поиска.
	 * @return Список сущностей удовлетворяющих критерию.
	 * @throws IOException
	 * @throws ValidationFailedException 
	 * @throws NotAuthorizedException 
	 */
    public List<T> find(FilterRequest request, Integer first, Integer last) throws Exception;
    
    /**
     * Подсчет кол-ва сущностей.
     * @param request
     * @return
     * @throws Exception
     */
    public Integer count(FilterRequest request) throws Exception;

}
