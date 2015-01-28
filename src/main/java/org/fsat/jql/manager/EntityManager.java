package org.fsat.jql.manager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fsat.jql.filter.FilterRequest;
import org.fsat.jql.filter.FilterRequest.Operand;
import org.fsat.jql.filter.FilterRequest.Type;
import org.fsat.jql.filter.annotation.MappedBy;
import org.fsat.jql.filter.annotation.Provides;
import org.fsat.jql.provider.EntityProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @since Jan 28, 2015
 **/
public class EntityManager implements ApplicationContextAware{

	
	final Logger log = LogManager.getLogger(this.getClass());

	protected ApplicationContext ctx;
	
	protected Map<String, List<EntityProvider>> candidatesCache;

	/**
	 * Заполнить список переданных полей сущности.
	 * 
	 * @param e
	 * @param requiredFields
	 * @return
	 */
	public <T> T fillEntity(T e, List<String> requiredFields) {
		for (String field : requiredFields) {
			e=loadField(e, field);
		}
		return e;
	}

	@SuppressWarnings("rawtypes")
	public <T> T loadField(T e, String field) {
		log.debug("Trying to fill field: {}", field);
		
		if (null != e) {
			EntityProvider provider = getProvider(e.getClass(), field);
			if (provider != null) {
				e = tryLoadField(e, field, provider);
			}
		}
		return e;
	}
	
	@SuppressWarnings("rawtypes")
	protected static Field getField(Class c, String name) {
		try {
			return c.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			if(c.getSuperclass()!=null) {
				return getField(c.getSuperclass(), name);
			}
			return null;
		} catch (SecurityException e) {
			return null;
		}
	}
	
	/**
	 * Определение типа одного поля, и загрузка его.
	 * 
	 * @param e
	 * @param field
	 * @param provider
	 * @return
	 */
	
	@SuppressWarnings("rawtypes")
	protected <T> T tryLoadField(T e, String field, EntityProvider provider) {
		try {
			// Определяем как искать связанную сущность. По дефолту связывается по айди, если не коллекция.
			Field f = getField(e.getClass(), field);
			String localField = field;
			String remoteField;
			Integer id;
			Class map = null;
			Class fieldType = Class.forName(f.getType().getTypeName());
			Object o = PropertyUtils.getSimpleProperty(e, field);
			
			if(alreadyLoaded(o)) {
				return e;
			}
			
			if (f.getAnnotation(MappedBy.class) != null) {
				// Если есть аннотация MappedBy считаем, что там указаны поля для связки
				map = f.getAnnotation(MappedBy.class).value();
				localField = f.getAnnotation(MappedBy.class).local();
				remoteField = f.getAnnotation(MappedBy.class).remote();
				id = (Integer) getFieldValue(localField, e);
			} else {
				// Иначе считаем, что надо построить простой byId запрос, и ID лежит внутри сущности, которую мы заполняем
				remoteField = "id";
				map = o.getClass();
				id = (Integer) PropertyUtils.getSimpleProperty(o, remoteField);
			}

			if (id != null) {
				// Айди был добыт. Имена полей выбраны и проставленны. Поехали.
				FilterRequest filter = FilterRequest.buildSimpleFilter(remoteField, Type.NUMBER, Operand.EQ, id, map);
				List loaded = wrappedFind(provider, filter);
				if (loaded != null) {
					if (List.class.isAssignableFrom(fieldType)) {
						// Вернулся лист, ожидали лист - просто устанавливаем
						PropertyUtils.setProperty(e, field, loaded);
					} else {
						// Ожидали единичное значение, а пришел лист. Просто берем первое.
						// TODO: А то ли это, что ожидалось?
						if (!loaded.isEmpty()) {
							PropertyUtils.setSimpleProperty(e, field, loaded.get(0));
						}
					}
				}
			} else {
				log.info("Unable to fill field {} for object {}", field, e.toString());
			}

		} catch (Exception ex) {
			log.info("Unable to fill field {} for object {}", field, e.toString(), ex);
		}
		return e;

	}
	
//	@Cacheable(value={"entities"}, key="#a0 + #a1.toString()")
	@SuppressWarnings("rawtypes")
	protected List wrappedFind(EntityProvider provider, FilterRequest request) throws Exception {
		return provider.find(request, 0, 1000);
	}
	
	@SuppressWarnings("rawtypes")
	protected Boolean alreadyLoaded(Object o) {
		if(o==null) {
			return false;
		}
		try {
			Object sample=o;
			if(Collection.class.isAssignableFrom(o.getClass())) {
				Collection c=(Collection) o;
				if(c.size()>0) {
					sample=c.iterator().next();
				}
			}
			Object isLoaded = PropertyUtils.getSimpleProperty(sample, "loaded");
			if(isLoaded!=null && Boolean.TRUE.equals(isLoaded)) {
				// Этот объект уже загружен из БД, не заморачиваемся.
				return true;
			}			
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
		}

		return false;
	}

	/**
	 * Получение значения поля. Поддерживает именование вида:
	 * account.id, что значит: надо взять поле account и в нем поле id. Метод
	 * рекурсивный.
	 * 
	 * @param fieldName
	 * @param o
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	protected Object getFieldValue(String fieldName, Object o) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (fieldName.contains(".")) {
			return getFieldValue(fieldName.substring(fieldName.indexOf(".")), o);
		}
		return PropertyUtils.getSimpleProperty(o, fieldName);
	}

	/**
	 * Поиск провайдера для необходимого объекта.
	 * Провайдер должен быть зарегистрирован у спринга, и иметь аннотацию
	 * Provides.
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> EntityProvider<T> getProvider(Class<T> type) {
		if(type==null) {
			return null;
		}
		
		log.debug("Searching provider for {}", null == type ? "null" : type.getTypeName());
		if(candidatesCache==null) {
			candidatesCache=new HashMap<>();
		}
		List<EntityProvider> candidates = candidatesCache.get(type.getCanonicalName());
		if(candidates==null || candidates.size()==0) {
			candidates=new ArrayList<>();
			Map<String, Object> beans = ctx.getBeansWithAnnotation(Provides.class);
			Iterator<String> it = beans.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				Object bean = beans.get(key);
				if (EntityProvider.class.isAssignableFrom(bean.getClass())) {
					Provides ann = ctx.findAnnotationOnBean(key, Provides.class);
					Class providedClass = ann.value();
					if (providedClass == type) {
						candidates.add((EntityProvider<T>) bean);
					}
				}
			}
	
			if (candidates.size() == 0) {
				return null;
			}
		}

		Random r = new SecureRandom();
		return candidates.get(r.nextInt(candidates.size()));
	}

	@SuppressWarnings("rawtypes")
	protected Class getFieldType(Class containingClass, String fieldName) throws SecurityException, ClassNotFoundException {
		Field f = getField(containingClass, fieldName);
		if(f==null) {
			log.debug("Unable to find field {} in class {}", fieldName, containingClass);
			return null;
		}
		Class type;
		if (f.getAnnotation(MappedBy.class) != null) {
			type = f.getAnnotation(MappedBy.class).value();
		} else {
			type = Class.forName(f.getGenericType().getTypeName());
		}
		return type;
	}
	
	/**
	 * Поиск провайдера для указанного поля сущности.
	 * 
	 * @param containingClass
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> EntityProvider<T> getProvider(Class<T> containingClass, String fieldName) {
		try {
			return getProvider(getFieldType(containingClass, fieldName));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
	}

	
}
